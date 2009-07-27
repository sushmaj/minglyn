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

import org.eclipse.mylyn.tasks.ui.editors.BrowserFormPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleBrowserFormPage extends BrowserFormPage {

	private final String	url;

	public MingleBrowserFormPage(FormEditor editor, String title, String url) {
		super(editor, title);
		this.url = url;
	}

	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		getBrowser().setUrl(url);
	}

}
