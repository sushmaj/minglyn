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
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.EncodingUtil;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleUtils {

	public static String getQueryUrlAfterEncodingURL(String fullUrl) throws UnsupportedEncodingException {
		String queryUrl = fullUrl.substring(fullUrl.indexOf('?') + 1);
		String[] split = queryUrl.split("&");
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (String nameValuePair : split) {
			if (nameValuePair.contains("=")) {
				int indexOfEquals = nameValuePair.indexOf('=');
				String name = nameValuePair.substring(0, indexOfEquals);
				String value = nameValuePair.substring(indexOfEquals + 1);
				name = URLDecoder.decode(name, "utf-8");
				value = URLDecoder.decode(value, "utf-8");
				pairs.add(new NameValuePair(name, value));
			}
		}
		String formUrlEncode = EncodingUtil.formUrlEncode(pairs.toArray(new NameValuePair[0]), "utf-8");
		return formUrlEncode;
	}
}
