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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import org.eclipse.virgo.nano.deployer.api.core.DeploymentIdentity;

public class BasicDeployLifecycleTests extends AbstractDeployerTests {

    @Test
    public void basicDeploy() throws Exception {
        // this just tests whether or not we can actually deploy snaps/wars
        DeploymentIdentity host = deploy("target/test-apps/simple-host.jar");
        DeploymentIdentity snap = deploy("target/test-apps/simple-snap.jar");
        try {
            assertNotNull(host);
            assertNotNull(snap);
        } finally {
            getDeployer().undeploy(snap);
            getDeployer().undeploy(host);
        }
    }
}
