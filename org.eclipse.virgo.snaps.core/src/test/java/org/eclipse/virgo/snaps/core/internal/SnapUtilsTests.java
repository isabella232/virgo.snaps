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
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.eclipse.virgo.snaps.core.internal.SnapHostDefinition;
import org.eclipse.virgo.snaps.core.internal.SnapUtils;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.springframework.mock.web.MockHttpServletRequest;

import com.springsource.util.osgi.manifest.BundleManifest;

public class SnapUtilsTests {

    @Test
    public void determineSnapContextPathFromDeepPathInfo() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/hotels/booking/index");
        String contextPath = SnapUtils.determineSnapContextPath(request);
        assertEquals("/hotels", contextPath);
    }
    
    @Test
    public void determineSnapContextPathFromPathInfo() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/hotels/booking");
        String contextPath = SnapUtils.determineSnapContextPath(request);
        assertEquals("/hotels", contextPath);
    }
    
    @Test
    public void determineSnapContextPathFromStraightPathInfo() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/hotels");
        String contextPath = SnapUtils.determineSnapContextPath(request);
        assertEquals("/hotels", contextPath);
    }
    
    @Test
    public void hasSnapHostHeaderTrue() {
        BundleManifest manifest = createMock(BundleManifest.class);
        expect(manifest.getHeader(SnapUtils.HEADER_SLICE_HOST)).andReturn("foo");
        
        replay(manifest);
        assertTrue(SnapUtils.hasSnapHostHeader(manifest));
        verify(manifest);
    }
    
    @Test
    public void hasSnapHostHeaderFalse() {
        BundleManifest manifest = createMock(BundleManifest.class);
        expect(manifest.getHeader(SnapUtils.HEADER_SLICE_HOST)).andReturn(null);
        
        replay(manifest);
        assertFalse(SnapUtils.hasSnapHostHeader(manifest));
        verify(manifest);
    }
    
    @Test
    public void getSnapHostHeader() {
        BundleManifest manifest = createMock(BundleManifest.class);
        expect(manifest.getHeader(SnapUtils.HEADER_SLICE_HOST)).andReturn("travel;version=\"[1.2, 1.3)\"");
        
        replay(manifest);
        SnapHostDefinition header = SnapUtils.getSnapHostHeader(manifest);
        assertNotNull(header);
        assertEquals("travel", header.getSymbolicName());
        verify(manifest);
    }
    
    @Test
    public void getSnapHostHeaderNull() {
        BundleManifest manifest = createMock(BundleManifest.class);
        expect(manifest.getHeader(SnapUtils.HEADER_SLICE_HOST)).andReturn(null);
        
        replay(manifest);
        SnapHostDefinition header = SnapUtils.getSnapHostHeader(manifest);
        assertNull(header);
        verify(manifest);
    }
    
    @Test
    public void getSnapContextPath() {
        Properties p = new Properties();
        p.setProperty(SnapUtils.HEADER_SLICE_CONTEXT_PATH, "snap");
        
        Bundle bundle = createMock(Bundle.class);
        expect(bundle.getHeaders()).andReturn(p);
        
        replay(bundle);
        assertEquals("snap", SnapUtils.getSnapContextPath(bundle));
        verify(bundle);
    }
    
    @Test
    public void getDefaultSnapContextPath() {
        Properties p = new Properties();
        
        Bundle bundle = createMock(Bundle.class);
        expect(bundle.getHeaders()).andReturn(p);
        expect(bundle.getLocation()).andReturn("file:../foo/mysnap.jar");
        
        replay(bundle);
        assertEquals("mysnap", SnapUtils.getSnapContextPath(bundle));
        verify(bundle);
    }
}
