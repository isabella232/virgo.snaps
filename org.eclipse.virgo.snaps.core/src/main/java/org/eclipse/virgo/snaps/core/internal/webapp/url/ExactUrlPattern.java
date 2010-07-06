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

final class ExactUrlPattern implements UrlPattern {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String pattern;

    public ExactUrlPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean matches(String path) {
        boolean matches = this.pattern.equals(path);
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
