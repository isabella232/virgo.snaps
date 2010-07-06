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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;

import org.eclipse.virgo.snaps.core.internal.webapp.config.FilterDispatcherType;


class FilterChainFactory {

    private final FilterManager filterManager;

    public FilterChainFactory(FilterManager filterManager) {
        this.filterManager = filterManager;
    }

    FilterChain createFilterChain(String path, Servlet servlet, String servletName, FilterDispatcherType dispatcherType) {
        Filter[] filters = this.filterManager.findMatches(path, servletName, dispatcherType);
        return new ImmutableFilterChain(filters, servlet);
    }
}
