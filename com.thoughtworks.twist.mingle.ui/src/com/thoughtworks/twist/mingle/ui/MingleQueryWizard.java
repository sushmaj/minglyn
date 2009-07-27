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
package com.thoughtworks.twist.mingle.ui;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleQueryWizard extends RepositoryQueryWizard {

	private IRepositoryQuery	query;

	public MingleQueryWizard(TaskRepository taskRepository, IRepositoryQuery queryToEdit) {
		super(taskRepository);
		this.query = queryToEdit;
		initializeWizard(queryToEdit);
	}

	private void initializeWizard(IRepositoryQuery queryToEdit) {
		setWindowTitle(queryToEdit == null ? "New Mingle Query" : "Edit Mingle Query");
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public void addPages() {
		addPage(new MingleCustomQueryPage(getTaskRepository(), query));
	}
}
