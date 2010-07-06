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
 * TODO Document FilterUrlPatternMatcher
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 *
 * TODO Document concurrent semantics of FilterUrlPatternMatcher
 *
 */
public class FilterUrlPatternMatcher {
    
    private final UrlPattern urlPattern;
    
    public FilterUrlPatternMatcher(String pattern) {
        this.urlPattern = UrlPatternFactory.create(pattern);
    }
    
    public boolean matches(String requestUri) {
        return urlPattern.matches(requestUri);
    }
}
