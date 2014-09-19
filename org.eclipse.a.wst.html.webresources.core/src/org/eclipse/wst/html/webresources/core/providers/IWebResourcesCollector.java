package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public interface IWebResourcesCollector {

	void startCollect(WebResourceType resourcesType);

	void add(IResource resource, IDOMNode htmlNode, IFile htmlFile,
			IURIResolver resolver);

	void endCollect(WebResourceType resourcesType);

}
