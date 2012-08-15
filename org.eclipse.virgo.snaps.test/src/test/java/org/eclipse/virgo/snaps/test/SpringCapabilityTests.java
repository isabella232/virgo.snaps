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

import java.io.IOException;

import org.junit.Test;

import org.eclipse.virgo.nano.deployer.api.core.DeploymentException;
import org.eclipse.virgo.nano.deployer.api.core.DeploymentIdentity;

public class SpringCapabilityTests extends AbstractCapabilityTests {
    
    private DeploymentIdentity snap;
    
    public void deployHostAndSnap() throws DeploymentException {
        deployHost();
        snap = deploy("target/test-apps/spring-snap.jar");
    }

    public void undeployHostAndSnap() throws DeploymentException {
        getDeployer().undeploy(snap);
        undeployHost();
    }
    
    @Test
    public void directWrite() throws Exception {
        deployHostAndSnap();
        try {
            RequestUtils.assertContent("OK", "/simple-host", "/spring/s/write");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            undeployHostAndSnap();
        }
    }
    
    @Test
    public void jspView() throws Exception {
        deployHostAndSnap();
        try {
            RequestUtils.assertContent("JSP.", "/simple-host", "/spring/s/withViewName");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            undeployHostAndSnap();
        }
    }
}
