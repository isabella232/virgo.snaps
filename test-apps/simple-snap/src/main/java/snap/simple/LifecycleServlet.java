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

import java.lang.management.ManagementFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class LifecycleServlet extends HttpServlet implements LifecycleServletMBean {
    
    private static final long serialVersionUID = -6368209667730097375L;
    
    private final ObjectName mBeanName;
    
    public LifecycleServlet() throws MalformedObjectNameException {
        mBeanName = new ObjectName("snaps:type=Test");
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        super.destroy();        
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(mBeanName);
        } catch (Exception e) {            
            throw new RuntimeException(e);
        }
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(this, mBeanName);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
