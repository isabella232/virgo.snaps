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

package org.eclipse.virgo.snaps.test;

import org.junit.Test;

public class HostCapabilityTests extends AbstractCapabilityTests {     

    @Test
    public void testHostExactMapping() throws Exception {
        deployHost();
        try {
            RequestUtils.assertContent("exact", "/simple-host", "/exact");
        } finally {
            undeployHost();
        }
    }

    @Test
    public void testHostPathMapping() throws Exception {
        deployHost();
        try {                    
            RequestUtils.assertContent("/foo", "/simple-host", "/path/foo");   
            RequestUtils.assertContent("/bar/baz", "/simple-host", "/path/bar/baz");
        } finally {
            undeployHost();
        }
    }

    @Test
    public void testHostJspMapping() throws Exception {
        deployHost();
        try {
            RequestUtils.assertContent("index", "/simple-host", "/index.jsp");
        } finally {
            undeployHost();
        }
    }    
}
