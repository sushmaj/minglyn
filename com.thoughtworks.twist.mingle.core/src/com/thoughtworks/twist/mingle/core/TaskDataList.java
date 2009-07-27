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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class TaskDataList extends ArrayList<TaskData> {

	public TaskDataList(List<TaskData> tasks) {
		super(tasks);
	}

	public TaskDataList() {
		super();
	}

	public TaskData getTaskWithId(String taskId) {
		for (TaskData task : this) {
			if (task.getTaskId().equals(taskId))
				return task;
		}
		return null;
	}

}
