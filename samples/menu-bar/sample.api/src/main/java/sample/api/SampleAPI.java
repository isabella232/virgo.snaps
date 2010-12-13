/*******************************************************************************
 * Copyright (c) 2010, Pouzin Society, http://www.pouzinsociety.org
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Patsy Phelan, Pouzin Society - initial contribution
 *******************************************************************************/

package sample.api;

import java.util.List;

public interface SampleAPI {
	List<SampleDao> get();
	void add(SampleDao dao);
	void remove(SampleDao dao);
}
