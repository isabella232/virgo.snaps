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

package sample.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sample.api.SampleDao;
import sample.api.SampleAPI;
public class SampleAPIImpl implements SampleAPI {
	
	List<SampleDao> daoList = new ArrayList<SampleDao>();
	
	public SampleAPIImpl() {
		synchronized (daoList) {
			daoList.add(new SampleDao("PreLoaded"));
		}
	}

	public void add(SampleDao arg0) {
		synchronized (daoList) {
			daoList.add(arg0);			
		}	
	}

	public List<SampleDao> get() {
		List<SampleDao> copy;
		synchronized (daoList) {
			copy = Collections.unmodifiableList(daoList);
		}
		return copy;
	}

	public void remove(SampleDao arg0) {
		synchronized (daoList) {
			daoList.remove(arg0);
		}
	}
}
