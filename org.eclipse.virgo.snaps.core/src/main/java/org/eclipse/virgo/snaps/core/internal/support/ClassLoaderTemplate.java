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

package org.eclipse.virgo.snaps.core.internal.support;


/**
 * TODO Document ClassLoaderTemplate
 * <p />
 *
 * <strong>Concurrent Semantics</strong><br />
 *
 * TODO Document concurrent semantics of ClassLoaderTemplate
 *
 */
public class ClassLoaderTemplate {
    
    public <T> T doWithThreadContextClassLoader(ClassLoader classLoader, ClassLoaderCallback<T> callback) throws Exception {
        Thread currentThread = Thread.currentThread();
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(classLoader);
            return callback.doWithClassLoader();
        } finally {
            currentThread.setContextClassLoader(contextClassLoader);
        }
    }
    
    public static interface ClassLoaderCallback<T> {
        T doWithClassLoader() throws Exception;
    }
}
