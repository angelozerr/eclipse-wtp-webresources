package org.eclipse.wst.html.webresources.internal.core.providers;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.html.webresources.core.providers.AbstractWebResourcesProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class PreferencesWebResourcesProvider extends
		AbstractWebResourcesProvider {

	@Override
	public IContainer[] getContainers(IDOMNode htmlNode, IFile htmlFile) {
		IProject project = htmlFile.getProject();
		return new IContainer[] { project };
	}
}
