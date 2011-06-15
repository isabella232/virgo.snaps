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

package org.eclipse.virgo.snaps.core.internal.webapp.container;

import java.util.Enumeration;

import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.eclipse.virgo.snaps.core.internal.webapp.SnapServletContext;


public final class SnapHttpSession extends HttpSessionWrapper {

    private static final String QUALIFIED_NAME_MARKER = "##";

    private final SnapServletContext snapServletContext;

    public SnapHttpSession(HttpSession delegate, SnapServletContext snapServletContext) {
        super(delegate);
        this.snapServletContext = snapServletContext;
    }

    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(qualifyName(name));
        if (value == null) {
            value = super.getAttribute(name);
        }
        return value;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new UnqualifyingEnumerationWrapper(super.getAttributeNames());
    }

    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(qualifyName(name));
    }

    @Override
    public void setAttribute(String name, Object value) {
        super.setAttribute(qualifyName(name), value);
    }

    @SuppressWarnings("deprecation")
	@Override
	public HttpSessionContext getSessionContext() {
		throw new RuntimeException(new OperationNotSupportedException("getSessionContext is deprecated"));
	}

    private String qualifyName(String baseName) {
        return QUALIFIED_NAME_MARKER + this.snapServletContext.getSnapContextPath() + "." + baseName;
    }

    private String unqualifyName(String name) {
        String unqualified = name;
        if (unqualified.startsWith(QUALIFIED_NAME_MARKER)) {
            int index = unqualified.indexOf(".");
            if (index > -1) {
                unqualified = name.substring(index + 1);
            }
        }
        return unqualified;
    }

    private final class UnqualifyingEnumerationWrapper implements Enumeration<String> {

        private final Enumeration<?> delegate;

        public UnqualifyingEnumerationWrapper(Enumeration<?> delegate) {
            this.delegate = delegate;
        }

        public boolean hasMoreElements() {
            return delegate.hasMoreElements();
        }

        public String nextElement() {
            String element = (String) delegate.nextElement();
            if (element != null) {
                element = unqualifyName(element);
            }
            return element;
        }

    }

}
