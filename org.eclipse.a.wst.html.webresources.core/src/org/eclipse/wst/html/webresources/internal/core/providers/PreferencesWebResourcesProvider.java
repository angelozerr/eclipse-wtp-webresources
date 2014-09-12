package org.eclipse.wst.html.webresources.internal.core.providers;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.html.webresources.core.providers.AbstractWebResourcesProvider;

public class PreferencesWebResourcesProvider extends
		AbstractWebResourcesProvider {

	@Override
	public IContainer[] getContainers(IProject project) {
		return new IContainer[] { project };
	}

}
