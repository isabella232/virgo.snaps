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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import org.eclipse.virgo.snaps.core.internal.deployer.SnapFactory;
import org.eclipse.virgo.snaps.core.internal.deployer.SnapLifecycleListener;
import org.junit.Test;
import org.osgi.framework.Version;

import org.eclipse.virgo.kernel.deployer.core.DeploymentException;
import org.eclipse.virgo.kernel.install.artifact.BundleInstallArtifact;
import org.eclipse.virgo.medic.test.eventlog.MockEventLogger;
import org.eclipse.virgo.teststubs.osgi.framework.OSGiAssert;
import org.eclipse.virgo.teststubs.osgi.framework.StubBundle;
import org.eclipse.virgo.teststubs.osgi.framework.StubBundleContext;
import org.eclipse.gemini.web.tomcat.spi.WebBundleClassLoaderFactory;
import org.eclipse.virgo.util.osgi.manifest.BundleManifest;
import org.eclipse.virgo.util.osgi.manifest.internal.StandardBundleManifest;

public class SnapLifecycleListenerTests {

    private WebBundleClassLoaderFactory classLoaderFactory = createMock(WebBundleClassLoaderFactory.class);

    private SnapLifecycleListener lifecycleListener = new SnapLifecycleListener(classLoaderFactory, new MockEventLogger());

    private final StubBundle bundle = new StubBundle("foo", new Version(1, 0, 0));

    private final StubBundleContext bundleContext = (StubBundleContext) bundle.getBundleContext();

    private final BundleManifest bundleManifest = new StandardBundleManifest(null);

    @Test
    public void standardLifecycle() throws IOException, DeploymentException {
        BundleInstallArtifact installArtifact = createMock(BundleInstallArtifact.class);
        bundleManifest.setHeader("Snap-Host", "myHost");
        expect(installArtifact.getBundleManifest()).andReturn(bundleManifest).anyTimes();
        expect(installArtifact.getBundle()).andReturn(bundle);
        expect(installArtifact.getName()).andReturn("mySnap");

        replay(installArtifact, classLoaderFactory);

        lifecycleListener.onStarted(installArtifact);

        OSGiAssert.assertServiceRegistrationCount(this.bundleContext, SnapFactory.class, 1);

        lifecycleListener.onStopping(installArtifact);

        OSGiAssert.assertServiceRegistrationCount(this.bundleContext, SnapFactory.class, 0);

        verify(installArtifact, classLoaderFactory);
    }
}
