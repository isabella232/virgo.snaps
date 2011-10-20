/*
 * This file is part of the Eclipse Virgo project.
 *
 * Copyright (c) 2011 Chariot Solutions, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    dsklyut - initial contribution
 */

package org.eclipse.virgo.snaps.core;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.eclipse.virgo.snaps.core.internal.SnapUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SnapServletRequestListenerDispatcher is used to dispatch {@link ServletRequestEvent} to registered snaps
 * {@link ServletRequestListener}
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 */
public class SnapHostServletEventListenerDispatcher implements ServletRequestListener, ServletRequestAttributeListener, HttpSessionListener,
    HttpSessionAttributeListener, ServletContextListener, ServletContextAttributeListener {

    private static final String TOPIC_PREFIX = "org/eclipse/virgo/snaps/%s/%s";

    private static final String TOPIC_requestDestroyed = String.format(TOPIC_PREFIX, ServletRequestListener.class.getName(), "requestDestroyed");

    private static final String TOPIC_requestInitialized = String.format(TOPIC_PREFIX, ServletRequestListener.class.getName(), "requestInitialized");

    private static final String TOPIC_sessionCreated = String.format(TOPIC_PREFIX, HttpSessionListener.class, "sessionCreated");

    private static final String TOPIC_sessionDestroyed = String.format(TOPIC_PREFIX, HttpSessionListener.class, "sessionDestroyed");

    private static final String TOPIC_contextDestroyed = String.format(TOPIC_PREFIX, ServletContextListener.class.getName(), "contextDestroyed");

    private static final String TOPIC_contextInitialized = String.format(TOPIC_PREFIX, ServletContextListener.class.getName(), "contextInitialized");

    private static final String TOPIC_session_attributeAdded = String.format(TOPIC_PREFIX, HttpSessionAttributeListener.class, "attributeAdded");

    private static final String TOPIC_session_attributeRemoved = String.format(TOPIC_PREFIX, HttpSessionAttributeListener.class, "attributeRemoved");

    private static final String TOPIC_session_attributeReplaced = String.format(TOPIC_PREFIX, HttpSessionAttributeListener.class, "attributeReplaced");

    private static final String TOPIC_request_attributeAdded = String.format(TOPIC_PREFIX, ServletRequestAttributeListener.class, "attributeAdded");

    private static final String TOPIC_request_attributeRemoved = String.format(TOPIC_PREFIX, ServletRequestAttributeListener.class,
        "attributeRemoved");

    private static final String TOPIC_request_attributeReplaced = String.format(TOPIC_PREFIX, ServletRequestAttributeListener.class,
        "attributeReplaced");

    private static final String TOPIC_context_attributeAdded = String.format(TOPIC_PREFIX, ServletContextAttributeListener.class, "attributeAdded");

    private static final String TOPIC_context_attributeRemoved = String.format(TOPIC_PREFIX, ServletContextAttributeListener.class,
        "attributeRemoved");

    private static final String TOPIC_context_attributeReplaced = String.format(TOPIC_PREFIX, ServletContextAttributeListener.class,
        "attributeReplaced");

    private final Logger logger = LoggerFactory.getLogger(SnapHostServletEventListenerDispatcher.class);

    // private final EventLogger eventLogger;

    private final EventAdmin eventAdmin;

    public SnapHostServletEventListenerDispatcher() {
        BundleContext bundleContext = FrameworkUtil.getBundle(SnapHostFilter.class).getBundleContext();
        // this.eventLogger = bundleContext.getService(bundleContext.getServiceReference(EventLogger.class));
        this.eventAdmin = bundleContext.getService(bundleContext.getServiceReference(EventAdmin.class));
    }

    /**
     * @param eventLogger
     * @param eventAdmin
     */
    SnapHostServletEventListenerDispatcher(/* EventLogger eventLogger, */EventAdmin eventAdmin) {
        super();
        // this.eventLogger = eventLogger;
        this.eventAdmin = eventAdmin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        sendEvent(sre, TOPIC_requestDestroyed, sre.getServletContext());
        logger.debug("Host dispatched requestDestroyed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        sendEvent(sre, TOPIC_requestInitialized, sre.getServletContext());
        logger.debug("Host dispatched requestInitialized");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        sendEvent(event, TOPIC_session_attributeAdded, event.getSession().getServletContext());
        logger.debug("Host dispatched session attributeAdded");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        sendEvent(event, TOPIC_session_attributeRemoved, event.getSession().getServletContext());
        logger.debug("Host dispatched session attributeRemoved");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        sendEvent(event, TOPIC_session_attributeReplaced, event.getSession().getServletContext());
        logger.debug("Host dispatched session attributeReplaced");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sendEvent(se, TOPIC_sessionCreated, se.getSession().getServletContext());
        logger.debug("Host dispatched sessionCreated");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sendEvent(se, TOPIC_sessionDestroyed, se.getSession().getServletContext());
        logger.debug("Host dispatched sessionDestroyed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        sendEvent(srae, TOPIC_request_attributeAdded, srae.getServletContext());
        logger.debug("Host dispatched request attributeAdded");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        sendEvent(srae, TOPIC_request_attributeRemoved, srae.getServletContext());
        logger.debug("Host dispatched request attributeRemoved");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeReplaced(ServletRequestAttributeEvent event) {
        sendEvent(event, TOPIC_request_attributeReplaced, event.getServletContext());
        logger.debug("Host dispatched request attributeReplaced");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        sendEvent(event, TOPIC_context_attributeAdded, event.getServletContext());
        logger.debug("Host dispatched context attributeAdded");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        sendEvent(event, TOPIC_context_attributeRemoved, event.getServletContext());
        logger.debug("Host dispatched context attributeRemoved");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        sendEvent(event, TOPIC_context_attributeReplaced, event.getServletContext());
        logger.debug("Host dispatched context attributeReplaced");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sendEvent(sce, TOPIC_contextInitialized, sce.getServletContext());
        logger.debug("Host dispatched request contextInitialized");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sendEvent(sce, TOPIC_contextDestroyed, sce.getServletContext());
        logger.debug("Host dispatched contextDestroyed");

    }

    // / ----- internal impl

    private void sendEvent(EventObject event, String topic, ServletContext ctx) {
        BundleContext context = null;
        try {
            context = SnapUtils.getRequiredBundleContext(ctx);
        } catch (ServletException ex) {
            logger.warn(ex.getMessage());

            // there isn't much we can do here unless we want to throw runtime.
            return;
        }
        this.eventAdmin.sendEvent(createEventObject(event, topic, context.getBundle()));
    }

    /**
     * @param sre
     * @return
     */
    private Event createEventObject(EventObject sre, String topic, Bundle hostBundle) {
        Map<String, Object> properties = new HashMap<String, Object>();
        addBundleProperties(properties, hostBundle);
        properties.put(EventConstants.EVENT, sre);
        return new Event(topic, properties);
    }

    /**
     * will be used for filtering in the snap. i.e will be easy to filter by host bundle.id for example
     * 
     * @param properties
     * @param bundle
     */
    private void addBundleProperties(Map<String, Object> properties, Bundle bundle) {
        properties.put(EventConstants.BUNDLE_ID, bundle.getBundleId());
        properties.put(EventConstants.BUNDLE_SYMBOLICNAME, bundle.getSymbolicName());
        properties.put(EventConstants.BUNDLE_VERSION, bundle.getVersion());
    }

}
