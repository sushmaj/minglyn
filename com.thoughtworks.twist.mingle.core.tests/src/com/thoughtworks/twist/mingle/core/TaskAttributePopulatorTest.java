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

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class TaskAttributePopulatorTest extends TestCase {

	public void testPopulatesTaskAttributeFromHash() throws Exception {
		TaskData taskData = new TaskData(new MingleTaskAttributeMapper(new TaskRepository(MingleConstants.CONNECTOR_KIND, "http://foo/bar")), MingleConstants.CONNECTOR_KIND, "http://foo/bar", "123");
		TaskAttribute root = taskData.getRoot();

		new TaskAttributePopulator().createAttribute(taskData, new Attribute("created_at", "2008-11-11T05:11:56Z", "date"));

	}
	
	/**
	 * <pre>
	 * public Date getCreationDate() {
	 * 	return getDateValue(TaskAttribute.DATE_CREATION);
	 * }
	 * 
	 * private Date getDateValue(String attributeKey) {
	 * 	TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeKey);
	 * 	if (attribute != null) {
	 * 		return taskData.getAttributeMapper().getDateValue(attribute);
	 * 	}
	 * 	return null;
	 * }
	 * </pre>
	 */
}
