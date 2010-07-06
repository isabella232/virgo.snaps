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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
abstract class HttpSessionWrapper implements HttpSession {

    private final HttpSession delegate;

    public HttpSessionWrapper(HttpSession delegate) {
        this.delegate = delegate;
    }

    public Object getAttribute(String name) {
        return delegate.getAttribute(name);
    }

    public Enumeration<?> getAttributeNames() {
        return delegate.getAttributeNames();
    }

    public long getCreationTime() {
        return delegate.getCreationTime();
    }

    public String getId() {
        return delegate.getId();
    }

    public long getLastAccessedTime() {
        return delegate.getLastAccessedTime();
    }

    public int getMaxInactiveInterval() {
        return delegate.getMaxInactiveInterval();
    }

    public ServletContext getServletContext() {
        return delegate.getServletContext();
    }

    public HttpSessionContext getSessionContext() {
        return delegate.getSessionContext();
    }

    public Object getValue(String name) {
        return getAttribute(name);
    }

    public String[] getValueNames() {
        Enumeration<?> attributeNames = getAttributeNames();
        List<String> names = new ArrayList<String>();
        while(attributeNames.hasMoreElements()) {
            String name = (String) attributeNames.nextElement();
            names.add(name);
        }
        return (String[]) names.toArray(new String[names.size()]);
    }

    public void invalidate() {
        delegate.invalidate();
    }

    public boolean isNew() {
        return delegate.isNew();
    }

    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        delegate.removeAttribute(name);
    }

    public void removeValue(String name) {
        removeAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        delegate.setAttribute(name, value);
    }

    public void setMaxInactiveInterval(int interval) {
        delegate.setMaxInactiveInterval(interval);
    }

}
