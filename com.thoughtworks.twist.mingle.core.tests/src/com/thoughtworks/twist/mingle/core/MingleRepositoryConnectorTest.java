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

public class MingleRepositoryConnectorTest extends TestCase {

	private MingleRepositoryConnector	connector;

	public void testGetRepositoryUrlFromTaskUrl() throws Exception {
		assertEquals("http://foo/projects/bar_project", connector.getRepositoryUrlFromTaskUrl("http://foo/projects/bar_project/cards/123"));
	}

	public void testGetTaskIdFromTaskUrl() {
		assertEquals("123", connector.getTaskIdFromTaskUrl("http://foo/projects/bar_project/cards/123"));
	}

	public void testGetTaskUrl() {
		assertEquals("http://foo/projects/bar_project/cards/1234", connector.getTaskUrl("http://foo/projects/bar_project", "1234"));
	}

	protected void setUp() throws Exception {
		super.setUp();
		connector = new MingleRepositoryConnector();
	}

}
