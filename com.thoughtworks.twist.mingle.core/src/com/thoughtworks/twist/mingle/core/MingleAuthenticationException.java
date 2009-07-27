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
public class MingleAuthenticationException extends RuntimeException {

	private static final long	serialVersionUID	= 2890327033966785233L;

	public MingleAuthenticationException() {
		super();
	}

	public MingleAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MingleAuthenticationException(String message) {
		super(message);
	}

	public MingleAuthenticationException(Throwable cause) {
		super(cause);
	}
}
