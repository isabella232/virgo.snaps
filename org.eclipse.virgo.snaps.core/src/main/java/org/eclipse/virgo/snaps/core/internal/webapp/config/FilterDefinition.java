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

package org.eclipse.virgo.snaps.core.internal.webapp.config;

/**
 * A <code>FilterDefinition</code> instance represents a web.xml <code>&lt;filter&gt;</code> entry.
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Implementations <strong>must</strong> be thread-safe.
 * 
 */
public interface FilterDefinition extends WebComponentDefinition {

    /**
     * Returns the name of the filter
     * @return the filter's name
     */
    String getFilterName();

    
    /**
     * Returns the class name of the defined filter
     * @return the defined filter's class name
     */    
    String getFilterClassName();
}
