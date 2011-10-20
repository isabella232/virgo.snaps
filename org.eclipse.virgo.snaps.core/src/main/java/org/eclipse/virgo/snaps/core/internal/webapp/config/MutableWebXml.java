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

package org.eclipse.virgo.snaps.core.internal.webapp.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class MutableWebXml implements WebXml {

    private final List<ServletDefinition> servletDefinitions = new ArrayList<ServletDefinition>();

    private final List<ServletMappingDefinition> servletMappingDefinitions = new ArrayList<ServletMappingDefinition>();

    private final List<FilterDefinition> filterDefinitions = new ArrayList<FilterDefinition>();

    private final List<ServletNameFilterMappingDefinition> servletNameFilterMappingDefinitions = new ArrayList<ServletNameFilterMappingDefinition>();

    private final List<UrlPatternFilterMappingDefinition> urlPatternFilterMappingDefinitions = new ArrayList<UrlPatternFilterMappingDefinition>();

    private final List<ListenerDefinition> listenerDefinitions = new ArrayList<ListenerDefinition>();

    public ServletDefinition[] getServletDefinitions() {
        return this.servletDefinitions.toArray(new ServletDefinition[this.servletDefinitions.size()]);
    }

    public ServletMappingDefinition[] getServletMappingDefinitions() {
        return this.servletMappingDefinitions.toArray(new ServletMappingDefinition[this.servletMappingDefinitions.size()]);
    }

    public FilterDefinition[] getFilterDefinitions() {
        return this.filterDefinitions.toArray(new FilterDefinition[this.filterDefinitions.size()]);
    }

    public ServletNameFilterMappingDefinition[] getServletNameFilterMappingDefinitions() {
        return this.servletNameFilterMappingDefinitions.toArray(new ServletNameFilterMappingDefinition[this.servletNameFilterMappingDefinitions.size()]);
    }

    public UrlPatternFilterMappingDefinition[] getUrlPatternFilterMappingDefinitions() {
        return this.urlPatternFilterMappingDefinitions.toArray(new UrlPatternFilterMappingDefinition[this.urlPatternFilterMappingDefinitions.size()]);
    }

    public ListenerDefinition[] getListenerDefinitions() {
        return this.listenerDefinitions.toArray(new ListenerDefinition[listenerDefinitions.size()]);
    }

    public MutableServletDefinition addServletDefinition(String servletName, String servletClassName) {
        MutableServletDefinition def = new MutableServletDefinition(servletName, servletClassName);
        this.servletDefinitions.add(def);
        return def;
    }

    public void addServletMappingDefinition(String servletName, String urlPattern) {
        ImmutableServletMappingDefinition def = new ImmutableServletMappingDefinition(servletName, urlPattern);
        this.servletMappingDefinitions.add(def);
    }

    public MutableFilterDefinition addFilterDefinition(String filterName, String filterClassName) {
        MutableFilterDefinition def = new MutableFilterDefinition(filterName, filterClassName);
        this.filterDefinitions.add(def);
        return def;
    }

    public void addUrlPatternFilterMappingDefinition(String filterName, String urlPattern, Set<FilterDispatcherType> filterDispatcherTypes) {
        UrlPatternFilterMappingDefinition definition = new ImmutableUrlPatternFilterMappingDefinition(filterName, urlPattern, filterDispatcherTypes);
        this.urlPatternFilterMappingDefinitions.add(definition);
    }

    public void addServletNameFilterMappingDefinition(String filterName, String servletName, Set<FilterDispatcherType> filterDispatcherTypes) {
        ServletNameFilterMappingDefinition definition = new ImmutableServletNameFilterMappingDefinition(filterName, servletName,
            filterDispatcherTypes);
        this.servletNameFilterMappingDefinitions.add(definition);
    }

    public void addListenerDefinition(String listenerClassName) {
        this.listenerDefinitions.add(new ImmutableListenerDefinition(listenerClassName));
    }

    static abstract class AbstractWebComponentDefinition implements WebComponentDefinition {

        private final Map<String, String> initParameters = new ConcurrentHashMap<String, String>();

        /**
         * {@inheritDoc}
         */
        public final Map<String, String> getInitParameters() {
            return this.initParameters;
        }

        public final void addInitParameter(String name, String value) {
            this.initParameters.put(name, value);
        }
    }

    static final class MutableServletDefinition extends AbstractWebComponentDefinition implements ServletDefinition {

        private final String servletName;

        private final String servletClassName;

        public MutableServletDefinition(String servletName, String servletClassName) {
            this.servletClassName = servletClassName;
            this.servletName = servletName;
        }

        public String getServletName() {
            return this.servletName;
        }

        public String getServletClassName() {
            return this.servletClassName;
        }
    }

    private static final class ImmutableServletMappingDefinition implements ServletMappingDefinition {

        private final String servletName;

        private final String urlPattern;

        public ImmutableServletMappingDefinition(String servletName, String urlPattern) {
            this.servletName = servletName;
            this.urlPattern = urlPattern;
        }

        public String getUrlPattern() {
            return this.urlPattern;
        }

        public String getServletName() {
            return this.servletName;
        }
    }

    static final class MutableFilterDefinition extends AbstractWebComponentDefinition implements FilterDefinition {

        private final String filterName;

        private final String filterClassName;

        public MutableFilterDefinition(String filterName, String filterClassName) {
            this.filterClassName = filterClassName;
            this.filterName = filterName;
        }

        public String getFilterName() {
            return this.filterName;
        }

        public String getFilterClassName() {
            return this.filterClassName;
        }
    }

    static abstract class AbstractFilterMappingDefinition implements FilterMappingDefinition {

        private final String filterName;

        final Set<FilterDispatcherType> dispatcherTypes;

        private AbstractFilterMappingDefinition(String filterName, Set<FilterDispatcherType> filterDispatcherTypes) {
            this.filterName = filterName;
            this.dispatcherTypes = filterDispatcherTypes;
        }

        public String getFilterName() {
            return this.filterName;
        }

        public Set<FilterDispatcherType> getFilterDispatcherTypes() {
            return dispatcherTypes;
        }
    }

    private static final class ImmutableServletNameFilterMappingDefinition extends AbstractFilterMappingDefinition implements
        ServletNameFilterMappingDefinition {

        private final String servletName;

        public ImmutableServletNameFilterMappingDefinition(String filterName, String servletName, Set<FilterDispatcherType> filterDispatcherTypes) {
            super(filterName, filterDispatcherTypes);
            this.servletName = servletName;
        }

        public String getServletName() {
            return servletName;
        }
    }

    private static final class ImmutableUrlPatternFilterMappingDefinition extends AbstractFilterMappingDefinition implements
        UrlPatternFilterMappingDefinition {

        private final String urlPattern;

        public ImmutableUrlPatternFilterMappingDefinition(String filterName, String urlPattern, Set<FilterDispatcherType> filterDispatcherTypes) {
            super(filterName, filterDispatcherTypes);
            this.urlPattern = urlPattern;
        }

        public String getUrlPattern() {
            return urlPattern;
        }
    }

    private static final class ImmutableListenerDefinition implements ListenerDefinition {

        private final String listenerClassName;

        public ImmutableListenerDefinition(String listenerClassName) {
            this.listenerClassName = listenerClassName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getListenerClassName() {
            return this.listenerClassName;
        }
    }
}
