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

package org.eclipse.virgo.snaps.core;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.virgo.snaps.core.internal.Snap;

/**
 * TODO Document RequestRouter
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 *
 * TODO Document concurrent semantics of RequestRouter
 *
 */
public final class RequestRouter {

    private static final String PATH_ELEMENT_SEPARATOR = "/";
    
    private final SnapRegistry snapRegistry;
    
    private final ServletContext servletContext;
    
    public RequestRouter(SnapRegistry snapRegistry, ServletContext servletContext) {
        this.snapRegistry = snapRegistry;
        this.servletContext = servletContext;
    }
    
    void service(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Snap snap = findSnap(request);
        
        if (snap != null) {
            snap.handleRequest(request, response);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    public void forward(String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = determineSnapContextPath(request);
        servletContext.getRequestDispatcher(contextPath + path).forward(request, response);
    }
    
    public void include(String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = determineSnapContextPath(request);
        servletContext.getRequestDispatcher(contextPath + path).include(request, response);
    }    
    
    private Snap findSnap(HttpServletRequest request) {
        String contextPath = determineSnapContextPath(request);
        return this.snapRegistry.findSnapByContextPath(contextPath);
    }
    
    void destroy() {
        this.snapRegistry.destroy();
    }
    
    private String determineSnapContextPath(HttpServletRequest request) {
    	String result;
    	String includeServletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
    	if (includeServletPath != null) {
    		result = includeServletPath;
    	} else {
    		result = request.getServletPath();
    	}

    	String checking = result;
    	// /dog/cat/web/page
    	
    	int index2 = result.indexOf(PATH_ELEMENT_SEPARATOR, 1);
    	if (index2 > -1){
    		String result2 = result.substring(0, index2);
    	}
    	//return result2;
    	
    	Snap snap = this.snapRegistry.findSnapByContextPath(checking);
    	while(snap == null){
    		int index = checking.lastIndexOf(PATH_ELEMENT_SEPARATOR);
    		if(index <= 0){
    			return result;
    		}
    		checking = checking.substring(0, index);
    		snap = this.snapRegistry.findSnapByContextPath(checking);
    	}
    	return checking;
    }
    
       
    
}
