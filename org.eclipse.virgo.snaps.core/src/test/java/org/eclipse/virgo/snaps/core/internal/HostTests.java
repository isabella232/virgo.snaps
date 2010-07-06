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

package org.eclipse.virgo.snaps.core.internal;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import javax.servlet.ServletContext;

import org.eclipse.virgo.snaps.core.internal.Host;
import org.junit.Test;
import org.osgi.framework.Bundle;

public class HostTests {

    @Test
    public void testEqualsAndHashCode() {

        Bundle bundle = createMock(Bundle.class);
        ServletContext context = createMock(ServletContext.class);

        Host a = new Host(bundle, context, null);
        Host b = new Host(bundle, context, null);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
