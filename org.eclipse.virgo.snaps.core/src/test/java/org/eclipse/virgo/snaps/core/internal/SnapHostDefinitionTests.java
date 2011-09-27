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

import static org.junit.Assert.assertEquals;

import org.eclipse.virgo.snaps.core.internal.SnapHostDefinition;
import org.junit.Test;

import org.eclipse.virgo.util.osgi.manifest.internal.VersionRange;


public class SnapHostDefinitionTests {

    @Test
    public void testStandard() {
        SnapHostDefinition def = SnapHostDefinition.parse("travel;version=\"[1.2, 1.3)\"");
        assertEquals("travel", def.getSymbolicName());
        assertEquals(new VersionRange("[1.2, 1.3)"), def.getVersionRange());
    }
    
    @Test
    public void testWithoutRange() {
        SnapHostDefinition def = SnapHostDefinition.parse("travel");
        assertEquals("travel", def.getSymbolicName());
        assertEquals(VersionRange.NATURAL_NUMBER_RANGE, def.getVersionRange());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInvalid() {
        SnapHostDefinition.parse("travel,hotels");
    }
}
