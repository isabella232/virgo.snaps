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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UrlPatternMatcher {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<Mapping> exactMappings = Collections.synchronizedList(new ArrayList<Mapping>());

    private final List<Mapping> pathMappings = Collections.synchronizedList(new ArrayList<Mapping>());

    private final List<Mapping> extensionMappings = Collections.synchronizedList(new ArrayList<Mapping>());

    private volatile Mapping defaultMapping;

    public void addMapping(String name, String pattern) {
        logger.info("Adding mapping '{}' with pattern '{}'", name, pattern);
        UrlPattern urlPattern = UrlPatternFactory.create(pattern);
        Mapping entry = new Mapping(urlPattern, name, pattern);
        if (urlPattern instanceof ExactUrlPattern) {
            this.exactMappings.add(entry);
        } else if (urlPattern instanceof ExtensionUrlPattern) {
            this.extensionMappings.add(entry);
        } else if (urlPattern instanceof PathUrlPattern) {
            this.pathMappings.add(entry);
            sortPathMappings();
        } else if (urlPattern instanceof DefaultUrlPattern) {
            this.defaultMapping = entry;
        } else {
            logger.error("Unrecognized UrlPattern type '{}'", pattern.getClass());
            throw new IllegalStateException("Unrecognised UrlPattern type '" + pattern.getClass() + "'");
        }
    }

    private void sortPathMappings() {
        Collections.sort(this.pathMappings, new PathSpecificityComparator());
    }

    public Mapping match(String path) {
        Mapping match = findMatchIn(path, this.exactMappings);

        if (match == null) {
            match = findMatchIn(path, this.pathMappings);
            if (match == null) {
                match = findMatchIn(path, this.extensionMappings);
                if (match == null) {
                    Mapping m = this.defaultMapping;
                    if (m != null && m.getPattern().matches(path)) {
                        match = m;
                    }
                }
            }
        }

        if (match != null) {
            logger.info("Matched path '{}' with mapping '{}'", path, match.getName());
        } else {
            logger.warn("Unable to match path '{}'", path);
        }
        return match;
    }

    private Mapping findMatchIn(String path, List<Mapping> mappings) {
        for (Mapping entry : mappings) {
            if (entry.getPattern().matches(path)) {
                return entry;
            }
        }
        return null;
    }

    private static final class PathSpecificityComparator implements Comparator<Mapping>, Serializable {

        private static final long serialVersionUID = 3703208057785098804L;

        public int compare(Mapping e1, Mapping e2) {
            Integer s1 = specificityOf(e1);
            Integer s2 = specificityOf(e2);
            return s2.compareTo(s1);
        }

        private Integer specificityOf(Mapping e) {
            String pattern = e.getRawPattern();
            return countOccurencesOf(pattern, '/');
        }

        private int countOccurencesOf(String pattern, char c) {
            int count = 0;
            for (int x = 0; x < pattern.length(); x++) {
                if (c == pattern.charAt(x)) {
                    count++;
                }
            }
            return count;
        }

    }
}
