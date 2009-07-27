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
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class CouldNotParseTasksException extends RuntimeException {

	private static final long	serialVersionUID	= 8235687460628044894L;

	public CouldNotParseTasksException() {
		super();
	}

	public CouldNotParseTasksException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouldNotParseTasksException(String message) {
		super(message);
	}

	public CouldNotParseTasksException(Throwable cause) {
		super(cause);
	}

}
