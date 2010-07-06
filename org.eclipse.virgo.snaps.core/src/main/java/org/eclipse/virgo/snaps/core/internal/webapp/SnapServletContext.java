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

package org.eclipse.virgo.snaps.core.internal.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.eclipse.virgo.snaps.core.internal.SnapException;
import org.osgi.framework.Bundle;

import com.springsource.util.common.IterableEnumeration;

/**
 * TODO Document SnapServletContext
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 *
 * TODO Document concurrent semantics of SnapServletContext
 *
 */
public class SnapServletContext implements ServletContext {
    
    private final ServletContext delegate;
    
    private final Bundle snapBundle;
    
    private final String snapContextPath;
    
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    public SnapServletContext(ServletContext delegate, Bundle snapBundle, String snapContextPath) {
        this.delegate = delegate;
        this.snapBundle = snapBundle;
        this.snapContextPath = snapContextPath;
    }

    /**
     * @param name
     * @return
     * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name) {
        Object attribute = this.attributes.get(name);
        if (attribute == null) {
            attribute = delegate.getAttribute(name);
        }
        return attribute; 
    }

    /**
     * @return
     * @see javax.servlet.ServletContext#getAttributeNames()
     */
    @SuppressWarnings("unchecked")
    public Enumeration<?> getAttributeNames() {
        Set<String> attributeNamesSet = new HashSet<String>(this.attributes.keySet());
        IterableEnumeration<String> delegateAttributeNames = new IterableEnumeration<String>((Enumeration<String>)delegate.getAttributeNames());        
        for (String delegateAttributeName : delegateAttributeNames) {
            attributeNamesSet.add(delegateAttributeName);
        }
        Vector<String> attributeNames = new Vector<String>();
        for (String attributeName : attributeNamesSet) {
            attributeNames.add(attributeName);
        }
        return attributeNames.elements();
    }

    /**
     * @param uripath
     * @return
     * @see javax.servlet.ServletContext#getContext(java.lang.String)
     */
    public ServletContext getContext(String uripath) {
        return delegate.getContext(uripath);
    }

    /**
     * @return
     * @see javax.servlet.ServletContext#getContextPath()
     */
    public String getContextPath() {
        return delegate.getContextPath();
    }

    /**
     * @param name
     * @return
     * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String name) {
        return delegate.getInitParameter(name);
    }

    /**
     * @return
     * @see javax.servlet.ServletContext#getInitParameterNames()
     */
    public Enumeration<?> getInitParameterNames() {
        return delegate.getInitParameterNames();
    }

    /**
     * @return
     * @see javax.servlet.ServletContext#getMajorVersion()
     */
    public int getMajorVersion() {
        return delegate.getMajorVersion();
    }

    /**
     * @param file
     * @return
     * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
     */
    public String getMimeType(String file) {
        return delegate.getMimeType(file);
    }

    /**
     * @return
     * @see javax.servlet.ServletContext#getMinorVersion()
     */
    public int getMinorVersion() {
        return delegate.getMinorVersion();
    }

    /**
     * @param name
     * @return
     * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
     */
    public RequestDispatcher getNamedDispatcher(String name) {
        return delegate.getNamedDispatcher(name);
    }

    /**
     * @param path
     * @return
     * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
     */
    public String getRealPath(String path) {
        return delegate.getRealPath(path);
    }

    /**
     * @param path
     * @return
     * @see javax.servlet.ServletContext#getRequestDispatcher(java.lang.String)
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return delegate.getRequestDispatcher(path);
    }

    /**
     * @param path
     * @return
     * @throws MalformedURLException
     * @see javax.servlet.ServletContext#getResource(java.lang.String)
     */
    public URL getResource(String path) throws MalformedURLException {
		if (path == null || !path.startsWith("/")) {
			throw new MalformedURLException(String.format("'%s' is not a valid resource path", path));
		}
        URL resource = getLocalResource(path);
        if (resource == null) {
            resource = delegate.getResource(path);
        }
        return resource;
    }

    private URL getLocalResource(String path) {
        URL entry = this.snapBundle.getEntry(path);
        if (entry == null && path.startsWith(this.snapContextPath)) {
            entry = this.snapBundle.getEntry(path.substring(this.snapContextPath.length()));
        }
        return entry;
    }

    /**
     * @param path
     * @return
     * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream(String path) {
        URL resource = getLocalResource(path);
        if (resource != null) {
            try {
                return resource.openStream();
            } catch (IOException e) {
                throw new SnapException("Failed to open stream for resource " + resource + " in bundle " + this.snapBundle, e);
            }
        } else {
            return delegate.getResourceAsStream(path);
        }
    }

    /**
     * @param path
     * @return
     * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
     */
    public Set<?> getResourcePaths(String path) {       
        Enumeration<?> entryPaths = this.snapBundle.getEntryPaths(path);
        if (entryPaths == null) {
            return null;
        } else {
            Set<String> resourcePaths = new HashSet<String>();
            while (entryPaths.hasMoreElements()) {
                String entryPath = (String)entryPaths.nextElement();
                if (path.startsWith("/") && !entryPath.startsWith("/")) {
                    entryPath = "/" + entryPath;
                }
                resourcePaths.add((String)entryPath);
            }
            return resourcePaths;
        }                
    }

    /**
     * @return
     * @see javax.servlet.ServletContext#getServerInfo()
     */
    public String getServerInfo() {
        return delegate.getServerInfo();
    }

    /**
     * @param name
     * @return
     * @throws ServletException
     * @deprecated
     * @see javax.servlet.ServletContext#getServlet(java.lang.String)
     */
    public Servlet getServlet(String name) throws ServletException {
        return delegate.getServlet(name);
    }

    /**
     * @return
     * @see javax.servlet.ServletContext#getServletContextName()
     */
    public String getServletContextName() {
        return delegate.getServletContextName();
    }

    /**
     * @return
     * @deprecated
     * @see javax.servlet.ServletContext#getServletNames()
     */
    public Enumeration<?> getServletNames() {
        return delegate.getServletNames();
    }

    /**
     * @return
     * @deprecated
     * @see javax.servlet.ServletContext#getServlets()
     */
    public Enumeration<?> getServlets() {
        return delegate.getServlets();
    }

    /**
     * @param exception
     * @param msg
     * @deprecated
     * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
     */
    public void log(Exception exception, String msg) {
        delegate.log(exception, msg);
    }

    /**
     * @param message
     * @param throwable
     * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
     */
    public void log(String message, Throwable throwable) {
        delegate.log(message, throwable);
    }

    /**
     * @param msg
     * @see javax.servlet.ServletContext#log(java.lang.String)
     */
    public void log(String msg) {
        delegate.log(msg);
    }

    /**
     * @param name
     * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name) {
        delegate.removeAttribute(name);
    }

    /**
     * @param name
     * @param object
     * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object object) {
        this.attributes.put(name, object);
    }
    
    public String getSnapContextPath() {
        return this.snapContextPath;
    }
}
