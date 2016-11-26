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
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.eclipse.virgo.medic.test.eventlog.MockEventLogger;
import org.eclipse.virgo.snaps.core.AbstractEquinoxLaunchingTests;
import org.eclipse.virgo.snaps.core.internal.deployer.SnapFactory;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.springframework.mock.web.MockServletContext;

public class SnapFactoryMonitorTests extends AbstractEquinoxLaunchingTests {

    private SnapFactoryMonitor binder;
    
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static final Executor executor = Executors.newFixedThreadPool(2); 

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
        expect(factory.hasSnap()).andReturn(Boolean.FALSE);

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
        expect(factory.hasSnap()).andReturn(Boolean.FALSE);
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
        expect(factory.hasSnap()).andReturn(Boolean.FALSE);
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
        expect(factory.hasSnap()).andReturn(Boolean.FALSE);
        publishFactory(factory, "travel", "[1.0, 2.0)");

        replay(factory, snap);
        publishContextForBundle(host);
        Thread.sleep(1000); // no meaningful way to be notified of snap failure right now
        verify(factory, snap);
    }

    private ServiceRegistration<SnapFactory> publishFactory(SnapFactory factory, String hostName, String hostVersionRange) {
        Dictionary<String, String> p = new Hashtable<String, String>();
        p.put(SnapFactory.FACTORY_NAME_PROPERTY, hostName);
        p.put(SnapFactory.FACTORY_RANGE_PROPERTY, hostVersionRange);

        return getBundleContext().registerService(SnapFactory.class, factory, p);
    }

    private void publishContextForBundle(Bundle bundle) {
        ServletContext context = new MockServletContext();

        Dictionary<String, String> p = new Hashtable<String, String>();
        p.put("osgi.web.symbolicname", bundle.getSymbolicName());
        p.put("osgi.web.version", bundle.getVersion().toString());

        bundle.getBundleContext().registerService(ServletContext.class, context, p);
    }

    private void assertSnapPublished(String contextPath, Bundle host) throws Exception {
        String filter = String.format("(& (snap.host.id=%d) (snap.context.path=%s))", host.getBundleId(), contextPath);
        int count = 0;
        while (count++ < 10) {
            Collection<ServiceReference<Snap>> serviceReferences = getBundleContext().getServiceReferences(Snap.class, filter);
            if (serviceReferences != null && !serviceReferences.isEmpty()) {
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
    
    
    /**
     * Test binding of snap to a host if snap started prior to the host and if there are many hosts out there vs. just the target host (singular)
     * @throws Exception
     */
    @Test
    public void testManyDifferentHosts() throws Exception {
        final Bundle host = installBundle("travel_1");
        final Bundle host2 = installBundle("travel_2");
        final Bundle host3 = installBundle("travel_3");
        final Bundle targetHost = installBundle("clinic_1");

        // these will be picked up by "already published" logic
        host2.start();
        host3.start();
       
        publishContextForBundle(host2);
        publishContextForBundle(host3);

        Snap slice = createMock(Snap.class);
        slice.init();
        expect(slice.getSnapProperties()).andReturn(new Properties());
        expect(slice.getContextPath()).andReturn("/hotels").anyTimes();

        final SnapFactory factory = createMock(SnapFactory.class);
        expect(factory.createSnap(isA(Host.class))).andReturn(slice);
        expect(factory.hasSnap()).andReturn(Boolean.FALSE);
        replay(factory, slice);

        // public snap factory bound to "clinic" host
        executor.execute(new Runnable() {
            public void run() {
                publishFactory(factory, "clinic", "[1.0, 3.0)");
            }
        });
        
        
        // need to wait for sec or so to let all services propagate.
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (Exception ex) {
            // ignore
        }
        
        // spin a thread so it does not block
        // start host 1
        executor.execute(new Runnable() {
            
            public void run() {
                try {
                    host.start();
                } catch (BundleException e) {
                    throw new RuntimeException(e);
                }
                publishContextForBundle(host);
            }
        });
        
        
        // need to wait for few sec to let all services propagate.
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (Exception ex) {
            // ignore
        }
        
        // start host 2 (the actual one we are interested in)
        executor.execute(new Runnable() {
            
            public void run() {
                try {
                    targetHost.start();
                } catch (BundleException e) {
                    throw new RuntimeException(e);
                }
                publishContextForBundle(targetHost);
            }
        });
        

        assertSnapPublished("/hotels", targetHost);
        verify(factory, slice);
    }
}
