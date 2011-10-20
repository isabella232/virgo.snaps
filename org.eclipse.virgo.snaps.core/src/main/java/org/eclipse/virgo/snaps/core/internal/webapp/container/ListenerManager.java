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

package org.eclipse.virgo.snaps.core.internal.webapp.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.eclipse.virgo.snaps.core.internal.SnapException;
import org.eclipse.virgo.snaps.core.internal.webapp.SnapServletContext;
import org.eclipse.virgo.snaps.core.internal.webapp.config.ListenerDefinition;
import org.eclipse.virgo.snaps.core.internal.webapp.config.WebXml;
import org.osgi.framework.Bundle;

/**
 * TODO Document ListenerManager
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * TODO Document concurrent semantics of ListenerManager
 */
final class ListenerManager {

    @SuppressWarnings("unchecked")
    private final List<Class<? extends EventListener>> SUPPORTED_LISTENERS = Arrays.<Class<? extends EventListener>> asList(
        ServletRequestListener.class, ServletRequestAttributeListener.class, HttpSessionListener.class, HttpSessionAttributeListener.class,
        ServletContextListener.class, ServletContextAttributeListener.class);

    private final SnapServletContext snapServletContext;

    private final Bundle snapBundle;

    private final ClassLoader classLoader;

    private final ListenerAdapter listenerAdapter = new ListenerAdapter();

    public ListenerManager(WebXml webXml, SnapServletContext snapServletContext, ClassLoader classLoader, Bundle snapBundle) {
        this.snapServletContext = snapServletContext;
        this.classLoader = classLoader;
        this.snapBundle = snapBundle;

        reifyWebXml(webXml);
    }

    void init() {
        // register all of the required EventAdmin handlers.
    }

    private void reifyWebXml(WebXml webXml) throws SnapException {
        for (ListenerDefinition def : webXml.getListenerDefinitions()) {
            try {
                Class<?> listenerClass = ManagerUtils.loadComponentClass(def.getListenerClassName(), this.classLoader);
                if (isListnerSupported(listenerClass)) {
                    this.listenerAdapter.registerListener((EventListener) listenerClass.newInstance());
                } else {
                    throw new SnapException(String.format("The class '%s' is not supported as a snap listener", listenerClass.getName()));
                }
            } catch (ClassNotFoundException e) {
                throw new SnapException(String.format("The listener class '%s' could not be loaded by %s", def.getListenerClassName(),
                    this.classLoader.toString()), e);
            } catch (InstantiationException e) {
                throw new SnapException(String.format("The listener class '%s' could not be instantiated", def.getListenerClassName()), e);
            } catch (IllegalAccessException e) {
                throw new SnapException(String.format("The listener class '%s' could not be instantiated due to access restrictions",
                    def.getListenerClassName()), e);
            } catch (Exception e) {
                throw new SnapException(String.format("Error initializing listener class '%s'", def.getListenerClassName()), e);
            }
        }
    }

    /**
     * @param listenerClass
     * @return
     */
    private boolean isListnerSupported(Class<?> listenerClass) {
        for (Class<?> c : SUPPORTED_LISTENERS) {
            if (c.isAssignableFrom(listenerClass)) {
                return true;
            }
        }
        return false;
    }

