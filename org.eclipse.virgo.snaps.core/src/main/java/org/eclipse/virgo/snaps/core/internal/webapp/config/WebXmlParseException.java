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


public class WebXmlParseException extends RuntimeException{

    private static final long serialVersionUID = 6538975102106101117L;

    public WebXmlParseException() {
        super();
    }

    public WebXmlParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebXmlParseException(String message) {
        super(message);
    }

    public WebXmlParseException(Throwable cause) {
        super(cause);
    }

}
