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

import javax.servlet.Servlet;

import org.eclipse.virgo.snaps.core.internal.webapp.config.ServletDefinition;


final class ServletHolder extends DefinitionAndInstanceHolder<ServletDefinition, Servlet>{
    
    ServletHolder(ServletDefinition definition, Servlet servlet) {
        super(definition, servlet);
    }    
}
