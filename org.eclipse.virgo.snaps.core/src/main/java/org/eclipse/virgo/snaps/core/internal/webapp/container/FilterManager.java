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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.eclipse.virgo.snaps.core.internal.SnapException;
import org.eclipse.virgo.snaps.core.internal.webapp.ImmutableFilterConfig;
import org.eclipse.virgo.snaps.core.internal.webapp.SnapServletContext;
import org.eclipse.virgo.snaps.core.internal.webapp.config.FilterDefinition;
import org.eclipse.virgo.snaps.core.internal.webapp.config.FilterDispatcherType;
import org.eclipse.virgo.snaps.core.internal.webapp.config.ServletNameFilterMappingDefinition;
import org.eclipse.virgo.snaps.core.internal.webapp.config.UrlPatternFilterMappingDefinition;
import org.eclipse.virgo.snaps.core.internal.webapp.config.WebXml;
import org.eclipse.virgo.snaps.core.internal.webapp.container.ManagerUtils.ClassLoaderCallback;
import org.eclipse.virgo.snaps.core.internal.webapp.url.FilterUrlPatternMatcher;


class FilterManager {
    
	private final List<SnapServletContext> snapServletContexts = new ArrayList<>();
	
    private final ClassLoader classLoader;        
    
    private final List<FilterHolder> filters = Collections.synchronizedList(new ArrayList<FilterHolder>());
    
    private final List<UrlPatternFilterMapping> urlPatternFilterMappings = Collections.synchronizedList(new ArrayList<UrlPatternFilterMapping>());
    
    private final List<ServletNameFilterMapping> servletNameFilterMappings = Collections.synchronizedList(new ArrayList<ServletNameFilterMapping>());
    
	private WebXml webXml;

	private Map<String, FilterHolder> filtersMap;
    
    public FilterManager(WebXml webXml, SnapServletContext snapServletContext, ClassLoader classLoader) {
    	this.webXml = webXml;
		this.snapServletContexts.add(snapServletContext);
        this.classLoader = classLoader;

        this.filtersMap = processFilters();
        reifyWebXml(snapServletContext);
    }
    
    private void reifyWebXml(SnapServletContext snapServletContext) {
        processUrlPatternFilterMappings(snapServletContext);
        processServletNameFilterMappings();
    }           
    
    private Map<String, FilterHolder> processFilters() throws SnapException {
        Map<String, FilterHolder> filtersMap = new HashMap<String, FilterHolder>();
        
        for (FilterDefinition filterDefinition : this.webXml.getFilterDefinitions()) {
            try {
                Class<?> filterClass = ManagerUtils.loadComponentClass(filterDefinition.getFilterClassName(), this.classLoader);
                if (Filter.class.isAssignableFrom(filterClass)) {
                    Filter filter = (Filter) filterClass.newInstance();
                    FilterHolder filterHolder = new FilterHolder(filterDefinition, filter);
                    filtersMap.put(filterDefinition.getFilterName(), filterHolder);
                    this.filters.add(filterHolder);
                } else {
                    throw new SnapException("The class '" + filterClass.getName() + "' does not implement '" + Filter.class.getName() + "'");
                }
            } catch (ClassNotFoundException e) {
                throw new SnapException("The filter class '" + filterDefinition.getFilterClassName() + "' could not be loaded by "
                    + this.classLoader, e);
            } catch (InstantiationException e) {
                throw new SnapException("The filter class '" + filterDefinition.getFilterClassName() + "' could not be instantiated", e);
            } catch (IllegalAccessException e) {
                throw new SnapException("The filter class '" + filterDefinition.getFilterClassName()
                    + "' could not be instantiated due to access restrictions", e);
            }
        }
        return filtersMap;
    }
    
