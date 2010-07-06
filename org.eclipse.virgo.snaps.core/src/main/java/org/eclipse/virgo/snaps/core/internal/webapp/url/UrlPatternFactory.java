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

final class UrlPatternFactory {

    private static final Logger logger = LoggerFactory.getLogger(UrlPatternFactory.class);

    public static UrlPattern create(String pattern) {
        if (PathUrlPattern.isPathPattern(pattern)) {
            logger.info("Pattern '{}' is a PathUrlPattern", pattern);
            return new PathUrlPattern(pattern);
        } else if (ExtensionUrlPattern.isExtensionPattern(pattern)) {
            logger.info("Pattern '{}' is an ExtensionUrlPattern", pattern);
            return new ExtensionUrlPattern(pattern);
        } else if (DefaultUrlPattern.isDefaultPattern(pattern)) {
            logger.info("Pattern '{}' is a DefaultUrlPattern", pattern);
            return new DefaultUrlPattern();
        } else {
            logger.info("Pattern '{}' is an ExactUrlPattern", pattern);
            return new ExactUrlPattern(pattern);
        }
    }
}
