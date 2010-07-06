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

final class ExtensionUrlPattern implements UrlPattern {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PREFIX = "*.";

    private final String pattern;

    public static boolean isExtensionPattern(String pattern) {
        return pattern != null && pattern.startsWith(PREFIX);
    }

    public ExtensionUrlPattern(String pattern) {
        this.pattern = pattern.substring(PREFIX.length());
    }

    public boolean matches(String path) {
        boolean matches = path != null && path.endsWith(this.pattern);
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
        logger.info("Did not translate path '{}'", servletPath);
        return new ImmutablePathTranslation(servletPath, null);
    }
}
