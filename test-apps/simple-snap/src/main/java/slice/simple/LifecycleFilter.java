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

package snap.simple;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class LifecycleFilter implements Filter, LifecycleFilterMBean {
    
    private final ObjectName mBeanName;
    
    public LifecycleFilter() throws MalformedObjectNameException {
        mBeanName = new ObjectName("snaps:type=FilterLifecycleTest");
    }
    
    public void destroy() {
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(mBeanName);
        } catch (Exception e) {            
            throw new RuntimeException(e);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(this, mBeanName);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
