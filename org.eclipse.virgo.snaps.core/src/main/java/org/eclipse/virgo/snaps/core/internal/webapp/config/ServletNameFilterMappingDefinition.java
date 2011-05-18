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
 * A <code>ServletNameFilterMappingDefinition</code> represents a web.xml <code>&lt;filter-mapping&gt;</code> entry,
 * which contains a <code>&lt;servlet-name</code> element, and therefore maps the referenced filter to a particular
 * servlet.
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Implementations <strong>must</strong> be thread-safe.
 * 
 * 
 */
public interface ServletNameFilterMappingDefinition extends FilterMappingDefinition, ServletNameAware {

    /**
     * Returns the name of the servlet to which this mapping applies.
     * 
     * @return the servlet's name
     */
    String getServletName();
}
