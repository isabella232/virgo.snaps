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

package org.eclipse.virgo.snaps.core.internal.webapp.container;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.virgo.snaps.core.internal.webapp.SnapServletContext;
import org.eclipse.virgo.snaps.core.internal.webapp.container.SnapHttpSession;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;



public class SnapHttpServletTests {

    private MockHttpSession parentSession;
    private SnapHttpSession snapSession;
    
    @Before
    public void before() {
        MockServletContext parentContext = new MockServletContext();
        Bundle bundle = createMock(Bundle.class);
        SnapServletContext context = new SnapServletContext(parentContext, bundle, "/contextPath");

        
        this.parentSession = new MockHttpSession();
        this.snapSession = new SnapHttpSession(parentSession, context);
    }
    @Test
    public void testSetAndGet() {
        this.snapSession.setAttribute("foo", "bar");
        assertEquals("bar", this.snapSession.getAttribute("foo"));
        assertNull(this.parentSession.getAttribute("foo"));
    }
    
    @Test
    public void testReadThroughToParent() {
        this.parentSession.setAttribute("foo", "bar");
        assertEquals("bar", this.snapSession.getAttribute("foo"));
    }
    
    @Test
    public void testGetAttributeNames() {
        this.snapSession.setAttribute("foo", "bar");
        this.parentSession.setAttribute("bar", "foo");
        Enumeration<?> attributeNames = this.snapSession.getAttributeNames();
        Set<String> names = new HashSet<String>();
        while(attributeNames.hasMoreElements()) {
            names.add((String)attributeNames.nextElement());
        }
        
        assertTrue(names.contains("foo"));
        assertTrue(names.contains("bar"));
    }
}
