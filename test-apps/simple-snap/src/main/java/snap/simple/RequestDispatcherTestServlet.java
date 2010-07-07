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

package snap.simple;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestDispatcherTestServlet extends HttpServlet {

    private static final long serialVersionUID = 4167184064530582217L;

    /** 
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String test = request.getParameter("test");
        doTest(test, request, response);
    }
    
    private void doTest(String test, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("forward-jsp".equals(test)) {
            doJspForward(request, response);
        } else if ("forward-servlet".equals(test)) {
            doServletForward(request, response);
        } else if ("forward-host-jsp".equals(test)) {
            doHostJspForward(request, response);
        } else if ("forward".equals(test)) {            
            doForward(request, response);
        } else if ("forward-path".equals(test)) {
            doPathMappingForward(request, response);
        } else if ("forward-filter-servlet".equals(test)) {
            doFilterServletForward(request, response);
        }
    }

    private void doJspForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/index.jsp");        
        requestDispatcher.forward(request, response);
    }
    
    private void doServletForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/test1");        
        requestDispatcher.forward(request, response);
    }
    
    private void doFilterServletForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/filterTest1");        
        requestDispatcher.forward(request, response);
    }
    
    private void doForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/forward");        
        requestDispatcher.forward(request, response);
    }
    
    private void doPathMappingForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/fa/test");        
        requestDispatcher.forward(request, response);
    }
    
    private void doHostJspForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/../index.jsp");        
        requestDispatcher.forward(request, response);
    }        
}
