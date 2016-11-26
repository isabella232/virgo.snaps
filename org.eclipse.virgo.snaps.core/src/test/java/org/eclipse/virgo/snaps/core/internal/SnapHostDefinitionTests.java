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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.virgo.util.osgi.manifest.VersionRange;
import org.junit.Test;


public class SnapHostDefinitionTests {

    @Test
    public void testStandard() {
    	Set<SnapHostDefinition> definitions = SnapHostDefinition.parse("travel;version=\"[1.2, 1.3)\"");
		assertEquals(1, definitions.size());

		SnapHostDefinition definition = definitions.iterator().next();
		assertEquals("travel", definition.getSymbolicName());
		assertEquals(new VersionRange("[1.2, 1.3)"), definition.getVersionRange());
    }
    
	@Test
	public void testWithoutRange() {
		Set<SnapHostDefinition> definitions = SnapHostDefinition.parse("travel");
		assertEquals(1, definitions.size());

		SnapHostDefinition definition = definitions.iterator().next();
		assertEquals("travel", definition.getSymbolicName());
		assertEquals(VersionRange.NATURAL_NUMBER_RANGE, definition.getVersionRange());
	}
    
	@Test
	public void testMultiple() throws Exception {
		Set<SnapHostDefinition> definitions = SnapHostDefinition.parse("travel,ski;version=\"[1.4, 2)\"");

		Map<String, VersionRange> expected = new HashMap<>();
		expected.put("travel", VersionRange.NATURAL_NUMBER_RANGE);
		expected.put("ski", new VersionRange("[1.4, 2)"));
		assertEquals(expected.size(), definitions.size());

		for (SnapHostDefinition host : definitions) {
			VersionRange expectedRange = expected.get(host.getSymbolicName());
			
			assertNotNull("Could not find a header for " + host.getSymbolicName(), expectedRange);
			assertEquals(expectedRange, host.getVersionRange());
		}
	}
}
