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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * TODO Document DefaultUrlPattern
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 *
 * TODO Document concurrent semantics of DefaultUrlPattern
 *
 */
public class DefaultUrlPattern implements UrlPattern {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final String DEFAULT_MAPPING = "/";
    
    public static boolean isDefaultPattern(String pattern) {
        return DEFAULT_MAPPING.equals(pattern);
    }

    /** 
     * {@inheritDoc}
     */
    public boolean matches(String path) {
        boolean matches = path.startsWith(DEFAULT_MAPPING);
        if (matches) {
            logger.info("Path '{}' matches pattern '{}'", path, DEFAULT_MAPPING);
        } else {
            logger.info("Path '{}' does not match pattern '{}'", path, DEFAULT_MAPPING);
        }
        return matches;
    }

    /** 
     * {@inheritDoc}
     */
    public PathTranslation translate(String servletPath) {        
        String pathInfo = null;
        
        int index = servletPath.indexOf(DEFAULT_MAPPING, 1);
        if (index > -1) {
            pathInfo = servletPath.substring(index);
            servletPath = servletPath.substring(0, index);            
        }
        logger.info("Translated path '{}' to '{}'", servletPath, pathInfo);
        return new ImmutablePathTranslation(servletPath, pathInfo);
    }
}
