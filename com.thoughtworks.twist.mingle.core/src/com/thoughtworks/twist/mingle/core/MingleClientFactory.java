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

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleClientFactory implements IRepositoryListener {

	private static MingleClientFactory		instance;

	private final Map<String, MingleClient>	clientByUrl	= new HashMap<String, MingleClient>();

	private MingleClientFactory() {
	}

	MingleClient createMingleClient(String projectUrl, String userName, String password) {
		TaskRepository taskRepository = new TaskRepository(MingleConstants.CONNECTOR_KIND, projectUrl);
		MingleTaskAttributeMapper taskAttributeMapper = new MingleTaskAttributeMapper(taskRepository);
		return new MingleClient(projectUrl, userName, password, taskAttributeMapper);
	}

	MingleClient createMingleClient(TaskRepository taskRepository) {
		MingleTaskAttributeMapper taskAttributeMapper = new MingleTaskAttributeMapper(taskRepository);
		return new MingleClient(taskRepository, taskAttributeMapper);
	}

	public synchronized static MingleClientFactory getDefault() {
		if (instance == null) {
			instance = new MingleClientFactory();
		}
		return instance;
	}

	public synchronized MingleClient getClient(TaskRepository taskRepository, IProgressMonitor monitor) throws MalformedURLException,
			CoreException {
		MingleClient client = clientByUrl.get(taskRepository.getRepositoryUrl());
		if (client == null) {
			client = MingleClientFactory.getDefault().createMingleClient(taskRepository);
			clientByUrl.put(taskRepository.getRepositoryUrl(), client);
		}
		return client;
	}

	public synchronized void repositoryAdded(TaskRepository repository) {
		removeClient(repository);
	}

	public synchronized void repositoryRemoved(TaskRepository repository) {
		removeClient(repository);
	}

	private void removeClient(TaskRepository repository) {
		clientByUrl.remove(repository.getRepositoryUrl());
	}

	public synchronized void repositorySettingsChanged(TaskRepository repository) {
		removeClient(repository);
	}

	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		// ignore
	}

}
