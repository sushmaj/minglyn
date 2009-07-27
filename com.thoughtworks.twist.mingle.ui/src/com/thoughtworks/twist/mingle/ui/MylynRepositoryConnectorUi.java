package com.thoughtworks.twist.mingle.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;

import com.thoughtworks.twist.mingle.core.MingleConstants;

public class MylynRepositoryConnectorUi extends AbstractRepositoryConnectorUi {

	private static final String		regexp	= "(duplicate of|bug|issue|task|card)(\\s*)(#?)(\\s*)(\\d+)";
	private static final Pattern	PATTERN	= Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);

	public MylynRepositoryConnectorUi() {
	}

	public String getConnectorKind() {
		return MingleConstants.CONNECTOR_KIND;
	}

	public IWizard getNewTaskWizard(TaskRepository taskRepository, ITaskMapping selection) {
		return null;
	}

	public IWizard getQueryWizard(TaskRepository taskRepository, IRepositoryQuery queryToEdit) {
		return new MingleQueryWizard(taskRepository, queryToEdit);
	}

	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new MingleRepositorySettingsPage(taskRepository);
	}

	public boolean hasSearchPage() {
		return false;
	}

	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int lineOffset, int regionOffset) {
		ArrayList<IHyperlink> links = new ArrayList<IHyperlink>();

		Matcher m = PATTERN.matcher(text);
		while (m.find()) {
			if (lineOffset >= m.start() && lineOffset < m.end()) {
				IHyperlink link = extractHyperlink(repository, regionOffset, m);
				if (link != null) {
					links.add(link);
				}
			}
		}

		return links.toArray(new IHyperlink[] {});
	}

	private IHyperlink extractHyperlink(TaskRepository repository, int regionOffset, Matcher m) {
		int start = -1;

		if (m.group().startsWith("duplicate")) {
			start = m.start() + m.group().indexOf(m.group(5));
		} else {
			start = m.start();
		}

		int end = m.end();

		if (end == -1) {
			end = m.group().length();
		}

		try {
			String bugId = m.group(5).trim();
			start += regionOffset;
			end += regionOffset;

			IRegion sregion = new Region(start, end - start);
			return new TaskHyperlink(sregion, repository, bugId);

		} catch (NumberFormatException e) {
			return null;
		}
	}
}
