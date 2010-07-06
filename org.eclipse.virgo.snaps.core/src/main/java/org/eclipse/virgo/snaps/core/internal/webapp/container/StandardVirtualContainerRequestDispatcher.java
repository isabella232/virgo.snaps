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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.virgo.snaps.core.RequestRouter;
import org.eclipse.virgo.snaps.core.internal.webapp.config.FilterDispatcherType;
import org.eclipse.virgo.snaps.core.internal.webapp.container.ServletManager.Match;
import org.eclipse.virgo.snaps.core.internal.webapp.url.Mapping;
import org.eclipse.virgo.snaps.core.internal.webapp.url.PathTranslation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO Document StandardVirtualContainerRequestDispatcher
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * TODO Document concurrent semantics of StandardVirtualContainerRequestDispatcher
 * 
 */
final class StandardVirtualContainerRequestDispatcher implements VirtualContainerRequestDispatcher {

    private static final String FORWARD_REQUEST_URI_ATTRIBUTE_NAME = "javax.servlet.forward.request_uri";
    
    private static final String INCLUDE_SERVLET_PATH_ATTRIBUTE_NAME = "javax.servlet.include.servlet_path";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ServletManager servletManager;

    private final ClassLoader classLoader;

    private final VirtualContainer virtualContainer;

    private final FilterChainFactory filterChainFactory;
    
    private final RequestRouter requestRouter;

    /**
     * @param requestRouter 
     * @param path
     */
    StandardVirtualContainerRequestDispatcher(ServletManager servletManager, ClassLoader classLoader, VirtualContainer virtualContainer,
        FilterChainFactory filterChainFactory, RequestRouter requestRouter) {
        this.servletManager = servletManager;
        this.classLoader = classLoader;
        this.virtualContainer = virtualContainer;
        this.filterChainFactory = filterChainFactory;
        this.requestRouter = requestRouter;
    }

    public void service(HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        logger.info("Servicing request '{}'", request.getPathInfo());
        
        FilterDispatcherType requestType = FilterDispatcherType.REQUEST;
        
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        
        String servletPathForMatch = (String)request.getAttribute(INCLUDE_SERVLET_PATH_ATTRIBUTE_NAME);
        
        if (servletPathForMatch == null) {
            if (request.getAttribute(FORWARD_REQUEST_URI_ATTRIBUTE_NAME) != null) {
                requestType = FilterDispatcherType.FORWARD;
            }
            servletPathForMatch = request.getServletPath();            
        } else {
            requestType = FilterDispatcherType.INCLUDE;
        }
        
        final Match match = servletManager.findMatch(servletPathForMatch);
        if (match == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {         
            if (requestType != FilterDispatcherType.INCLUDE) {
                PathTranslation translation = match.getMapping().translate(servletPathForMatch);
                servletPath = translation.getServletPath();
                pathInfo = translation.getPathInfo();
            }
            final HttpServletRequest wrappedRequest = wrapRequest(request, servletPath, pathInfo, match.getMapping());
            final FilterChain filterChain = this.filterChainFactory.createFilterChain(servletPathForMatch, match.getServlet(), match.getMapping().getName(),
                requestType);
            doWithThreadContextClassLoader(new StandardVirtualContainerRequestDispatcher.ClassLoaderCallback<Void>() {

                public Void doWithClassLoader() throws ServletException, IOException {
                    filterChain.doFilter(wrappedRequest, response);
                    return null;
                }
            });
        }
    }

    private HttpServletRequest wrapRequest(HttpServletRequest request, String servletPath, String pathInfo, Mapping match) {
        return new SnapHttpServletRequest(request, servletPath, pathInfo, this.virtualContainer, this.requestRouter);
    }

    private <T> T doWithThreadContextClassLoader(StandardVirtualContainerRequestDispatcher.ClassLoaderCallback<T> callback) throws ServletException,
        IOException {
        Thread currentThread = Thread.currentThread();
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(this.classLoader);
            return callback.doWithClassLoader();
        } finally {
            currentThread.setContextClassLoader(contextClassLoader);
        }
    }

    private static interface ClassLoaderCallback<T> {

        T doWithClassLoader() throws ServletException, IOException;
    }

}
