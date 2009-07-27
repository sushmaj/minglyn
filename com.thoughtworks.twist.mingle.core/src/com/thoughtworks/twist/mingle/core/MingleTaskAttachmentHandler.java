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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleTaskAttachmentHandler extends AbstractTaskAttachmentHandler {

	private final MingleRepositoryConnector	mingleRepositoryConnector;

	public MingleTaskAttachmentHandler(MingleRepositoryConnector mingleRepositoryConnector) {
		this.mingleRepositoryConnector = mingleRepositoryConnector;
	}

	public boolean canGetContent(TaskRepository repository, ITask task) {
		return true;
	}

	public boolean canPostContent(TaskRepository repository, ITask task) {
		return true;
	}

	public InputStream getContent(TaskRepository repository, ITask task, TaskAttribute attachmentAttribute, IProgressMonitor monitor)
			throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void postContent(TaskRepository repository, ITask task, AbstractTaskAttachmentSource source, String comment,
			TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws CoreException {

		try {
			monitor.beginTask("Sending attachment", IProgressMonitor.UNKNOWN);

			MingleClient client = MingleClientFactory.getDefault().getClient(repository,
					new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));

			String description = source.getDescription();
			String filename = source.getName();

			if (description == null) {
				throw new CoreException(new Status(IStatus.WARNING, Activator.PLUGIN_ID,
						"A description is required when submitting attachments."));
			}

			client.attachFile(task.getTaskId(), comment, description, source, filename, monitor);

		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to submit attachment", e));
		} finally {
			monitor.done();
		}

	}

}
