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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.eclipse.gemini.web.core.WebContainer;
import org.eclipse.gemini.web.tomcat.spi.WebBundleClassLoaderFactory;
import org.eclipse.virgo.medic.eventlog.EventLogger;
import org.eclipse.virgo.snaps.core.RequestRouter;
import org.eclipse.virgo.snaps.core.internal.Snap;
import org.eclipse.virgo.snaps.core.internal.Host;
import org.eclipse.virgo.snaps.core.internal.SnapException;
import org.eclipse.virgo.snaps.core.internal.SnapUtils;
import org.eclipse.virgo.snaps.core.internal.SnapsLogEvents;
import org.eclipse.virgo.snaps.core.internal.webapp.config.BundleWebXmlLoader;
import org.eclipse.virgo.snaps.core.internal.webapp.config.WebXml;
import org.eclipse.virgo.snaps.core.internal.webapp.container.VirtualContainer;
import org.eclipse.virgo.snaps.core.internal.webapp.container.VirtualContainerFactory;
import org.eclipse.virgo.util.io.IOUtils;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Document WebAppSnap
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * TODO Document concurrent semantics of WebAppSnap
 * 
 */
class WebAppSnap implements Snap {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Host host;

    private final Bundle snapBundle;

    private volatile VirtualContainer virtualContainer;

    private volatile ClassLoader snapClassLoader;

    private final WebBundleClassLoaderFactory classLoaderFactory;

    private final String contextPath;

    private final EventLogger eventLogger;

    /**
     * @param host
     * @param snapBundle
     * @param webBundleClassLoaderFactory
     */
    public WebAppSnap(Host host, Bundle snapBundle, WebBundleClassLoaderFactory webBundleClassLoaderFactory, EventLogger eventLogger) {
        this.host = host;
        this.snapBundle = snapBundle;
        this.contextPath = SnapUtils.getSnapContextPath(snapBundle);
        this.classLoaderFactory = webBundleClassLoaderFactory;
        this.eventLogger = eventLogger;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws ServletException
     */
    public final void init() throws ServletException {
        logger.info("Initializing snap '{}'", this.contextPath);
        WebXml webXml = BundleWebXmlLoader.loadWebXml(this.snapBundle);
        SnapServletContext servletContext = new SnapServletContext(this.host.getServletContext(), this.snapBundle, this.contextPath);
        servletContext.setAttribute(WebContainer.ATTRIBUTE_BUNDLE_CONTEXT, this.snapBundle.getBundleContext());

        this.snapClassLoader = this.classLoaderFactory.createWebBundleClassLoader(this.snapBundle);

        try {
            ((Lifecycle) snapClassLoader).start();
        } catch (LifecycleException e) {
            logger.error("Failed to start snap's class loader", e);
            throw new ServletException("Failed to start web bundle's class loader", e);
        }

        VirtualContainer virtualContainer = createVirtualContainer(webXml, servletContext, this.snapClassLoader, host.getRouter());

        virtualContainer.init();

        this.virtualContainer = virtualContainer;
        this.eventLogger.log(SnapsLogEvents.SNAP_BOUND, SnapUtils.boundContextPath(this.host.getServletContext().getContextPath(), this.contextPath));
    }

    /**
     * {@inheritDoc}
     */
    public final void destroy() {
        VirtualContainer virtualContainer = this.virtualContainer;

        if (virtualContainer != null) {
            virtualContainer.destroy();
        } else {
            // TODO Log warning that dispatcher was null during destroy
        }

        ClassLoader snapClassLoader = this.snapClassLoader;

        if (snapClassLoader != null) {
            try {
                ((Lifecycle) snapClassLoader).stop();
            } catch (LifecycleException e) {
                logger.error("Failed to stop snap's class loader", e);
                throw new SnapException("Failed to stop web bundle class loader", e);
            }
        } else {
            // TODO Log warning that class loader was null during destroy
        }
        this.eventLogger.log(SnapsLogEvents.SNAP_UNBOUND, SnapUtils.boundContextPath(this.host.getServletContext().getContextPath(), this.contextPath));
    }

    /**
     * {@inheritDoc}
     */
    public final String getContextPath() {
        return this.contextPath;
    }

    /**
     * {@inheritDoc}
     */
    public final void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        VirtualContainer virtualContainer = this.virtualContainer;

        if (virtualContainer != null) {
            virtualContainer.createDispatcher().service(request, response);
        } else {
            // TODO Log warning that dispatcher is not present
            throw new ServletException("handleRequest invoked when virtual container was null");
        }
    }

    protected VirtualContainer createVirtualContainer(WebXml webXml, SnapServletContext servletContext, ClassLoader snapClassLoader, RequestRouter requestRouter) {
        return VirtualContainerFactory.fromWebXml(webXml, servletContext, snapClassLoader, requestRouter);
    }

    /**
     * {@inheritDoc}
     */
    public Properties getSnapProperties() {
        Properties properties = new Properties();
        URL url = this.snapBundle.getEntry("META-INF/snap.properties");
        if (url != null) {
            InputStream is = null;
            try {
                is = url.openStream();
                properties.load(is);
            } catch (IOException ioe) {

            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return properties;
    }
}
