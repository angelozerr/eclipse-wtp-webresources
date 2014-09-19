package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public abstract class AbstractWebResourcesProvider implements
		IWebResourcesProvider {

	@Override
	public IWebResourcesCollector getCollector(IDOMNode htmlNode,
			IFile htmlFile, WebResourceType resourceType) {
		return null;
	}
}
