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

import java.util.Map;


/**
 * A base interface for web.xml filter and servlet definitions.
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Implementations <strong>must</strong> be thread-safe.
 *
 * @see ServletDefinition
 * @see FilterDefinition
 */
public interface WebComponentDefinition {
    
    /**
     * Returns a map of the init parameters associated with this web component definition
     * @return the definition's init parameters
     */
    Map<String, String> getInitParameters();
}
