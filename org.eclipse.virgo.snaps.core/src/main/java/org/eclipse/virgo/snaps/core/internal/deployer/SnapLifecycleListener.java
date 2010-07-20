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

package org.eclipse.virgo.snaps.core.internal.deployer;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.virgo.snaps.core.internal.SnapHostDefinition;
import org.eclipse.virgo.snaps.core.internal.SnapUtils;
import org.eclipse.virgo.snaps.core.internal.webapp.WebAppSnapFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.virgo.kernel.deployer.core.DeploymentException;
import org.eclipse.virgo.kernel.install.artifact.BundleInstallArtifact;
import org.eclipse.virgo.kernel.install.artifact.InstallArtifact;
import org.eclipse.virgo.kernel.install.artifact.InstallArtifactLifecycleListenerSupport;
import org.eclipse.virgo.medic.eventlog.EventLogger;
import org.eclipse.gemini.web.tomcat.spi.WebBundleClassLoaderFactory;
import org.eclipse.virgo.util.osgi.ServiceRegistrationTracker;
import org.eclipse.virgo.util.osgi.manifest.BundleManifest;

/**
 * <strong>Concurrent Semantics</strong><br />
 * Thread-safe.
 * 
 */
final class SnapLifecycleListener extends InstallArtifactLifecycleListenerSupport {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<InstallArtifact, ServiceRegistrationTracker> registrationTrackers = new ConcurrentHashMap<InstallArtifact, ServiceRegistrationTracker>();

    private final WebBundleClassLoaderFactory classLoaderFactory;

    private final EventLogger eventLogger;

    public SnapLifecycleListener(WebBundleClassLoaderFactory classLoaderFactory, EventLogger eventLogger) {
        this.classLoaderFactory = classLoaderFactory;
        this.eventLogger = eventLogger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStarted(InstallArtifact installArtifact) throws DeploymentException {
        if (isSnap(installArtifact)) {
            Bundle bundle = ((BundleInstallArtifact) installArtifact).getBundle();
            BundleManifest bundleManifest = getBundleManifest((BundleInstallArtifact) installArtifact);

            ServiceRegistration registration = createAndRegisterSnapFactoryService(bundle, bundleManifest);

            ServiceRegistrationTracker registrationTracker = new ServiceRegistrationTracker();
            registrationTracker.track(registration);

            this.registrationTrackers.put(installArtifact, registrationTracker);
        }
    }

    ServiceRegistration createAndRegisterSnapFactoryService(Bundle bundle, BundleManifest bundleManifest) {
        logger.info("Creating a SnapFactory for bundle '{}'", bundle);
        SnapFactory snapFactory = new WebAppSnapFactory(bundle, this.classLoaderFactory, this.eventLogger);

        SnapHostDefinition hostDefinition = SnapUtils.getSnapHostHeader(bundleManifest);

        Properties serviceProperties = new Properties();
        serviceProperties.put(SnapFactory.FACTORY_NAME_PROPERTY, hostDefinition.getSymbolicName());
        serviceProperties.put(SnapFactory.FACTORY_RANGE_PROPERTY, hostDefinition.getVersionRange().toParseString());

        ServiceRegistration registration = bundle.getBundleContext().registerService(SnapFactory.class.getName(), snapFactory, serviceProperties);
        return registration;
    }

    static BundleManifest getBundleManifest(BundleInstallArtifact bundleInstallArtifact) throws DeploymentException {
        try {
            return bundleInstallArtifact.getBundleManifest();
        } catch (IOException ioe) {
            throw new DeploymentException("Failed to get bundle manifest from '" + bundleInstallArtifact + "'", ioe);
        }
    }

    static boolean isSnap(InstallArtifact installArtifact) throws DeploymentException {
        if (installArtifact instanceof BundleInstallArtifact) {
            return SnapUtils.hasSnapHostHeader(getBundleManifest((BundleInstallArtifact) installArtifact));
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStopping(InstallArtifact installArtifact) {
        logger.info("Destroying SnapFactory for bundle '{}'", installArtifact.getName());
        ServiceRegistrationTracker serviceRegistrationTracker = this.registrationTrackers.remove(installArtifact);
        if (serviceRegistrationTracker != null) {
            serviceRegistrationTracker.unregisterAll();
        }
    }
}
