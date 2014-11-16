package org.eclipse.wst.html.webresources.internal.ui.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.INewWizard;

public interface INewFileWizard extends INewWizard {

	IFile getFile();
}
