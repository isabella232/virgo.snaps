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

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.eclipse.virgo.snaps.core.internal.webapp.config.FilterDefinition;


/**
 * An immutable implementation of {@link FilterConfig}
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * Thread-safe.
 * 
 */
public final class ImmutableFilterConfig extends AbstractConfig implements FilterConfig {
    
    private final FilterDefinition filterDefinition;
        
    public ImmutableFilterConfig(FilterDefinition definition, ServletContext servletContext) {
        super(definition, servletContext);
        this.filterDefinition = definition;
    }
    
    /** 
     * {@inheritDoc}
     */
    public String getFilterName() {
        return this.filterDefinition.getFilterName();
    }   

    /**
     * {@inheritDoc}
     */
    public Enumeration<String> getInitParameterNames(){
    	return Collections.enumeration(this.filterDefinition.getInitParameters().keySet());
    }     
}
