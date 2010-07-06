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

import org.eclipse.virgo.snaps.core.RequestRouter;
import org.eclipse.virgo.snaps.core.internal.webapp.SnapServletContext;
import org.eclipse.virgo.snaps.core.internal.webapp.config.WebXml;


/**
 * TODO Document VirtualContainerFactory
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 *
 * TODO Document concurrent semantics of VirtualContainerFactory
 *
 */
public final class VirtualContainerFactory {
    
    public static VirtualContainer fromWebXml(WebXml webXml, SnapServletContext servletContext, ClassLoader classLoader, RequestRouter requestRouter) {
        return new StandardVirtualContainer(webXml, servletContext, classLoader, requestRouter);
    }
}
