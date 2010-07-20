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

package org.eclipse.virgo.snaps.core.internal.webapp;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.eclipse.virgo.snaps.core.internal.webapp.SnapServletContext;
import org.junit.Test;
import org.osgi.framework.Bundle;

import org.eclipse.virgo.util.common.IterableEnumeration;

public class SnapServletContextTests {
    
    private ServletContext delegate = createMock(ServletContext.class);
    
    private Bundle snapBundle = createMock(Bundle.class);
    
    private String snapContextPath = "/snap";
    
    private SnapServletContext servletContext = new SnapServletContext(delegate, snapBundle, snapContextPath);
    
    @Test
    public void attributeFromSnap() {
        replay(this.delegate, this.snapBundle);
        this.servletContext.setAttribute("foo", "bar");
        assertEquals("bar", this.servletContext.getAttribute("foo"));
        verify(this.delegate, this.snapBundle);
    }
    
    @Test
    public void attributeFromDelegate() {
        expect(this.delegate.getAttribute("bar")).andReturn("foo");
        replay(this.delegate, this.snapBundle);
        assertEquals("foo", this.servletContext.getAttribute("bar"));
        verify(this.delegate, this.snapBundle);
    }
    
    @Test 
    @SuppressWarnings("unchecked")
    public void attributeNames() {
        Vector<String> delegateAttributeNames = new Vector<String>();
        delegateAttributeNames.add("c");
        delegateAttributeNames.add("d");
        
        expect(this.delegate.getAttributeNames()).andReturn(delegateAttributeNames.elements());
        replay(this.delegate, this.snapBundle);
        
        this.servletContext.setAttribute("a", new Object());
        this.servletContext.setAttribute("b", new Object());
        this.servletContext.setAttribute("c", new Object());
        
        List<String> attributeNames = toList((Enumeration<String>)this.servletContext.getAttributeNames());
        
        assertEquals(4, attributeNames.size());
        
        assertTrue(attributeNames.contains("a"));
        assertTrue(attributeNames.contains("b"));
        assertTrue(attributeNames.contains("c"));
        assertTrue(attributeNames.contains("d"));
        
        verify(this.delegate, this.snapBundle);        
    }
    
    private static <T> List<T> toList(Enumeration<T> enumeration) {
        List<T> list = new ArrayList<T>();
        IterableEnumeration<T> iterableEnumeration = new IterableEnumeration<T>(enumeration);
        for (T t : iterableEnumeration) {
            list.add(t);
        }
        return list;
    }
}
