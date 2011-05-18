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
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.eclipse.virgo.snaps.core.SnapHostFilter;
import org.eclipse.virgo.snaps.core.SnapRegistry;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import org.eclipse.virgo.medic.test.eventlog.MockEventLogger;

public class SnapHostFilterTests {

    private StubSnapRegistry stubRegistry;

    @Before
    public void before() {
        this.stubRegistry = new StubSnapRegistry();
    }

    @Test
    public void testInitAndDestroy() throws ServletException {
        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig config = new MockFilterConfig(servletContext);

        TestFilter filter = new TestFilter();

        assertFalse(this.stubRegistry.initCalled);
        assertFalse(this.stubRegistry.destroyCalled);

        filter.init(config);
        assertTrue(this.stubRegistry.initCalled);
        assertFalse(this.stubRegistry.destroyCalled);

        filter.destroy();
        assertTrue(this.stubRegistry.initCalled);
        assertTrue(this.stubRegistry.destroyCalled);

    }

    @Test
    public void testRouteToSnap() throws ServletException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/hotels/booking");

        Snap snap = createMock(Snap.class);
        snap.handleRequest(request, response);
        expectLastCall();

        this.stubRegistry.snaps.put("/hotels", snap);

        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig config = new MockFilterConfig(servletContext);
        MockFilterChain chain = new MockFilterChain();

        replay(snap);
        TestFilter filter = new TestFilter();
        filter.init(config);
        filter.doFilter(request, response, chain);
        filter.destroy();
        verify(snap);
    }

    @Test
    public void testRouteToChain() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setPathInfo("/hotels/booking");

        Snap snap = createMock(Snap.class);
        FilterChain chain = createMock(FilterChain.class);
        chain.doFilter(request, response);
        expectLastCall();

        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig config = new MockFilterConfig(servletContext);

        replay(snap, chain);
        TestFilter filter = new TestFilter();
        filter.init(config);
        filter.doFilter(request, response, chain);
        filter.destroy();
        verify(snap, chain);
    }

    private class TestFilter extends SnapHostFilter {

        public TestFilter() {
            super(new MockEventLogger());
        }

        @Override
        protected SnapRegistry createSnapRegistry(ServletContext servletContext) throws ServletException {
            return stubRegistry;
        }

    }

    private static class StubSnapRegistry implements SnapRegistry {

        private final Map<String, Snap> snaps = new HashMap<String, Snap>();

        public boolean initCalled = false;

        public boolean destroyCalled = false;

        public Snap findSnapByContextPath(String contextPath) {
            return this.snaps.get(contextPath);
        }

        public void init() {
            this.initCalled = true;
        }

        public void destroy() {
            this.destroyCalled = true;
        }
    }
}
