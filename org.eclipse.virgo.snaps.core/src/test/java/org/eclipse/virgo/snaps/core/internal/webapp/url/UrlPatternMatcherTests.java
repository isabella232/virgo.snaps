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

package org.eclipse.virgo.snaps.core.internal.webapp.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.virgo.snaps.core.internal.webapp.url.Mapping;
import org.eclipse.virgo.snaps.core.internal.webapp.url.UrlPatternMatcher;
import org.junit.Test;



public class UrlPatternMatcherTests {

    /**
     * This test matches the examples given in the servlet spec section 11.2.2.
     */
    @Test
    public void testAsPerServletSpec() {
        UrlPatternMatcher matcher = new UrlPatternMatcher();
        
        matcher.addMapping("servlet1", "/foo/bar/*");
        matcher.addMapping("servlet2", "/baz/*");
        matcher.addMapping("servlet3", "/catalog");
        matcher.addMapping("servlet4", "*.bop");
        
        assertMapsTo(matcher, "/foo/bar/index.html", "servlet1");
        assertMapsTo(matcher, "/foo/bar/index.bop", "servlet1");
        assertMapsTo(matcher, "/baz", "servlet2");
        assertMapsTo(matcher, "/baz/index.html", "servlet2");
        assertMapsTo(matcher, "/catalog", "servlet3");
        assertMapsTo(matcher, "/catalog/racecar.bop", "servlet4");
        assertMapsTo(matcher, "/index.bop", "servlet4");
        
        assertNull(matcher.match("/catalog/index.html"));
    }
    
    @Test
    public void testLongestMatch() {
        UrlPatternMatcher matcher = new UrlPatternMatcher();
        matcher.addMapping("servlet1", "/foo/bar/*");
        matcher.addMapping("servlet2", "/foo/bar/abc/*");
        
        assertMapsTo(matcher, "/foo/bar/index.html", "servlet1");
        assertMapsTo(matcher, "/foo/bar/index.bop", "servlet1");
        assertMapsTo(matcher, "/foo/bar/abc/xyz/index.bop", "servlet2");
    }
    
    @Test
    public void slashMapping() {
        UrlPatternMatcher matcher = new UrlPatternMatcher();
        matcher.addMapping("servlet1", "/");
        
        assertMapsTo(matcher, "/foo/bar", "servlet1");
    }
    
    private void assertMapsTo(UrlPatternMatcher matcher, String path, String  servletName) {
        Mapping mapping = matcher.match(path);
        assertNotNull("No mapping found for '" + path + "'", mapping);
        assertEquals("Incorrect mapping", servletName, mapping.getName());
    }
}
