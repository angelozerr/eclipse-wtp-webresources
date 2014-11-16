package org.eclipse.wst.html.webresources.internal.ui.wizard.css;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.webresources.internal.ui.wizard.INewFileWizard;
import org.eclipse.wst.html.webresources.internal.ui.wizard.css.org.eclipse.wst.css.ui.internal.wizard.NewCSSWizard;

public class NewCSSFileWizard extends NewCSSWizard implements INewFileWizard {

	private IFile file;

	@Override
	protected void openEditor(IFile file) {
		this.file = file;
	}

	@Override
	public IFile getFile() {
		return file;
	}
}
