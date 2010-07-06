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

import java.util.Set;

/**
 * A <code>FilterMappingDefinition</code> represents the common portions of a web.xml <code>&lt;filter-mapping&gt;</code> entry.
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Implementations <strong>must</strong> be thread-safe.
 * 
 * 
 * @see UrlPatternFilterMappingDefinition
 * @see ServletNameFilterMappingDefinition
 */
public interface FilterMappingDefinition {

    /**
     * Returns the name of the filter referenced in this filter mapping.
     * 
     * @return the name of the filter.
     */
    String getFilterName();

    /**
     * Returns the <code>Set</code> of dispatcher types for which the mapped filter
     * is enabled  per the &lt;dispatcher&gt; elements in the definition's
     * configuration.
     * @return the mapping's dispatcher types
     */
    Set<FilterDispatcherType> getFilterDispatcherTypes();    
}
