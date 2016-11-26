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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.eclipse.virgo.snaps.core.internal.SnapException;
import org.eclipse.virgo.snaps.core.internal.webapp.ImmutableServletConfig;
import org.eclipse.virgo.snaps.core.internal.webapp.SnapServletContext;
import org.eclipse.virgo.snaps.core.internal.webapp.config.ServletDefinition;
import org.eclipse.virgo.snaps.core.internal.webapp.config.ServletMappingDefinition;
import org.eclipse.virgo.snaps.core.internal.webapp.config.WebXml;
import org.eclipse.virgo.snaps.core.internal.webapp.container.ManagerUtils.ClassLoaderCallback;
import org.eclipse.virgo.snaps.core.internal.webapp.url.Mapping;
import org.eclipse.virgo.snaps.core.internal.webapp.url.UrlPatternMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO Document ServletManager
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * TODO Document concurrent semantics of ServletManager
 * 
 */
final class ServletManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<SnapServletContext> snapServletContexts = new ArrayList<>();

    private final ClassLoader classLoader;

    private final UrlPatternMatcher patternMatcher = new UrlPatternMatcher();

    private final Map<String, ServletHolder> servlets = new ConcurrentHashMap<String, ServletHolder>();
    
	private WebXml webXml;

    ServletManager(WebXml webXml, SnapServletContext snapServletContext, ClassLoader classLoader) {
    	this.webXml = webXml;
		this.snapServletContexts.add(snapServletContext);
        this.classLoader = classLoader;

        reifyWebXml(snapServletContext);
    }

    void init() throws ServletException {
        try {
            ManagerUtils.doWithThreadContextClassLoader(this.classLoader, new ClassLoaderCallback<Void>() {

                public Void doWithClassLoader() throws ServletException {
                    for (Map.Entry<String, ServletHolder> entry : servlets.entrySet()) {
                        ServletHolder holder = entry.getValue();
                        ServletConfig config = new ImmutableServletConfig(holder.getDefinition(),  snapServletContexts.get(0));
                        holder.getInstance().init(config);
                        // TODO Log which servlet failed, and re-throw
                    }
                    return null;
                }
            });
        } catch (IOException e) {
            logger.error("Unexpected IOException from servlet init", e);
            throw new ServletException("Unexpected IOException from servlet init", e);
        }
    }

    void destroy() {
        for (Map.Entry<String, ServletHolder> entry : this.servlets.entrySet()) {
            ServletHolder holder = entry.getValue();
            holder.getInstance().destroy();
        }
        this.servlets.clear();
    }

    Match findMatch(String servletPath) {
        Mapping mapping = this.patternMatcher.match(servletPath);
        if (mapping != null) {
            ServletHolder servletHolder = this.servlets.get(mapping.getName());
            return new Match(servletHolder.getInstance(), mapping);
        } else {
            return null;
        }
    }

    private void reifyWebXml(SnapServletContext snapServletContext) {
        processServlets();
        processServletMappingDefinitions(snapServletContext);
    }

    private void processServlets( ) throws SnapException {
        for (ServletDefinition servletDefinition : this.webXml.getServletDefinitions()) {
            try {
                Class<?> servletClass = ManagerUtils.loadComponentClass(servletDefinition.getServletClassName(), this.classLoader);
                Servlet servlet = (Servlet) servletClass.newInstance();
                this.servlets.put(servletDefinition.getServletName(), new ServletHolder(servletDefinition, servlet));
            } catch (ClassNotFoundException e) {
                logger.error(String.format("The servlet class '%s' could not be loaded by '%s'", servletDefinition.getServletClassName(),
                    this.classLoader), e);
                throw new SnapException("The servlet class '" + servletDefinition.getServletClassName() + "' could not be loaded by "
                    + this.classLoader, e);
            } catch (InstantiationException e) {
                logger.error(String.format("The servlet class '%s' could not be instantiated", servletDefinition.getServletClassName()), e);
                throw new SnapException("The servlet class '" + servletDefinition.getServletClassName() + "' could not be instantiated", e);
            } catch (IllegalAccessException e) {
                logger.error(String.format("The servlet class '%s' could not be instantiated due to access restrictions",
                    servletDefinition.getServletClassName()), e);
                throw new SnapException("The servlet class '" + servletDefinition.getServletClassName()
                    + "' could not be instantiated due to access restrictions", e);
            }
        }
    }

    private void processServletMappingDefinitions(SnapServletContext snapServletContext) {
        for (ServletMappingDefinition mappingDefinition : this.webXml.getServletMappingDefinitions()) {
            this.patternMatcher.addMapping(mappingDefinition.getServletName(), ManagerUtils.expandMapping(mappingDefinition.getUrlPattern(),
                snapServletContext));
            // TODO Validate, probably in WebXml, the referenced servlets exist, etc.
        }
    }

    static class Match {

        private final Servlet servlet;

        private final Mapping mapping;

        public Match(Servlet servlet, Mapping mapping) {
            this.servlet = servlet;
            this.mapping = mapping;
        }

        /**
         * @return
         */
        public Servlet getServlet() {
            return servlet;
        }

        /**
         * @return
         */
        public Mapping getMapping() {
            return mapping;
        }
    }
    
	public void addSnapServletContext(SnapServletContext snapServletContext) {
		this.snapServletContexts.add(snapServletContext);
		processServletMappingDefinitions(snapServletContext);
	}
}
