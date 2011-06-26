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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO Document StandardWebXmlValidator
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * TODO Document concurrent semantics of StandardWebXmlValidator
 */
class StandardWebXmlValidator implements WebXmlValidator {

    /**
     * {@inheritDoc}
     */
    public void validate(WebXml xml) throws WebXmlValidationException {

        validateServletMappingToServletDefinition(xml);
        validateFilterMappingToFilterDefintion(xml);

    }

    /**
     * @param xml
     */
    private void validateFilterMappingToFilterDefintion(WebXml xml) {
        // check if there are filter mapping and no matching servlets
        // only check servlet names

        // check if there are filter mappings for non existent servlet definitions.
        Set<String> names = readServeltNames(xml.getServletDefinitions());
        Set<String> snameFromFilterMapping = readServeltNames(xml.getServletNameFilterMappingDefinitions());
        snameFromFilterMapping.removeAll(names);
        if (!snameFromFilterMapping.isEmpty()) {
            throw new WebXmlValidationException(String.format("WebXml contains filter mapping without matching servlet definition.  '[%s]'",
                snameFromFilterMapping));
        }

    }

    /**
     * @param xml
     */
    private void validateServletMappingToServletDefinition(WebXml xml) {

        // check if there are filter mappings for non existent servlet definitions.
        Set<String> names = readServeltNames(xml.getServletDefinitions());
        Set<String> mappingNames = readServeltNames(xml.getServletMappingDefinitions());

        mappingNames.removeAll(names);
        if (!mappingNames.isEmpty()) {
            throw new WebXmlValidationException(String.format("WebXml contains servlet mapping without matching servlet definition.  '[%s]'",
                mappingNames));
        }
    }

    private Set<String> readServeltNames(ServletNameAware[] nameAware) {
        Set<String> names = new HashSet<String>();
        for (ServletNameAware def : nameAware) {
            names.add(def.getServletName());
        }
        return names;
    }

}