    private void processUrlPatternFilterMappings(SnapServletContext snapServletContext) throws SnapException {
        for (UrlPatternFilterMappingDefinition definition : this.webXml.getUrlPatternFilterMappingDefinitions()) {
            Filter filter = this.filtersMap.get(definition.getFilterName()).getInstance();
            this.urlPatternFilterMappings.add(new UrlPatternFilterMapping(filter, ManagerUtils.expandMapping(definition.getUrlPattern(), snapServletContext), definition.getFilterDispatcherTypes()));
        }
    }

	private void processServletNameFilterMappings() {
		for (ServletNameFilterMappingDefinition definition : this.webXml.getServletNameFilterMappingDefinitions()) {
            Filter filter = this.filtersMap.get(definition.getFilterName()).getInstance();
            this.servletNameFilterMappings.add(new ServletNameFilterMapping(filter, definition.getServletName(), definition.getFilterDispatcherTypes()));            
        }
	}
    
    void init() throws ServletException {
        try {
            ManagerUtils.doWithThreadContextClassLoader(this.classLoader, new ClassLoaderCallback<Void>() {
                public Void doWithClassLoader() throws ServletException {
                    for (FilterHolder filterHolder : filters) {                        
                        FilterConfig config = new ImmutableFilterConfig(filterHolder.getDefinition(), snapServletContexts.get(0));
                        try {
                            filterHolder.getInstance().init(config);
                        } catch (ServletException se) {
                            // TODO Log which filter failed
                            throw se;
                        }
                    }        
                    return null;
                }
            });
        } catch (IOException e) {
            throw new ServletException("Unexpected IOException from filter init", e);
        }
    }
    
    void destroy() {
        for (FilterHolder holder : this.filters) {            
            holder.getInstance().destroy();
        }
        this.filters.clear();
    }
    
    Filter[] findMatches(String path, String servletName, FilterDispatcherType dispatcherType) {
        List<Filter> filters = new ArrayList<Filter>();
        
        for (UrlPatternFilterMapping filterMapping : this.urlPatternFilterMappings) {
            if (filterMapping.matches(path, dispatcherType)) {
                filters.add(filterMapping.getFilter());
            }
        }                
        
        for (ServletNameFilterMapping filterMapping : this.servletNameFilterMappings) {
            if (filterMapping.matches(servletName, dispatcherType)) {
                filters.add(filterMapping.getFilter());
            }
        }    
        
        return filters.toArray(new Filter[filters.size()]);     
    }
    
    private static abstract class FilterMapping {
        
        private final Filter filter;
        
        protected final Set<FilterDispatcherType> dispatcherTypes;
        
        private FilterMapping(Filter filter, Set<FilterDispatcherType> dispatcherTypes) {
            this.filter = filter;
            this.dispatcherTypes = dispatcherTypes;
        }
        
        Filter getFilter() {
            return this.filter;
        }
    }
    
    private static class UrlPatternFilterMapping extends FilterMapping {
        
        private final FilterUrlPatternMatcher patternMatcher;
        
        private UrlPatternFilterMapping(Filter filter, String pattern, Set<FilterDispatcherType> dispatcherTypes) {
            super(filter, dispatcherTypes);
            this.patternMatcher = new FilterUrlPatternMatcher(pattern);
        }
        
        private boolean matches(String path, FilterDispatcherType dispatcherType) {
            return this.dispatcherTypes.contains(dispatcherType) && this.patternMatcher.matches(path);
        }
    }
    
    private static class ServletNameFilterMapping extends FilterMapping {
        
        private final String servletName;
        
        private ServletNameFilterMapping(Filter filter,  String servletName, Set<FilterDispatcherType> dispatcherTypes) {
            super(filter, dispatcherTypes);
            this.servletName = servletName;
        }
        
        private boolean matches(String servletName, FilterDispatcherType dispatcherType) {
            return this.dispatcherTypes.contains(dispatcherType) && this.servletName.equals(servletName);
        }
    }
    
	public void addSnapServletContext(SnapServletContext snapServletContext) {
		this.snapServletContexts.add(snapServletContext);
		processUrlPatternFilterMappings(snapServletContext);
	}
}
