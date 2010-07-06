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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * TODO Document HttpServletRequestWrapper
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * TODO Document concurrent semantics of HttpServletRequestWrapper
 * 
 */
class HttpServletRequestWrapper implements HttpServletRequest {

    private final HttpServletRequest delegate;

    public HttpServletRequestWrapper(HttpServletRequest delegate) {
        this.delegate = delegate;

    }

    /**
     * @param name
     * @return
     * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name) {
        return delegate.getAttribute(name);
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getAttributeNames()
     */
    public Enumeration<?> getAttributeNames() {
        return delegate.getAttributeNames();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getAuthType()
     */
    public String getAuthType() {
        return delegate.getAuthType();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return delegate.getCharacterEncoding();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getContentLength()
     */
    public int getContentLength() {
        return delegate.getContentLength();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getContentType()
     */
    public String getContentType() {
        return delegate.getContentType();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getContextPath()
     */
    public String getContextPath() {
        return delegate.getContextPath();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getCookies()
     */
    public Cookie[] getCookies() {
        return delegate.getCookies();
    }

    /**
     * @param name
     * @return
     * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
     */
    public long getDateHeader(String name) {
        return delegate.getDateHeader(name);
    }

    /**
     * @param name
     * @return
     * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
     */
    public String getHeader(String name) {
        return delegate.getHeader(name);
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
     */
    public Enumeration<?> getHeaderNames() {
        return delegate.getHeaderNames();
    }

    /**
     * @param name
     * @return
     * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
     */
    public Enumeration<?> getHeaders(String name) {
        return delegate.getHeaders(name);
    }

    /**
     * @return
     * @throws IOException
     * @see javax.servlet.ServletRequest#getInputStream()
     */
    public ServletInputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    /**
     * @param name
     * @return
     * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
     */
    public int getIntHeader(String name) {
        return delegate.getIntHeader(name);
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getLocalAddr()
     */
    public String getLocalAddr() {
        return delegate.getLocalAddr();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getLocale()
     */
    public Locale getLocale() {
        return delegate.getLocale();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getLocales()
     */
    public Enumeration<?> getLocales() {
        return delegate.getLocales();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getLocalName()
     */
    public String getLocalName() {
        return delegate.getLocalName();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getLocalPort()
     */
    public int getLocalPort() {
        return delegate.getLocalPort();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getMethod()
     */
    public String getMethod() {
        return delegate.getMethod();
    }

    /**
     * @param name
     * @return
     * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        return delegate.getParameter(name);
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    public Map<?,?> getParameterMap() {
        return delegate.getParameterMap();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    public Enumeration<?> getParameterNames() {
        return delegate.getParameterNames();
    }

    /**
     * @param name
     * @return
     * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String name) {
        return delegate.getParameterValues(name);
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
     */
    public String getPathTranslated() {
        return delegate.getPathTranslated();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getProtocol()
     */
    public String getProtocol() {
        return delegate.getProtocol();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getQueryString()
     */
    public String getQueryString() {
        return delegate.getQueryString();
    }

    /**
     * @return
     * @throws IOException
     * @see javax.servlet.ServletRequest#getReader()
     */
    public BufferedReader getReader() throws IOException {
        return delegate.getReader();
    }

    /**
     * @param path
     * @return
     * @deprecated
     * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
     */
    public String getRealPath(String path) {
        return delegate.getRealPath(path);
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getRemoteAddr()
     */
    public String getRemoteAddr() {
        return delegate.getRemoteAddr();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getRemoteHost()
     */
    public String getRemoteHost() {
        return delegate.getRemoteHost();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getRemotePort()
     */
    public int getRemotePort() {
        return delegate.getRemotePort();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
     */
    public String getRemoteUser() {
        return delegate.getRemoteUser();
    }

    /**
     * @param path
     * @return
     * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return delegate.getRequestDispatcher(path);
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
     */
    public String getRequestedSessionId() {
        return delegate.getRequestedSessionId();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     */
    public String getRequestURI() {
        return delegate.getRequestURI();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getRequestURL()
     */
    public StringBuffer getRequestURL() {
        return delegate.getRequestURL();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getScheme()
     */
    public String getScheme() {
        return delegate.getScheme();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getServerName()
     */
    public String getServerName() {
        return delegate.getServerName();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#getServerPort()
     */
    public int getServerPort() {
        return delegate.getServerPort();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getSession()
     */
    public HttpSession getSession() {
        return delegate.getSession();
    }

    /**
     * @param create
     * @return
     * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
     */
    public HttpSession getSession(boolean create) {
        return delegate.getSession(create);
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
     */
    public Principal getUserPrincipal() {
        return delegate.getUserPrincipal();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
     */
    public boolean isRequestedSessionIdFromCookie() {
        return delegate.isRequestedSessionIdFromCookie();
    }

    /**
     * @return
     * @deprecated
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
     */
    public boolean isRequestedSessionIdFromUrl() {
        return delegate.isRequestedSessionIdFromUrl();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
     */
    public boolean isRequestedSessionIdFromURL() {
        return delegate.isRequestedSessionIdFromURL();
    }

    /**
     * @return
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
     */
    public boolean isRequestedSessionIdValid() {
        return delegate.isRequestedSessionIdValid();
    }

    /**
     * @return
     * @see javax.servlet.ServletRequest#isSecure()
     */
    public boolean isSecure() {
        return delegate.isSecure();
    }

    /**
     * @param role
     * @return
     * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
     */
    public boolean isUserInRole(String role) {
        return delegate.isUserInRole(role);
    }

    /**
     * @param name
     * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name) {
        delegate.removeAttribute(name);
    }

    /**
     * @param name
     * @param o
     * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object o) {
        delegate.setAttribute(name, o);
    }

    /**
     * @param env
     * @throws UnsupportedEncodingException
     * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
     */
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        delegate.setCharacterEncoding(env);
    }

    /**
     * {@inheritDoc}
     */
    public String getPathInfo() {
        return this.delegate.getPathInfo();
    }

    /**
     * {@inheritDoc}
     */
    public String getServletPath() {
        return this.delegate.getServletPath();
    }

}
