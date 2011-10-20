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

/**
 * A <code>ListenerDefinition</code> instance represents a web.xml <code>&lt;listener&gt;</code> entry.
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Implementations <strong>must</strong> be thread-safe.
 * 
 */
public interface ListenerDefinition {

    /**
     * Returns the class name of the defined listener
     * 
     * @return the defined listener's class name
     */
    String getListenerClassName();
}
