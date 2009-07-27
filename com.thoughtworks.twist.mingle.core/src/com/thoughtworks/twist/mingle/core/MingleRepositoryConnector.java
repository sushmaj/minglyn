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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

public class MingleRepositoryConnector extends AbstractRepositoryConnector {

	private MingleTaskAttachmentHandler	attachmentHandler;

	public MingleRepositoryConnector() {
		attachmentHandler = new MingleTaskAttachmentHandler(this);
	}

	public boolean canCreateNewTask(TaskRepository repository) {
		return false;
	}

	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	public String getConnectorKind() {
		return MingleConstants.CONNECTOR_KIND;
	}

	public String getLabel() {
		return MingleConstants.LABEL;
	}

	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.lastIndexOf(MingleConstants.CARDS_BASE_URL);
		return index == -1 ? null : url.substring(0, index);
	}

	public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor) throws CoreException {
		return MingleClientFactory.getDefault().createMingleClient(taskRepository).getTaskData(taskId);
	}

	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		return taskFullUrl.substring(taskFullUrl.lastIndexOf(MingleConstants.CARDS_BASE_URL) + MingleConstants.CARDS_BASE_URL.length());
	}

	public String getTaskUrl(String repositoryUrl, String taskId) {
		return repositoryUrl + MingleConstants.CARDS_BASE_URL + taskId;
	}

	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		return false;
	}

	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector resultCollector,
			ISynchronizationSession event, IProgressMonitor monitor) {
		try {
			MingleClient client = MingleClientFactory.getDefault().getClient(repository, monitor);

			TaskDataList allTaskData = client.getAllTaskData(MingleUtils.getQueryUrlAfterReplacingSquareBrackets(query.getUrl()));

			for (TaskData taskData : allTaskData) {
				resultCollector.accept(taskData);
			}

		} catch (Exception e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not perform a query on mingle", e);
		}
		return Status.OK_STATUS;
	}

	public void updateRepositoryConfiguration(TaskRepository taskRepository, IProgressMonitor monitor) throws CoreException {

	}

	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		TaskMapper scheme = getTaskMapping(taskData);
		scheme.applyTo(task);
		task.setCompletionDate(scheme.getCompletionDate());

		task.setUrl(getTaskUrl(taskRepository.getRepositoryUrl(), taskData.getTaskId()));
	}

	public TaskMapper getTaskMapping(TaskData taskData) {
		return new TaskMapper(taskData);
	}

	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return attachmentHandler;
	}
	
}
