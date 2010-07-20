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

import org.eclipse.virgo.snaps.core.internal.Host;
import org.eclipse.virgo.snaps.core.internal.Snap;
import org.eclipse.virgo.snaps.core.internal.deployer.SnapFactory;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.virgo.medic.eventlog.EventLogger;
import org.eclipse.gemini.web.tomcat.spi.WebBundleClassLoaderFactory;

public final class WebAppSnapFactory implements SnapFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Bundle snapBundle;

    private final WebBundleClassLoaderFactory classLoaderFactory;

    private final EventLogger eventLogger;

    public WebAppSnapFactory(Bundle snapBundle, WebBundleClassLoaderFactory webBundleClassLoaderFactory, EventLogger eventLogger) {
        this.snapBundle = snapBundle;
        this.classLoaderFactory = webBundleClassLoaderFactory;
        this.eventLogger = eventLogger;
    }

    public Snap createSnap(Host host) {
        logger.info("Creating new snap that binds snap bundle '{}' to host bundle '{}'", this.snapBundle, host.getBundle());
        return new WebAppSnap(host, this.snapBundle, this.classLoaderFactory, this.eventLogger);
    }

}
