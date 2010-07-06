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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.eclipse.virgo.snaps.core.AbstractEquinoxLaunchingTests;
import org.eclipse.virgo.snaps.core.internal.Host;
import org.eclipse.virgo.snaps.core.internal.Snap;
import org.eclipse.virgo.snaps.core.internal.SnapFactoryMonitor;
import org.eclipse.virgo.snaps.core.internal.deployer.SnapFactory;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.springframework.mock.web.MockServletContext;

import com.springsource.osgi.medic.test.eventlog.MockEventLogger;

public class SnapFactoryMonitorTests extends AbstractEquinoxLaunchingTests {

    private SnapFactoryMonitor binder;

    @Before
    public void setupBinder() {
        this.binder = new SnapFactoryMonitor(getBundleContext(), new MockEventLogger(), null);
        this.binder.start();
    }

    @Test
    public void testHostFirst() throws Exception {
        Bundle host = installBundle("travel_1");
        host.start();
        publishContextForBundle(host);

        Snap snap = createMock(Snap.class);
        snap.init();
        expect(snap.getSnapProperties()).andReturn(new Properties());
        expect(snap.getContextPath()).andReturn("/hotels").anyTimes();

        SnapFactory factory = createMock(SnapFactory.class);
        expect(factory.createSnap(isA(Host.class))).andReturn(snap);

        replay(factory, snap);
        publishFactory(factory, "travel", "[1.0, 2.0)");

        assertSnapPublished("/hotels", host);
        verify(factory, snap);

    }

    @Test
    public void testSnapFirst() throws Exception {
        Bundle host = installBundle("travel_1");
        host.start();

        Snap snap = createMock(Snap.class);
        snap.init();
        expect(snap.getSnapProperties()).andReturn(new Properties());
        expect(snap.getContextPath()).andReturn("/hotels").anyTimes();

        SnapFactory factory = createMock(SnapFactory.class);
        expect(factory.createSnap(isA(Host.class))).andReturn(snap);
        publishFactory(factory, "travel", "[1.0, 2.0)");

        replay(factory, snap);
        publishContextForBundle(host);

        assertSnapPublished("/hotels", host);
        verify(factory, snap);
    }

    @Test
    public void testNonHostService() {
        SnapFactory factory = createMock(SnapFactory.class);
        publishFactory(factory, "travel", "[1.0, 2.0)");
        replay(factory);
        getBundleContext().registerService(Object.class.getName(), new Object(), null);
        verify(factory);
    }

    @Test
    public void testManyHosts() throws Exception {
        Bundle host = installBundle("travel_1");
        Bundle host2 = installBundle("travel_2");
        Bundle host3 = installBundle("travel_3");

        host.start();
        host2.start();
        host3.start();

        publishContextForBundle(host);
        publishContextForBundle(host2);
        publishContextForBundle(host3);

        Snap snap = createMock(Snap.class);
        snap.init();
        expect(snap.getSnapProperties()).andReturn(new Properties());
        expect(snap.getContextPath()).andReturn("/hotels").anyTimes();

        SnapFactory factory = createMock(SnapFactory.class);
        expect(factory.createSnap(isA(Host.class))).andReturn(snap);
        replay(factory, snap);

        publishFactory(factory, "travel", "[1.0, 3.0)");

        assertSnapPublished("/hotels", host2);
        verify(factory, snap);
    }
    
    @Test
    public void testSnapInitFailed() throws Exception {
        Bundle host = installBundle("travel_1");
        host.start();

        Snap snap = createMock(Snap.class);
        expect(snap.getContextPath()).andReturn("/hotels").anyTimes();
        snap.init();
        expectLastCall().andThrow(new IllegalStateException());

        SnapFactory factory = createMock(SnapFactory.class);
        expect(factory.createSnap(isA(Host.class))).andReturn(snap);
        publishFactory(factory, "travel", "[1.0, 2.0)");

        replay(factory, snap);
        publishContextForBundle(host);
        Thread.sleep(1000); // no meaningful way to be notified of snap failure right now
        verify(factory, snap);
    }

    private ServiceRegistration publishFactory(SnapFactory factory, String hostName, String hostVersionRange) {
        Properties p = new Properties();
        p.setProperty(SnapFactory.FACTORY_NAME_PROPERTY, hostName);
        p.setProperty(SnapFactory.FACTORY_RANGE_PROPERTY, hostVersionRange);

        return getBundleContext().registerService(SnapFactory.class.getName(), factory, p);
    }

    private void publishContextForBundle(Bundle bundle) {
        ServletContext context = new MockServletContext();

        Properties p = new Properties();
        p.setProperty("osgi.web.symbolicname", bundle.getSymbolicName());
        p.setProperty("osgi.web.version", bundle.getVersion().toString());

        bundle.getBundleContext().registerService(ServletContext.class.getName(), context, p);
    }

    private void assertSnapPublished(String contextPath, Bundle host) throws Exception {
        String filter = String.format("(& (snap.host.id=%d) (snap.context.path=%s))", host.getBundleId(), contextPath);
        int count = 0;
        while (count++ < 10) {
            ServiceReference[] serviceReferences = getBundleContext().getServiceReferences(Snap.class.getName(), filter);
            if (serviceReferences != null) {
                return;
            } else {
                Thread.sleep(100);
            }
        }
        fail("Snap not published");
    }

    private Bundle installBundle(String name) throws BundleException {
        String path = "src/test/resources/test-bundles/shm/" + name;
        File f = new File(path);
        assertTrue("File: " + path + " does not exist", f.exists());
        return getBundleContext().installBundle("file:" + f.getAbsolutePath());
    }
}
