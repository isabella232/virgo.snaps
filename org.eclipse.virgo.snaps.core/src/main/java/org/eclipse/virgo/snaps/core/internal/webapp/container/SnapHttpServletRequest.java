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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.virgo.snaps.core.RequestRouter;


/**
 * TODO Document SnapHttpServletRequest
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * TODO Document concurrent semantics of SnapHttpServletRequest
 * 
 */
class SnapHttpServletRequest extends HttpServletRequestWrapper {

    private static final String HOST_PATH_PREFIX = "host:";

    private final String servletPath;
    
    private final String pathInfo;
    
    private final VirtualContainer virtualContainer;
    
    private final RequestRouter requestRouter;

    public SnapHttpServletRequest(HttpServletRequest delegate, String servletPath, String pathInfo, VirtualContainer virtualContainer, RequestRouter requestRouter) {
        super(delegate);
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
        this.virtualContainer = virtualContainer;
        this.requestRouter = requestRouter;
    }

    /**
     * {@inheritDoc}
     */
    public String getPathInfo() {
        return this.pathInfo;
    }

    /**
     * {@inheritDoc}
     */
    public String getServletPath() {
        return this.servletPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContext getServletContext(){
    	return this.virtualContainer.getSnapServletContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpSession getSession(boolean create) {
        HttpSession session = super.getSession(create);
        if (session != null) {
            session = new SnapHttpSession(session, this.virtualContainer.getSnapServletContext());
        }
        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestDispatcher getRequestDispatcher(final String path) {
        if (path.startsWith(HOST_PATH_PREFIX)) {
            String hostPath = path.substring(HOST_PATH_PREFIX.length());
            return super.getRequestDispatcher(hostPath);
        } else {
            return new RequestDispatcher() {                                                
                public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
                    requestRouter.forward(path, (HttpServletRequest)request, (HttpServletResponse)response);
                }

                public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
                    requestRouter.include(path, (HttpServletRequest)request, (HttpServletResponse)response);                    
                }                
            };                      
        }        
    }
}
