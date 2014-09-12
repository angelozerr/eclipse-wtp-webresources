package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.webresources.core.DOMHelper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public abstract class AbstractWebResourcesProvider implements
		IWebResourcesProvider {

	@Override
	public IURIResolver getResolver(IDOMNode htmlNode, IFile htmlFile) {
		return DefaultURIResolver.INSTANCE;
	}
}
