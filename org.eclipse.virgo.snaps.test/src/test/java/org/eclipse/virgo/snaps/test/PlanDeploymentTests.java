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

import java.io.File;

import org.junit.Test;

import org.eclipse.virgo.kernel.deployer.core.DeploymentIdentity;


/**
 * Tests for the deployment of Snaps referenced by a plan file.
 */
public class PlanDeploymentTests extends AbstractDeployerTests {
    
    @Test
    public void unscopedPlan() throws Exception {
        DeploymentIdentity deployed = getDeployer().deploy(new File("src/test/resources/unscoped.plan").toURI());
        
        try {
            RequestUtils.assertContent("index", "/simple-host", "/index.jsp");
            RequestUtils.assertContent("test1", "/simple-host", "/simple/test1");
        } finally {
            getDeployer().undeploy(deployed);
        }
    }
    
    @Test
    public void scopedPlan() throws Exception {
        DeploymentIdentity deployed = getDeployer().deploy(new File("src/test/resources/scoped.plan").toURI());
        // Allow snap to be published before proceeding with test
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        
        try {
            RequestUtils.assertContent("index", "/simple-host", "/index.jsp");
            RequestUtils.assertContent("test1", "/simple-host", "/simple/test1");
        } finally {
            getDeployer().undeploy(deployed);
        }
    }
}