    private static class ListenerAdapter implements ServletRequestListener, ServletRequestAttributeListener, HttpSessionListener,
        HttpSessionAttributeListener, ServletContextListener, ServletContextAttributeListener {

        private final List<ServletRequestListener> servletRequestListeners = new ArrayList<ServletRequestListener>();

        private final List<ServletRequestAttributeListener> servletRequestAttributeListeners = new ArrayList<ServletRequestAttributeListener>();

        private final List<HttpSessionListener> httpSessionListeners = new ArrayList<HttpSessionListener>();

        private final List<HttpSessionAttributeListener> httpSessionAttributeListeners = new ArrayList<HttpSessionAttributeListener>();

        private final List<ServletContextListener> servletContextListeners = new ArrayList<ServletContextListener>();

        private final List<ServletContextAttributeListener> servletContextAttributeListeners = new ArrayList<ServletContextAttributeListener>();

        void registerListener(EventListener l) {
            if (l instanceof ServletRequestListener) {
                add((ServletRequestListener) l);
            }
            if (l instanceof ServletRequestAttributeListener) {
                add((ServletRequestAttributeListener) l);
            }
            if (l instanceof HttpSessionListener) {
                add((HttpSessionListener) l);
            }
            if (l instanceof HttpSessionAttributeListener) {
                add((HttpSessionAttributeListener) l);
            }
            if (l instanceof ServletContextListener) {
                add((ServletContextListener) l);
            }
            if (l instanceof ServletContextAttributeListener) {
                add((ServletContextAttributeListener) l);
            }
        }

        void add(ServletRequestListener l) {
            this.servletRequestListeners.add(l);
        }

        void add(ServletRequestAttributeListener l) {
            this.servletRequestAttributeListeners.add(l);
        }

        void add(HttpSessionAttributeListener l) {
            this.httpSessionAttributeListeners.add(l);
        }

        void add(HttpSessionListener l) {
            this.httpSessionListeners.add(l);
        }

        void add(ServletContextListener l) {
            this.servletContextListeners.add(l);
        }

        void add(ServletContextAttributeListener l) {
            this.servletContextAttributeListeners.add(l);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributeAdded(ServletContextAttributeEvent event) {
            for (ServletContextAttributeListener l : servletContextAttributeListeners) {
                l.attributeAdded(event);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributeRemoved(ServletContextAttributeEvent event) {
            for (ServletContextAttributeListener l : servletContextAttributeListeners) {
                l.attributeRemoved(event);
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributeReplaced(ServletContextAttributeEvent event) {
            for (ServletContextAttributeListener l : servletContextAttributeListeners) {
                l.attributeReplaced(event);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void contextInitialized(ServletContextEvent sce) {
            for (ServletContextListener l : servletContextListeners) {
                l.contextInitialized(sce);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            for (ServletContextListener l : servletContextListeners) {
                l.contextDestroyed(sce);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributeAdded(HttpSessionBindingEvent event) {
            for (HttpSessionAttributeListener l : httpSessionAttributeListeners) {
                l.attributeAdded(event);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributeRemoved(HttpSessionBindingEvent event) {
            for (HttpSessionAttributeListener l : httpSessionAttributeListeners) {
                l.attributeRemoved(event);
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributeReplaced(HttpSessionBindingEvent event) {
            for (HttpSessionAttributeListener l : httpSessionAttributeListeners) {
                l.attributeReplaced(event);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void sessionCreated(HttpSessionEvent se) {
            for (HttpSessionListener l : httpSessionListeners) {
                l.sessionCreated(se);
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            for (HttpSessionListener l : httpSessionListeners) {
                l.sessionDestroyed(se);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributeAdded(ServletRequestAttributeEvent srae) {
            for (ServletRequestAttributeListener l : servletRequestAttributeListeners) {
                l.attributeAdded(srae);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributeRemoved(ServletRequestAttributeEvent srae) {
            for (ServletRequestAttributeListener l : servletRequestAttributeListeners) {
                l.attributeRemoved(srae);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributeReplaced(ServletRequestAttributeEvent srae) {
            for (ServletRequestAttributeListener l : servletRequestAttributeListeners) {
                l.attributeReplaced(srae);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void requestDestroyed(ServletRequestEvent sre) {
            for (ServletRequestListener l : servletRequestListeners) {
                l.requestDestroyed(sre);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void requestInitialized(ServletRequestEvent sre) {
            for (ServletRequestListener l : servletRequestListeners) {
                l.requestInitialized(sre);
            }
        }

    }
}
