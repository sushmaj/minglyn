package com.thoughtworks.twist.mingle.ui;

import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.BrowserFormPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

import com.thoughtworks.twist.mingle.core.MingleConstants;

public class MylynTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	public boolean canCreatePageFor(TaskEditorInput input) {
		return input.getTask().getConnectorKind().equals(MingleConstants.CONNECTOR_KIND);
	}

	public IFormPage createPage(TaskEditor parentEditor) {
		ITask task = parentEditor.getTaskEditorInput().getTask();
		String url = task.getUrl();
		BrowserFormPage browser = new MingleBrowserFormPage(parentEditor, "Mingle", url);
		return browser;
	}

	@SuppressWarnings("restriction")
	public Image getPageImage() {
		return CommonImages.getImage(TasksUiImages.REPOSITORY_SMALL);
	}

	public String getPageText() {
		return "Mingle";
	}

}
