package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

public interface IWebResourcesProvider {

	IContainer[] getContainers(IProject project);

	//void collect(IContainer container, WebResourcesType type,
	//		IWebResourcesCollector collector);

	// boolean accept(IResource resource);

}
