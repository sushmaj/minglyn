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

import java.io.UnsupportedEncodingException;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleUtils {

	public static String getQueryUrlAfterReplacingSquareBrackets(String fullUrl) throws UnsupportedEncodingException {
		String substring = fullUrl.substring(fullUrl.indexOf('?') + 1);
		return substring.replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
	}
}
