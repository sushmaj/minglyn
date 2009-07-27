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

import junit.framework.TestCase;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleUtilsTest extends TestCase {

	public void testReplacesSquareBracketsInMingleUrls() throws Exception {
		assertEquals(
				"color_by=priority&filters%5B%5D=%5BType%5D%5Bis%5D%5BCard%5D&filters%5B%5D=%5BOwner%5D%5Bis%5D%5Bnk%5D&group_by=iteration&tab=Plan+by+Iterations",
				MingleUtils
						.getQueryUrlAfterEncodingURL("https://foo/projects/twist_launch/cards/grid?color_by=priority&filters[]=[Type][is][Card]&filters[]=[Owner][is][nk]&group_by=iteration&tab=Plan+by+Iterations"));

	}

	public void testReplacesCommasInUrls() throws Exception {
		assertEquals(
				"lanes=foo%2Cbar%2Cbaz&tab=Plan+by+Iterations&filters%5B%5D=%5BType%5D%5Bis%5D%5BCard%5D",
				MingleUtils
						.getQueryUrlAfterEncodingURL("https://mingle05.thoughtworks.com/projects/tide/cards/grid?lanes=foo,bar,baz&tab=Plan+by+Iterations&filters%5B%5D=%5BType%5D%5Bis%5D%5BCard%5D&"));
	}

}
