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

package org.eclipse.virgo.snaps;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.eclipse.virgo.snaps.Snap;
import org.eclipse.virgo.snaps.SnapsTag;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.mock.web.MockPageContext;

import org.eclipse.gemini.web.core.WebContainer;

/**
 * TODO Document SnapsTagTests
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 *
 * TODO Document concurrent semantics of SnapsTagTests
 *
 */
public class SnapsTagTests {
    
    private final SnapsTag snapsTag = new SnapsTag();
    
    private final ServletContext servletContext = createMock(ServletContext.class);
    
    private final BundleContext bundleContext = createMock(BundleContext.class);
    
    private final PageContext pageContext = new MockPageContext(servletContext);
    
    @Before
    public void setup() {
        snapsTag.setPageContext(pageContext);
    }
    
	@Test
    public void noSnapsFromServiceRegistry() throws JspException, InvalidSyntaxException {
        Bundle bundle = createMock(Bundle.class);
        expect(bundleContext.getBundle()).andReturn(bundle);
        expect(bundle.getBundleId()).andReturn(5L);
        expect(bundleContext.getServiceReferences("org.eclipse.virgo.snaps.core.internal.Snap", "(snap.host.id=5)")).andReturn(null);
        expect(servletContext.getAttribute(WebContainer.ATTRIBUTE_BUNDLE_CONTEXT)).andReturn(bundleContext);
        
        replay(servletContext, bundleContext, bundle);
        
        snapsTag.doStartTag();
        
        verify(servletContext, bundleContext, bundle);
        
        @SuppressWarnings("unchecked")
		List<Snap> snaps = (List<Snap>)pageContext.getAttribute(SnapsTag.SNAPS_ATTRIBUTE_NAME);
        assertNotNull(snaps);
        assertEquals(0, snaps.size());
    }
    
    @Test
    public void snapsFromServiceRegistry() throws JspException, InvalidSyntaxException {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put("a", "b");
        properties1.put("c", new Integer(6));
        
        ServiceReference<?> serviceReference1 = createServiceReference(properties1);

        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put("d", "e");
        properties2.put("f", Boolean.TRUE);
        
        ServiceReference<?> serviceReference2 = createServiceReference(properties2);
                
        expect(servletContext.getAttribute(WebContainer.ATTRIBUTE_BUNDLE_CONTEXT)).andReturn(bundleContext);
        
        Bundle bundle = createMock(Bundle.class);
        expect(bundleContext.getBundle()).andReturn(bundle);
        expect(bundle.getBundleId()).andReturn(27L);
        
        expect(bundleContext.getServiceReferences("org.eclipse.virgo.snaps.core.internal.Snap", "(snap.host.id=27)")).andReturn(new ServiceReference[] {serviceReference1, serviceReference2});
        
        expect(serviceReference1.compareTo(serviceReference2)).andReturn(1).anyTimes();
        expect(serviceReference2.compareTo(serviceReference1)).andReturn(-1).anyTimes();
        
        replay(servletContext, bundleContext, serviceReference1, serviceReference2, bundle);
        
        snapsTag.doStartTag();
        
        verify(servletContext, bundleContext, serviceReference1, serviceReference2, bundle);
        
        @SuppressWarnings("unchecked")
		List<Snap> snaps = (List<Snap>)pageContext.getAttribute(SnapsTag.SNAPS_ATTRIBUTE_NAME);
        assertNotNull(snaps);
        assertEquals(2, snaps.size());
        
        Snap snap = snaps.get(1);
        assertEquals("b", snap.getProperties().get("a"));
        assertEquals(new Integer(6), snap.getProperties().get("c"));
        
        snap = snaps.get(0);     
        assertEquals("e", snap.getProperties().get("d"));
        assertEquals(Boolean.TRUE, snap.getProperties().get("f"));
        
    }
    
    private static ServiceReference<?> createServiceReference(Dictionary<?, ?> properties) {
        ServiceReference<?> serviceReference = createMock(ServiceReference.class);
        String[] keys = toArray(properties.keys());
        expect(serviceReference.getPropertyKeys()).andReturn(keys);
        for (String key : keys) {
            expect(serviceReference.getProperty(key)).andReturn(properties.get(key));
        }
        return serviceReference;
    }
    
    private static final String[] toArray(Enumeration<?> enumeration) {
        List<String> list = new ArrayList<String>();
        while(enumeration.hasMoreElements()) {
            String element = enumeration.nextElement().toString();
            list.add(element);
        }
        return list.toArray(new String[list.size()]);
    }
}
