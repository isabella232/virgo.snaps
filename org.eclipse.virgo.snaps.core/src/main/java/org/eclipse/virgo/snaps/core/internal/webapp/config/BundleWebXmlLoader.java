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

package org.eclipse.virgo.snaps.core.internal.webapp.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.virgo.util.io.IOUtils;

public final class BundleWebXmlLoader {

    private static final Logger logger = LoggerFactory.getLogger(BundleWebXmlLoader.class);

    private static final String ENTRY_WEB_XML = "WEB-INF/web.xml";

    private static final String ENTRY_DEFAULT_WEB_XML = "META-INF/snaps/default-web.xml";

    public static WebXml loadWebXml(Bundle bundle) {
        MutableWebXml webXml = new MutableWebXml();

        URL entry = bundle.getEntry(ENTRY_WEB_XML);
        doParse(entry, webXml);

        entry = FrameworkUtil.getBundle(BundleWebXmlLoader.class).getEntry(ENTRY_DEFAULT_WEB_XML);
        doParse(entry, webXml);

        return webXml;
    }

    private static void doParse(URL webXmlUrl, MutableWebXml webXml) {
        if (webXmlUrl == null) {
            return;
        }
        InputStream stream = null;
        try {
            logger.info("Loading configuration from '{}'", webXmlUrl.toExternalForm());
            stream = webXmlUrl.openStream();
            new WebXmlParser().parse(stream, webXml);
        } catch (IOException e) {
            throw new WebXmlParseException("Unable to read '" + webXmlUrl + "'", e);
        } finally {
            if (stream != null) {
                IOUtils.closeQuietly(stream);
            }
        }
    }
}
