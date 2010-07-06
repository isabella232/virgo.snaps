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

package org.eclipse.virgo.snaps.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

class RequestUtils {
    
    static void assertContent(String content, String contextPath, String path) throws Exception {
        InputStream is = requestForInputStream(contextPath, path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        assertEquals(content, reader.readLine());
    }
    
    static void assertContentContains(String expectedContent, String contextPath, String path) throws Exception {
        String actualContent = getContent(contextPath, path);
        assertTrue("Content was expected to contain '" + expectedContent + "' but actual content was '" + actualContent + "'", actualContent.indexOf(expectedContent) > -1);
    }
    
    static void assertContentDoesNotContain(String expectedContent, String contextPath, String path) throws Exception {
        String actualContent = getContent(contextPath, path);
        assertTrue(actualContent.indexOf(expectedContent) == -1);
    }
    
    private static String getContent(String contextPath, String path) throws Exception {
        InputStream is = requestForInputStream(contextPath, path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder contentBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            contentBuilder.append(line);
        }
        return contentBuilder.toString();
    }
    
    static InputStream requestForInputStream(String contextPath, String path) throws Exception {
        URL url = new URL("http://localhost:8080" + contextPath + path);
        InputStream is = url.openStream();
        return is;
    }

    static Properties requestForProperties(String contextPath, String path) throws Exception {
        InputStream inputStream = requestForInputStream(contextPath, path);
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }
}
