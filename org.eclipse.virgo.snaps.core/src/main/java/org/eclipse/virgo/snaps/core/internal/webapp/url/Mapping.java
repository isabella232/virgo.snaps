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


public final class Mapping {

    private final UrlPattern pattern;

    private final String name;
    
    private final String rawPattern;

    public Mapping(UrlPattern pattern, String name, String rawPattern) {
        this.pattern = pattern;
        this.name = name;
        this.rawPattern = rawPattern;
    }

    public String getName() {
        return name;
    }
    
    String getRawPattern() {
        return this.rawPattern;
    }
    
    UrlPattern getPattern() {
        return this.pattern;
    }
    
    public PathTranslation translate(String servletPath) {
        return this.pattern.translate(servletPath);
    }
}
