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

import java.util.Dictionary;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.eclipse.virgo.snaps.core.RequestRouter;
import org.eclipse.virgo.snaps.core.SnapRegistry;
import org.eclipse.virgo.snaps.core.internal.deployer.SnapFactory;
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

import com.springsource.osgi.medic.eventlog.EventLogger;
import com.springsource.util.osgi.ServiceRegistrationTracker;

final class SnapFactoryMonitor implements ServiceTrackerCustomizer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BundleContext bundleContext;

    private final ServiceTracker snapFactoryTracker;

    private final EventLogger eventLogger;

    private final SnapRegistry snapRegistry;

    public SnapFactoryMonitor(BundleContext bundleContext, EventLogger eventLogger, SnapRegistry snapRegistry) {
        this.bundleContext = bundleContext;
        this.snapFactoryTracker = new ServiceTracker(bundleContext, SnapFactory.class.getName(), this);
        this.eventLogger = eventLogger;
        this.snapRegistry = snapRegistry;
    }

    public void start() {
        this.snapFactoryTracker.open();
    }

    public void stop() {
        this.snapFactoryTracker.close();
    }

    public Object addingService(ServiceReference reference) {
        SnapFactory snapFactory = (SnapFactory) this.bundleContext.getService(reference);
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

    public void modifiedService(ServiceReference reference, Object service) {
    }

    public void removedService(ServiceReference reference, Object service) {
        logger.info("Destroying SnapBinder for bundle '{}'", reference.getBundle());
        ((SnapBinder) service).destroy();
    }

    private static enum SnapLifecycleState {
    	AWAITING_INIT,
    	INIT_SUCCEEDED,
    	INIT_FAILED
    }
    
    private static final class SnapBinder implements ServiceListener {

        private static final String SLICE_ORDER = "snap.order";

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final BundleContext context;

        private final SnapFactory factory;

        private final HostSelector hostSelector;

        private final Object hostStateMonitor = new Object();

        private final Object snapStateMonitor = new Object();

        private boolean queriedInitialHosts = false;

        private ServiceReference hostReference;

        private final ServiceRegistrationTracker registrationTracker = new ServiceRegistrationTracker();

        private final EventLogger eventLogger;

        private final SnapRegistry snapRegistry;

        private Snap snap;                

        public SnapBinder(BundleContext context, SnapFactory factory, SnapHostDefinition hostDefinition, EventLogger eventLogger,
            SnapRegistry snapRegistry) {
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

        private void hostPublished(ServiceReference hostReference) {
            assert (!Thread.holdsLock(this.hostStateMonitor));

            ServletContext servletContext = (ServletContext) this.context.getService(hostReference);
            if (servletContext != null) {
                synchronized (this.hostStateMonitor) {
                    this.hostReference = hostReference;
                }
                Bundle hostBundle = hostReference.getBundle();


                SnapLifecycleState newState = SnapLifecycleState.INIT_FAILED;

                Snap snap = this.factory.createSnap(new Host(hostBundle, servletContext, new RequestRouter(this.snapRegistry, servletContext)));
                try {
                    logger.info("Initializing snap '{}'", snap.getContextPath());
                    snap.init();
                    
                    newState = SnapLifecycleState.INIT_SUCCEEDED;
                    
                    logger.info("Publishing snap '{}'", snap.getContextPath());
                    publishSnapService(snap, hostBundle);
                    
                } catch (ServletException e) {
                    this.eventLogger.log(SnapsLogEvents.SLICE_INIT_FAILURE, SnapUtils.boundContextPath(servletContext.getContextPath(), snap.getContextPath()));
                } finally {
                	synchronized (this.snapStateMonitor) {                		
						if (newState == SnapLifecycleState.INIT_SUCCEEDED) {
							this.snap = snap;
						}
                	}                	
                }

            }
        }

        @SuppressWarnings("unchecked")
        private void publishSnapService(Snap snap, Bundle hostBundle) {
            Dictionary serviceProperties = snap.getSnapProperties();

            String snapOrder = (String) serviceProperties.get(SLICE_ORDER);
            if (snapOrder != null) {
                serviceProperties.put(Constants.SERVICE_RANKING, Integer.parseInt(snapOrder));
            }
            serviceProperties.put("snap.host.id", Long.toString(hostBundle.getBundleId()));
            serviceProperties.put("snap.context.path", snap.getContextPath());
            serviceProperties.put("snap.name", (String) this.context.getBundle().getHeaders().get("Bundle-Name"));

            ServiceRegistration registration = this.context.registerService(Snap.class.getName(), snap, serviceProperties);
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
            this.context.removeServiceListener(this);
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
            ServiceReference serviceReference = event.getServiceReference();

            if (type == ServiceEvent.REGISTERED && this.hostReference == null) {
                hostPublished(serviceReference);
            } else if (type == ServiceEvent.UNREGISTERING) {
                if (serviceReference.equals(this.hostReference)) {
                    hostRetracted(serviceReference);
                }
            }
        }

        private void hostRetracted(ServiceReference serviceReference) {
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
            ServiceReference existingHost = null;
            ServiceReference[] candidates = findHostCandidiates();
            if (candidates != null) {
                logger.info("{} host candidates found", candidates.length);
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

        private ServiceReference[] findHostCandidiates() {
            try {
                return this.context.getServiceReferences(ServletContext.class.getName(), null);
            } catch (InvalidSyntaxException ise) {
                throw new IllegalStateException("Unexpected invalid filter syntax with null filter", ise);
            }
        }
    }
}
