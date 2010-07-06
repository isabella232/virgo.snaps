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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.adaptor.LocationManager;
import org.eclipse.osgi.launch.EquinoxFactory;
import org.junit.After;
import org.junit.Before;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;

public abstract class AbstractEquinoxLaunchingTests {

    private volatile Framework framework;

    @Before
    public final void startFramework() throws Exception {
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("osgi.clean", "true");
        configuration.put(LocationManager.PROP_INSTALL_AREA, "target/equinox");
        configuration.put(LocationManager.PROP_CONFIG_AREA, "target/equinox");
        this.framework = new EquinoxFactory().newFramework(configuration);
        this.framework.start();
    }

    protected BundleContext getBundleContext() {
        return this.framework.getBundleContext();
    }

    @After
    public final void stopFramework() throws Exception {
        this.framework.stop();
        this.framework.waitForStop(5000);
    }
}
