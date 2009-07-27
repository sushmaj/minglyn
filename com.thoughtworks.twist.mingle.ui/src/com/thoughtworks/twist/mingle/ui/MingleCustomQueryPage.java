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
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.thoughtworks.twist.mingle.core.MingleConstants;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleCustomQueryPage extends AbstractRepositoryQueryPage {

	private static final String		LABEL_CUSTOM_TITLE	= "&Query Title:";
	private static final String		LABEL_CUSTOM_QUERY	= "Query &URL";
	private static final String		TITLE				= "Create query from URL";
	private static final String		DESCRIPTION			= "Enter the title and URL for the query";

	private Text					queryText;
	private Text					queryTitle;
	private final IRepositoryQuery	query;

	public MingleCustomQueryPage(TaskRepository taskRepository, IRepositoryQuery query) {
		super(TITLE, taskRepository, query);
		this.query = query;

		initializeWizardPage();
	}

	private void initializeWizardPage() {
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		setControl(composite);

		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(isPageComplete());
			}
		};

		final Label queryTitleLabel = new Label(composite, SWT.NONE);
		queryTitleLabel.setText(LABEL_CUSTOM_TITLE);

		queryTitle = new Text(composite, SWT.BORDER);
		queryTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		queryTitle.addModifyListener(modifyListener);
		queryTitle.setFocus();

		final Label queryUrlLabel = new Label(composite, SWT.NONE);
		queryUrlLabel.setText(LABEL_CUSTOM_QUERY);

		queryText = new Text(composite, SWT.BORDER);
		final GridData gd_queryText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_queryText.widthHint = 300;
		queryText.setLayoutData(gd_queryText);
		queryText.addModifyListener(modifyListener);

		if (query != null) {
			queryTitle.setText(query.getSummary());
			queryText.setText(query.getUrl());
		}
	}

	public String getQueryTitle() {
		return queryTitle.getText();
	}

	public boolean isPageComplete() {
		if (super.isPageComplete()) {
			if (queryText.getText().length() > 0) {
				return true;
			}
			setErrorMessage("Please specify query URL");
		}
		return false;
	}

	public void applyTo(IRepositoryQuery query) {
		query.setSummary(this.getQueryTitle());
		query.setUrl(queryText.getText());
		query.setAttribute(MingleConstants.ATTRIBUTE_MINGLE_QUERY_CUSTOM, Boolean.TRUE.toString());
	}

}
