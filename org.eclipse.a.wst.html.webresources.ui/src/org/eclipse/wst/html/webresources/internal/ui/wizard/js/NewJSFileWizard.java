package org.eclipse.wst.html.webresources.internal.ui.wizard.js;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.webresources.internal.ui.wizard.INewFileWizard;
import org.eclipse.wst.html.webresources.internal.ui.wizard.js.org.eclipse.wst.jsdt.internal.ui.wizards.NewJSWizard;

public class NewJSFileWizard extends NewJSWizard implements INewFileWizard {

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
