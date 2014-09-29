package org.eclipse.wst.html.webresources.core.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.providers.WebResourcesCollectorAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class WebResourcesCollectorForValidation extends
		WebResourcesCollectorAdapter {

	private final String fileName;
	private int nbFiles;

	public WebResourcesCollectorForValidation(String fileName) {
		this.fileName = fileName;
		this.nbFiles = 0;
	}

	@Override
	public void add(Object resource, WebResourceKind resourceKind,
			IDOMNode htmlNode, IFile htmlFile, IURIResolver resolver) {
		if (resourceKind == WebResourceKind.ECLIPSE_RESOURCE) {
			IResource r = (IResource) resource;
			IPath resourceFileLoc = resolver.resolve(r, htmlFile);
			if (resourceFileLoc.toString().equals(fileName)) {
				nbFiles++;
			}
		}
	}

	public int getNbFiles() {
		return nbFiles;
	}

}
