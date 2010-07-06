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

final class PathUrlPattern implements UrlPattern {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PREFIX = "/";

    private static final String SUFFIX = "/*";

    private final String pattern;

    public PathUrlPattern(String pattern) {
        this.pattern = pattern.substring(0, pattern.length() - SUFFIX.length());
    }

    public static boolean isPathPattern(String pattern) {
        return pattern != null && pattern.startsWith(PREFIX) && pattern.endsWith(SUFFIX);
    }

    public boolean matches(String path) {
        boolean matches = path != null && path.startsWith(this.pattern);
        if (matches) {
            logger.info("Path '{}' matches pattern '{}'", path, this.pattern);
        } else {
            logger.info("Path '{}' does not match pattern '{}'", path, this.pattern);
        }
        return matches;
    }

    /**
     * {@inheritDoc}
     */
    public PathTranslation translate(String servletPath) {
        String pathInfo = servletPath.substring(this.pattern.length());
        logger.info("Translated path '{}' to '{}'", servletPath, pathInfo);
        return new ImmutablePathTranslation(this.pattern, pathInfo);
    }
}
