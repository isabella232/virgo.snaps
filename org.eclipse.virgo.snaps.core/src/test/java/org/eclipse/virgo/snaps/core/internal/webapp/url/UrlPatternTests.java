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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.virgo.snaps.core.internal.webapp.url.PathTranslation;
import org.eclipse.virgo.snaps.core.internal.webapp.url.UrlPattern;
import org.eclipse.virgo.snaps.core.internal.webapp.url.UrlPatternFactory;
import org.junit.Test;

public class UrlPatternTests {

    @Test
    public void testExactPattern() {
        UrlPattern pattern = UrlPatternFactory.create("/test");
        assertTrue(pattern.matches("/test"));
        assertFalse(pattern.matches("/foo"));
    }

    @Test
    public void testPathPattern() {
        UrlPattern pattern = UrlPatternFactory.create("/test/*");
        assertTrue(pattern.matches("/test/index.html"));
        assertTrue(pattern.matches("/test/"));
        assertFalse(pattern.matches("/foo/"));
    }

    @Test
    public void testExtensionPattern() {
        UrlPattern pattern = UrlPatternFactory.create("*.jsp");
        assertTrue(pattern.matches("/test/index.jsp"));
        assertTrue(pattern.matches("/foo/bar.jsp"));
        assertFalse(pattern.matches("/foo/"));
        assertFalse(pattern.matches("/foo/index.html"));
    }

    @Test
    public void testExactTranslation() {
        UrlPattern pattern = UrlPatternFactory.create("/simple/exact");        

        PathTranslation translation = pattern.translate("/simple/exact");

        assertEquals("/simple/exact", translation.getServletPath());
        assertNull(translation.getPathInfo());
    }
    
    public void testExtensionTranslation() {
        UrlPattern pattern = UrlPatternFactory.create("*.jsp");

        PathTranslation translation = pattern.translate("/foo.jsp");
        
        assertEquals("/foo.jsp", translation.getServletPath());
        assertNull(translation.getPathInfo());
    }

    @Test
    public void testPathTranslation() {
        UrlPattern pattern = UrlPatternFactory.create("/simple/path/*");        

        PathTranslation translation = pattern.translate("/simple/path/test");

        assertEquals("/simple/path", translation.getServletPath());
        assertEquals("/test", translation.getPathInfo());
    }
    
    @Test
    public void testDefaultTranslation() {
        UrlPattern pattern = UrlPatternFactory.create("/");        

        PathTranslation translation = pattern.translate("/simple/path/test");

        assertEquals("/simple", translation.getServletPath());
        assertEquals("/path/test", translation.getPathInfo());
    }
}
