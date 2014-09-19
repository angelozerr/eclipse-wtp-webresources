package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public interface IWebResourcesProvider {

	IResource[] getResources(IDOMNode htmlNode, IFile htmlFile,
			WebResourceType resourceType);

	IWebResourcesCollector getCollector(IDOMNode htmlNode, IFile htmlFile,
			WebResourceType resourceType);
}
