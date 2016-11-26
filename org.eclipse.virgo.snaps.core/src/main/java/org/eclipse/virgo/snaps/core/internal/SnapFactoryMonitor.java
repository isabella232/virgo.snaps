/*******************************************************************************
 * Copyright (c) 2008, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   VMware Inc. - initial contribution
 *******************************************************************************/

package org.eclipse.virgo.snaps.core.internal;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.eclipse.virgo.medic.eventlog.EventLogger;
import org.eclipse.virgo.snaps.core.RequestRouter;
import org.eclipse.virgo.snaps.core.internal.Snap;
import org.eclipse.virgo.snaps.core.SnapRegistry;
import org.eclipse.virgo.snaps.core.internal.deployer.SnapFactory;
import org.eclipse.virgo.util.osgi.ServiceRegistrationTracker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SnapFactoryMonitor implements ServiceTrackerCustomizer<SnapFactory, Object> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BundleContext bundleContext;

    private final ServiceTracker<SnapFactory, Object> snapFactoryTracker;

    private final EventLogger eventLogger;

    private final SnapRegistry snapRegistry;

    public SnapFactoryMonitor(BundleContext bundleContext, EventLogger eventLogger, SnapRegistry snapRegistry) {
        this.bundleContext = bundleContext;
        this.snapFactoryTracker = new ServiceTracker<SnapFactory, Object>(bundleContext, SnapFactory.class, this);
        this.eventLogger = eventLogger;
        this.snapRegistry = snapRegistry;
    }

    public void start() {
        this.snapFactoryTracker.open();
    }

    public void stop() {
        this.snapFactoryTracker.close();
    }

    public Object addingService(ServiceReference<SnapFactory> reference) {
        SnapFactory snapFactory = this.bundleContext.getService(reference);
        if (snapFactory != null) {
            BundleContext snapBundleContext = reference.getBundle().getBundleContext();
            SnapBinder snapBinder = new SnapBinder(snapBundleContext, snapFactory, SnapHostDefinition.fromServiceReference(reference),
                this.eventLogger, this.snapRegistry);
            snapBinder.start();
            return snapBinder;
        }
        logger.warn("Unable to create SnapBinder due to missing SnapFactory");
        return null;
    }

    public void modifiedService(ServiceReference<SnapFactory> reference, Object service) {
    }

    public void removedService(ServiceReference<SnapFactory> reference, Object service) {
        logger.info("Destroying SnapBinder for bundle '{}'", reference.getBundle());
        ((SnapBinder) service).destroy();
    }

    private static enum SnapLifecycleState {
    	AWAITING_INIT,
    	INIT_SUCCEEDED,
    	INIT_FAILED
    }
    
    private static final class SnapBinder implements ServiceListener {

        private static final String SNAP_ORDER = "snap.order";

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final BundleContext context;

        private final SnapFactory factory;

        private final HostSelector hostSelector;

        private final Object hostStateMonitor = new Object();

        private final Object snapStateMonitor = new Object();

        private boolean queriedInitialHosts = false;

        private ServiceReference<ServletContext> hostReference;

        private final ServiceRegistrationTracker registrationTracker = new ServiceRegistrationTracker();

        private final EventLogger eventLogger;

        private final SnapRegistry snapRegistry;

        private Snap snap;                

        public SnapBinder(final BundleContext context, final SnapFactory factory, final SnapHostDefinition hostDefinition, final EventLogger eventLogger, final SnapRegistry snapRegistry) {
            this.context = context;
            this.factory = factory;
            this.hostSelector = new HostSelector(hostDefinition, (String)context.getBundle().getHeaders().get("Module-Scope"));
            this.eventLogger = eventLogger;
            this.snapRegistry = snapRegistry;
        }

        private void start() {
            registerHostListener();
        }

        private void registerHostListener() {
            try {
                this.context.addServiceListener(this, "(objectClass=javax.servlet.ServletContext)");
                logger.info("Listening for hosts to be registered.");
                searchForExistingHost();
            } catch (InvalidSyntaxException e) {
                logger.error("Filter syntax invalid");
            }
        }

        private void hostPublished(ServiceReference<ServletContext> hostReference) {
            assert (!Thread.holdsLock(this.hostStateMonitor));

            ServletContext servletContext = this.context.getService(hostReference);
            if (servletContext != null) {
                synchronized (this.hostStateMonitor) {
                    
                	Collection<ServiceReference<ServletContext>> references = new HashSet<ServiceReference<ServletContext>>();
                	references.add(hostReference);
                    ServiceReference<ServletContext> matchedHost = this.hostSelector.selectHost(references);
                    
                    if (matchedHost == null) {
                        logger.info("Host {} did not match {} ", hostReference.getBundle().getSymbolicName(),
                            this.hostSelector.getHostDefinition().toString());
                        return;
                    }
                }
                
                Bundle hostBundle = hostReference.getBundle();

                Host host = new Host(hostBundle, servletContext, new RequestRouter(this.snapRegistry, servletContext));
                
                synchronized (this.snapStateMonitor) {
                	if (this.factory.hasSnap()) {
                		Snap snap = this.factory.getSnap();
                		snap.addHost(host);
                		publishSnapService(snap, hostBundle);
                	} else {
                		SnapLifecycleState newState = SnapLifecycleState.INIT_FAILED;
                		Snap snap = this.factory.createSnap(host);
                		try {
                			logger.info("Initializing snap '{}'", snap.getContextPath());
                			snap.init();
                			
                			newState = SnapLifecycleState.INIT_SUCCEEDED;
                			
                			logger.info("Publishing snap '{}'", snap.getContextPath());
                			publishSnapService(snap, hostBundle);
                		} catch (ServletException e) {
                			this.eventLogger.log(SnapsLogEvents.SNAP_INIT_FAILURE, SnapUtils.boundContextPath(servletContext.getContextPath(), snap.getContextPath()));
                		} finally {
                			if (newState == SnapLifecycleState.INIT_SUCCEEDED) {
                				this.snap = snap;
                			}
                		}
					}
                }
            }
        }

        private void publishSnapService(Snap snap, Bundle hostBundle) {
        	Hashtable<Object, Object> props = snap.getSnapProperties();
            Dictionary<String, Object> serviceProperties = new Hashtable<String, Object>();
            
        	for(Object key : props.keySet()){
        		serviceProperties.put(key.toString(), props.get(key));
        	}
        	            
            String snapOrder = (String) serviceProperties.get(SNAP_ORDER);
            if (snapOrder != null) {
                serviceProperties.put(Constants.SERVICE_RANKING, Integer.parseInt(snapOrder));
            }
            serviceProperties.put("snap.host.id", Long.toString(hostBundle.getBundleId()));
            serviceProperties.put("snap.context.path", snap.getContextPath());
            serviceProperties.put("snap.name", (String) this.context.getBundle().getHeaders().get("Bundle-Name"));

            ServiceRegistration<Snap> registration = this.context.registerService(Snap.class, snap, serviceProperties);
            this.registrationTracker.track(registration);
            logger.info("Published snap service for '{}'", snap.getContextPath());
        }

        private void destroy() {
            try {
                destroySnap();
            } finally {
                unregisterHostListener();
            }
        }

        private void unregisterHostListener() {
            logger.info("No longer listening for hosts to be registered.");
            try {
				this.context.removeServiceListener(this);
			} catch (IllegalStateException e) {
				logger.warn("Could not remove host listener. Reason: " + e.getMessage());
			}
        }

		public void serviceChanged(ServiceEvent event) {
            synchronized (this.hostStateMonitor) {
                while (!queriedInitialHosts) {
                    try {
                        this.hostStateMonitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            int type = event.getType();
            @SuppressWarnings("unchecked")
			ServiceReference<ServletContext> serviceReference = (ServiceReference<ServletContext>) event.getServiceReference();

            if (type == ServiceEvent.REGISTERED && this.hostReference == null) {
                hostPublished(serviceReference);
            } else if (type == ServiceEvent.UNREGISTERING) {
                if (serviceReference.equals(this.hostReference)) {
                    hostRetracted(serviceReference);
                }
            }
        }

        private void hostRetracted(ServiceReference<ServletContext> serviceReference) {
            try {
                destroySnap();
            } finally {
                synchronized (this.hostStateMonitor) {
                    this.hostReference = null;
                }
            }
        }

        private void destroySnap() {
            Snap s = null;
            synchronized (this.snapStateMonitor) {                
                s = this.snap;
                this.snap = null;
            }
            this.registrationTracker.unregisterAll();
            if(s != null) {
            logger.info("Retracted snap service for '{}'", s.getContextPath());
            	s.destroy();
            }
        }

        private void searchForExistingHost() {
            ServiceReference<ServletContext> existingHost = null;
            Collection<ServiceReference<ServletContext>> candidates = findHostCandidiates();
            if (candidates != null && !candidates.isEmpty()) {
                logger.info("{} host candidates found", candidates.size());
            } else {
                logger.info("No host candidates found");
            }

            synchronized (this.hostStateMonitor) {
                try {
                    existingHost = this.hostSelector.selectHost(candidates);
                    this.queriedInitialHosts = true;
                } finally {
                    this.hostStateMonitor.notifyAll();
                }
            }
            if (existingHost != null) {
                hostPublished(existingHost);
            }
        }

        private Collection<ServiceReference<ServletContext>> findHostCandidiates() {
            try {
                return this.context.getServiceReferences(ServletContext.class, null);
            } catch (InvalidSyntaxException ise) {
                throw new IllegalStateException("Unexpected invalid filter syntax with null filter", ise);
            }
        }
    }
}
