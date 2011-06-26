/*
 * This file is part of the Eclipse Virgo project.
 *
 * Copyright (c) 2011 Chariot Solutions, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    dsklyut - initial contribution
 */

package org.eclipse.virgo.snaps.core.internal.webapp.config;


/**
 * TODO Document WebXmlValidationException
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 * TODO Document concurrent semantics of WebXmlValidationException
 */
public class WebXmlValidationException extends WebXmlParseException {

    private static final long serialVersionUID = 9203103242166889912L;

    /**
     * 
     */
    WebXmlValidationException() {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    WebXmlValidationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    WebXmlValidationException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    WebXmlValidationException(Throwable arg0) {
        super(arg0);
    }

    
}
