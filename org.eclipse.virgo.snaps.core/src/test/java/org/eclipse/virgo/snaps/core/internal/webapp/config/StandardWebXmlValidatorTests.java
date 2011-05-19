/*
 * This file is part of the Eclipse Virgo project.
 *
 * Copyright (c) 2011 Chariot Solutions, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    dsklyut - initial contribution
 */

package org.eclipse.virgo.snaps.core.internal.webapp.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for StandardWebXmlValidator
 * <p />
 */
public class StandardWebXmlValidatorTests {

    private final StandardWebXmlValidator validator = new StandardWebXmlValidator();

    @Test(expected = WebXmlValidationException.class)
    public void testMissingServletDefinitionReferencedInServletMapping() {
        WebXml webXml = parse("src/test/resources/invalid-servlet-mapping-web.xml");
        validator.validate(webXml);
    }

    @Test(expected = WebXmlValidationException.class)
    public void testMissingServletDefinitionReferencedInServletNameFilterMapping() {
        WebXml webXml = parse("src/test/resources/invalid-filter-servletname-web.xml");
        validator.validate(webXml);
    }

    private WebXml parse(String file) {
        WebXmlParser parser = new WebXmlParser();
        MutableWebXml webXml = new MutableWebXml();
        try {
            parser.parse(new FileInputStream(file), webXml);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
        return webXml;
    }
}
