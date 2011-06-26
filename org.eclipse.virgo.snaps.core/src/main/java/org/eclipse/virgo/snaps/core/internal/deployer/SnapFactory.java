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

package org.eclipse.virgo.snaps.core.internal.deployer;

import org.eclipse.virgo.snaps.core.internal.Snap;
import org.eclipse.virgo.snaps.core.internal.Host;

public interface SnapFactory {
    
    static final String FACTORY_NAME_PROPERTY = "snap.factory.host.name";

    static final String FACTORY_RANGE_PROPERTY = "snap.factory.host.range";

    Snap createSnap(Host host);
}
