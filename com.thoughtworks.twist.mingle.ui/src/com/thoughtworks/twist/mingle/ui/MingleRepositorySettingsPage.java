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

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewRepositoryWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.thoughtworks.twist.mingle.core.MingleAuthenticationException;
import com.thoughtworks.twist.mingle.core.MingleClient;
import com.thoughtworks.twist.mingle.core.MingleConstants;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleRepositorySettingsPage extends WizardPage implements ITaskRepositoryPage {

	private TaskRepository				taskRepository;
	private Combo						serverUrlCombo;
	private AbstractRepositoryConnector	connector;
	private Text						repositoryLabel;
	private Text						userName;
	private Text						password;
	private Button						validateButton;

	public MingleRepositorySettingsPage(TaskRepository taskRepository) {
		super("Mingle Repository Settings");
		setTitle("Mingle Repository Settings");
		setMessage("Example: https://mingle.mycompany.com:8080/projects/my_project");

		this.taskRepository = taskRepository;
		this.connector = TasksUi.getRepositoryManager().getRepositoryConnector(getConnectorKind());
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, false));

		createURL(composite);
		createServerLabel(composite);
		createUsername(composite);
		createPassword(composite);
		createValidate(composite);

		if (getTaskRepository() != null) {
			setInitialProperties();
		}
		createHooks();

		setControl(composite);
	}

	private void setInitialProperties() {
		serverUrlCombo.setText(getTaskRepository().getUrl());
		repositoryLabel.setText(getTaskRepository().getRepositoryLabel());

		AuthenticationType http = AuthenticationType.HTTP;
		if (http != null) {
			userName.setText(getTaskRepository().getCredentials(http).getUserName());
			password.setText(getTaskRepository().getCredentials(AuthenticationType.HTTP).getPassword());
		}
	}

	public void applyTo(TaskRepository repository) {
		AuthenticationCredentials webCredentials = new AuthenticationCredentials(userName.getText(), password.getText());

		repository.setCredentials(AuthenticationType.HTTP, webCredentials, true);
		repository.setRepositoryLabel(repositoryLabel.getText());
	}

	public String getRepositoryUrl() {
		return serverUrlCombo.getText().replaceAll("/+$", "");
	}

	private void createHooks() {
		serverUrlCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateURL();
			}
		});
		userName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateUsername();
			}
		});
		password.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePass();
			}
		});

		validateButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				try {
					validateSettings();
					validateConnection();
				} catch (Exception exc) {
					// do nothing
				}
			}

		});

	}

	protected void validateConnection() throws InvocationTargetException, InterruptedException {
		clearMessages();
		MingleConnectionValidator validator = new MingleConnectionValidator(createTemporaryTaskRepository());
		getWizard().getContainer().run(false, false, validator);
	}

	private TaskRepository createTemporaryTaskRepository() {
		TaskRepository repository = new TaskRepository(MingleConstants.CONNECTOR_KIND, getRepositoryUrl());
		applyTo(repository);
		return repository;
	}

	private void validateSettings() {
		clearMessages();
		validateURL();
		validateUsername();
		validatePass();
	}

	private void clearMessages() {
		setErrorMessage(null);
		setMessage(null);
	}

	private void validatePass() {
		clearMessages();
		if (password.getText().trim().equals("")) {
			setErrorMessage("The password is empty.");
		}
	}

	private void validateUsername() {
		clearMessages();

		if (userName.getText().trim().equals("")) {
			setErrorMessage("The username is empty.");
		}
	}

	private void validateURL() {
		clearMessages();
		if (!isValidUrl()) {
			setErrorMessage("The project url must be of the form https://mingle.mycompany.com:8080/projects/my_project");
		}
	}

	private void createValidate(Composite composite) {
		validateButton = new Button(composite, SWT.PUSH);
		validateButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		validateButton.setText("&Validate");
		validateButton.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_SYNCHRONIZE_SMALL));
	}

	private void createPassword(Composite composite) {
		Label label4 = new Label(composite, SWT.NONE);
		label4.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		label4.setText("&Password:");

		password = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	}

	private void createUsername(Composite composite) {
		Label label3 = new Label(composite, SWT.NONE);
		label3.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		label3.setText("&Username:");

		userName = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		userName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	}

	private void createServerLabel(Composite composite) {
		Label label2 = new Label(composite, SWT.NONE);
		label2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		label2.setText("&Label:");

		repositoryLabel = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		repositoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	}

	private void createURL(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		label.setText("&Project URL:");

		serverUrlCombo = new Combo(composite, SWT.DROP_DOWN);
		serverUrlCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		addRepositoryTemplatesToServerUrlCombo();

		if (getTaskRepository() == null) {
			serverUrlCombo.setText("http://mingle.yourcompany.com:8080/projects/my_project");
		}
	}

	private boolean isValidUrl() {
		try {
			new URL(serverUrlCombo.getText());
		} catch (MalformedURLException e) {
			return false;
		}
		return true;
	}

	public String getConnectorKind() {
		return MingleConstants.CONNECTOR_KIND;
	}

	@SuppressWarnings("restriction")
	private void addRepositoryTemplatesToServerUrlCombo() {
		final RepositoryTemplateManager templateManager = TasksUiPlugin.getRepositoryTemplateManager();
		for (RepositoryTemplate template : templateManager.getTemplates(connector.getConnectorKind())) {
			serverUrlCombo.add(template.label);
		}
		serverUrlCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = serverUrlCombo.getText();
				RepositoryTemplate template = templateManager.getTemplate(connector.getConnectorKind(), text);
				if (template != null) {
					return;
				}
			}

		});
	}

	public TaskRepository getTaskRepository() {
		if (taskRepository == null) {
			IWizard wizard = getWizard();
			if (wizard instanceof NewRepositoryWizard)
				taskRepository = ((NewRepositoryWizard) wizard).getTaskRepository();
			if (wizard instanceof EditRepositoryWizard)
				taskRepository = ((EditRepositoryWizard) wizard).getRepository();
		}
		return taskRepository;
	}

	public class MingleConnectionValidator implements IRunnableWithProgress {
		private final TaskRepository	repository;

		public MingleConnectionValidator(TaskRepository repository) {
			this.repository = repository;
		}

		public void run(IProgressMonitor monitor) {
			try {
				MingleClient client = new MingleClient(repository, null);
				if (client.validate()) {
					setMessage("Authentication credentials are valid.", IMessageProvider.INFORMATION);
				}
				monitor.beginTask("Validating server settings", IProgressMonitor.UNKNOWN);
			} catch (MingleAuthenticationException e) {
				setErrorMessage("Could not authenticate user. Check username and password.");
			} catch (Exception e) {
				setErrorMessage("Could not connect to server: " + repository.getUrl());
			} finally {
				monitor.done();
			}
		}

	}

}
