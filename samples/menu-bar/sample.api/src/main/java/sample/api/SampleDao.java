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

public class SampleDao {
	String text;
	
	public SampleDao() {
		text = new String("Default");
	}
	
	public SampleDao(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override public boolean equals(Object aThat) {
	    //check for self-comparison
	    if ( this == aThat ) return true;

	    if ( !(aThat instanceof SampleDao) ) return false;
	    //cast to native object is now safe
	    SampleDao that = (SampleDao)aThat;

	    //now a proper field-by-field evaluation can be made
	    return this.getText().equals(that.getText());
	}
}
