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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class TaskAttributePopulator {

	public void createAttribute(TaskData taskData, Attribute attribute) {
		String taskAttributeKey = attributeMapRegistry().get(attribute.name);

		if (taskAttributeKey == null) {
			taskAttributeKey = attribute.name;
		}

		AttributeCreator attributeCreator = typeRegistry().get(attribute.type);
		attributeCreator.create(taskData, taskAttributeKey, attribute.value);
	}

	private Map<String, String> attributeMapRegistry() {
		HashMap<String, String> registry = new HashMap<String, String>();
		registry.put("created_at", TaskAttribute.DATE_CREATION);
		registry.put("description", TaskAttribute.DESCRIPTION);
		registry.put("name", TaskAttribute.SUMMARY);
		registry.put("updated_at", TaskAttribute.DATE_MODIFICATION);
		registry.put("card_type_name", TaskAttribute.TASK_KIND);
		registry.put("id", "task.id");
		return registry;
	}

	private Map<String, AttributeCreator> typeRegistry() {
		HashMap<String, AttributeCreator> hashMap = new HashMap<String, AttributeCreator>();
		hashMap.put("date", new DateAttributeCreator());
		hashMap.put("integer", new IntegerAttributeCreator());
		hashMap.put("string", new StringAttributeCreator());
		return hashMap;
	}

	public class StringAttributeCreator implements AttributeCreator {
		public void create(TaskData taskData, String taskAttributeKey, String value) {
			TaskAttribute root = taskData.getRoot();
			TaskAttribute taskAttribute = root.createMappedAttribute(taskAttributeKey);
			taskData.getAttributeMapper().setValue(taskAttribute, value);
		}
	}

	public class IntegerAttributeCreator implements AttributeCreator {
		public void create(TaskData taskData, String taskAttributeKey, String value) {
			TaskAttribute root = taskData.getRoot();
			TaskAttribute taskAttribute = root.createMappedAttribute(taskAttributeKey);
			taskData.getAttributeMapper().setIntegerValue(taskAttribute, Integer.valueOf(value));
		}
	}

	public class DateAttributeCreator implements AttributeCreator {
		SimpleDateFormat	dateFormatter	= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		public void create(TaskData taskData, String taskAttributeKey, String value) {
			TaskAttribute root = taskData.getRoot();
			TaskAttribute taskAttribute = root.createMappedAttribute(taskAttributeKey);
			try {
				taskData.getAttributeMapper().setDateValue(taskAttribute, dateFormatter.parse(value));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	}

	interface AttributeCreator {
		void create(TaskData taskData, String taskAttributeKey, String value);
	}
}
