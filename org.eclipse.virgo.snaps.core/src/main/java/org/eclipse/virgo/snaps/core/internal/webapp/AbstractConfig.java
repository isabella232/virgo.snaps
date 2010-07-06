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

import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.eclipse.virgo.snaps.core.internal.webapp.config.WebComponentDefinition;


abstract class AbstractConfig {
    
    private final WebComponentDefinition definition;
    
    private final ServletContext servletContext;
    
    public AbstractConfig(WebComponentDefinition definition, ServletContext servletContext) {
        this.definition = definition;
        this.servletContext = servletContext;
    }            
    
    public String getInitParameter(String name) {
        return this.definition.getInitParameters().get(name);        
    }

    public Enumeration<?> getInitParameterNames() {
        Vector<Object> parameterNames = new Vector<Object>();
        Set<Entry<String, String>> entries = this.definition.getInitParameters().entrySet();
        for (Entry<String, String> entry : entries) {
            parameterNames.add(entry.getKey());
        }
        return parameterNames.elements();      
    }
        
    public ServletContext getServletContext() {
        return servletContext;
    }
}
