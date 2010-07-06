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
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

class ImmutableFilterChain implements FilterChain {
    
    private final Filter[] filters;
    
    private final AtomicInteger index = new AtomicInteger(0);
    
    private final Servlet servlet;
    
    ImmutableFilterChain(Filter[] filters, Servlet servlet) {
        this.filters = filters;
        this.servlet = servlet;
    }

    /** 
     * {@inheritDoc}
     */
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        int index = this.index.getAndIncrement();
        
        if (index < this.filters.length) {
            Filter filter = filters[index];
            filter.doFilter(request, response, this);            
        } else {
            servlet.service(request, response);
        }
    }
}
