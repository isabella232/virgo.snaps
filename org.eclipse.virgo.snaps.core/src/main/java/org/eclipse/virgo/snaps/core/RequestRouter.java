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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.virgo.snaps.core.internal.Snap;
import org.eclipse.virgo.snaps.core.internal.SnapUtils;



/**
 * TODO Document RequestRouter
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 *
 * TODO Document concurrent semantics of RequestRouter
 *
 */
public final class RequestRouter {
    
    private final SnapRegistry snapRegistry;
    
    private final ServletContext servletContext;
    
    public RequestRouter(SnapRegistry snapRegistry, ServletContext servletContext) {
        this.snapRegistry = snapRegistry;
        this.servletContext = servletContext;
    }
    
    void service(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Snap snap = findSnap(request);
        
        if (snap != null) {
            snap.handleRequest(request, response);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    public void forward(String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = SnapUtils.determineSnapContextPath(request);
        servletContext.getRequestDispatcher(contextPath + path).forward(request, response);
    }
    
    public void include(String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = SnapUtils.determineSnapContextPath(request);
        servletContext.getRequestDispatcher(contextPath + path).include(request, response);
    }    
    
    private Snap findSnap(HttpServletRequest request) {
        String contextPath = SnapUtils.determineSnapContextPath(request);
        return this.snapRegistry.findSnapByContextPath(contextPath);
    }
    
    void destroy() {
        this.snapRegistry.destroy();
    }
}
