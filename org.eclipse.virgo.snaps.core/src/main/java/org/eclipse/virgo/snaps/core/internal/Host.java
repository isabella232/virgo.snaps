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

package org.eclipse.virgo.snaps.core.internal;

import javax.servlet.ServletContext;

import org.eclipse.virgo.snaps.core.RequestRouter;
import org.osgi.framework.Bundle;


/**
 * Represents a host that a snap is attached to.
 * 
 * 
 */
public final class Host {

    private final Bundle bundle;

    private final ServletContext servletContext;
    
    private final RequestRouter router;

    public Host(Bundle bundle, ServletContext servletContext, RequestRouter router) {
        this.bundle = bundle;
        this.servletContext = servletContext;
        this.router = router;
    }

    public RequestRouter getRouter() {
        return router;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result +  bundle.hashCode();
        result = prime * result + servletContext.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Host other = (Host) obj;
        return bundle.equals(other.bundle) && servletContext.equals(other.servletContext);
    }
    
    public String  toString() {
        return String.format("Host[bundle=%s,servletContext=%s]", this.bundle, this.servletContext);
    }
    
}
