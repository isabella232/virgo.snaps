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

package org.eclipse.virgo.snaps.core.internal.webapp;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.eclipse.virgo.snaps.core.internal.webapp.config.ServletDefinition;


/**
 * An immutable implementation of {@link ServletConfig}
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * Thread-safe.
 * 
 */
public final class ImmutableServletConfig extends AbstractConfig implements ServletConfig {

    private final ServletDefinition servletDefinition;        

    public ImmutableServletConfig(ServletDefinition servletDefinition, ServletContext servletContext) {
        super(servletDefinition, servletContext);        
        this.servletDefinition = servletDefinition;
    }

    /**
     * {@inheritDoc}
     */
    public String getServletName() {
        return this.servletDefinition.getServletName();
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration<String> getInitParameterNames(){
    	return Collections.enumeration(this.servletDefinition.getInitParameters().keySet());
    }
}
