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

package org.eclipse.virgo.snaps.core.internal.webapp.url;


/**
 * TODO Document ImmutablePathTranslation
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 *
 * TODO Document concurrent semantics of ImmutablePathTranslation
 *
 */
public class ImmutablePathTranslation implements PathTranslation {
    
    private final String pathInfo;
    
    private final String servletPath;

    public ImmutablePathTranslation(String servletPath, String pathInfo) {
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
    }

    /** 
     * {@inheritDoc}
     */
    public String getPathInfo() {
        return this.pathInfo;        
    }

    /** 
     * {@inheritDoc}
     */
    public String getServletPath() {
        return this.servletPath;
    }
}
