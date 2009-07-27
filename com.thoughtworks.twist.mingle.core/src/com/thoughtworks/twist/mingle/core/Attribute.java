/*******************************************************************************
 * Copyright (c) 2008 ThoughtWorks, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ThoughtWorks, Inc. - initial API and implementation
 *******************************************************************************/
package com.thoughtworks.twist.mingle.core;

/**
 * Represents an attribute in the mingle cards.xml format.
 * 
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class Attribute {

	final String	type;
	final String	value;
	final String	name;

	public Attribute(String name, String value, String type) {
		this.type = type;
		this.name = name;
		this.value = value;
	}

}
