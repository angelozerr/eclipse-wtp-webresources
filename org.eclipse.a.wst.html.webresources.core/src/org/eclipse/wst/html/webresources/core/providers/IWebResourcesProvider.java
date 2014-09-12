package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public interface IWebResourcesProvider {

	IContainer[] getContainers(IDOMNode htmlNode, IFile htmlFile);

	IURIResolver getResolver(IDOMNode htmlNode, IFile htmlFile);

}
