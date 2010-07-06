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

package org.eclipse.virgo.snaps.core;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.eclipse.virgo.snaps.core.OsgiSnapRegistry;
import org.eclipse.virgo.snaps.core.internal.Snap;
import org.junit.Test;


public class OsgiSnapRegistryTests extends AbstractEquinoxLaunchingTests {

    @Test
    public void testPublishSnap() throws Exception {
        Snap snap = createMock(Snap.class);

        OsgiSnapRegistry registry = new OsgiSnapRegistry(getBundleContext());
        registry.init();
        assertNull(registry.findSnapByContextPath("/hotels"));

        Properties p = new Properties();
        p.put("snap.context.path", "/hotels");
        p.put("snap.host.id", getBundleContext().getBundle().getBundleId());

        getBundleContext().registerService(Snap.class.getName(), snap, p);

        assertEquals(snap, registry.findSnapByContextPath("/hotels"));
        registry.destroy();
    }

    @Test
    public void testNonClashPublishSnap() throws Exception {
        Snap snap = createMock(Snap.class);

        OsgiSnapRegistry registry = new OsgiSnapRegistry(getBundleContext());
        registry.init();
        assertNull(registry.findSnapByContextPath("/hotels"));

        Properties p = new Properties();
        p.put("snap.context.path", "/hotels");
        p.put("snap.host.id", getBundleContext().getBundle().getBundleId() + 1);

        getBundleContext().registerService(Snap.class.getName(), snap, p);

        assertNull(registry.findSnapByContextPath("/hotels"));
        registry.destroy();
    }
}
