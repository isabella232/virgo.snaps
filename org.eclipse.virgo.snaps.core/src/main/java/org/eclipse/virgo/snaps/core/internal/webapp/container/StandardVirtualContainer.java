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


import javax.servlet.ServletException;

import org.eclipse.virgo.snaps.core.RequestRouter;
import org.eclipse.virgo.snaps.core.internal.SnapException;
import org.eclipse.virgo.snaps.core.internal.webapp.SnapServletContext;
import org.eclipse.virgo.snaps.core.internal.webapp.config.WebXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO Document StandardSnapRequestDispatcher
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * TODO Document concurrent semantics of StandardSnapRequestDispatcher
 * 
 */
final class StandardVirtualContainer implements VirtualContainer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ClassLoader classLoader;
    
    private final ServletManager servletManager;
    
    private final FilterManager filterManager;
    
    private final SnapServletContext snapServletContext;
    
    private final FilterChainFactory filterChainFactory;
    
    private final RequestRouter requestRouter;

    /**
     * @param webXml
     * @param servletContext
     * @param snapClassLoader
     */
    public StandardVirtualContainer(WebXml webXml, SnapServletContext servletContext, ClassLoader snapClassLoader, RequestRouter requestRouter) throws SnapException {                
        this.classLoader = snapClassLoader;
        this.servletManager = new ServletManager(webXml, servletContext, snapClassLoader);
        this.filterManager = new FilterManager(webXml, servletContext, snapClassLoader);
        this.filterChainFactory = new FilterChainFactory(this.filterManager);
        this.snapServletContext = servletContext;
        this.requestRouter = requestRouter;
    }

    /**
     * {@inheritDoc}
     */
    
    public VirtualContainerRequestDispatcher createDispatcher() {
        return new StandardVirtualContainerRequestDispatcher(this.servletManager, this.classLoader, this, this.filterChainFactory, this.requestRouter);
    }
        
    /** 
     * {@inheritDoc}
     */
    public void destroy() {
        this.servletManager.destroy();
        this.filterManager.destroy();
        logger.info("Virtual container destroyed");
    }

    /** 
     * {@inheritDoc}
     */
    public void init() throws ServletException {
        this.servletManager.init();
        this.filterManager.init();
        logger.info("Virtual container initialized");
    }

    /** 
     * {@inheritDoc}
     */
    public SnapServletContext getSnapServletContext() {
        return this.snapServletContext;
    }
}
