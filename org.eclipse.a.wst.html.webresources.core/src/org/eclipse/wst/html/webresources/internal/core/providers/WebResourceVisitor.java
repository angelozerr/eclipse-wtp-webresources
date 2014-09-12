package org.eclipse.wst.html.webresources.internal.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.webresources.core.WebResourcesType;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class WebResourceVisitor implements IResourceVisitor {

	private final IWebResourcesCollector collector;
	private final WebResourcesType resourcesType;
	private final IDOMNode htmlNode;
	private final IFile htmlFile;
	private final IWebResourcesProvider provider;

	public WebResourceVisitor(IWebResourcesCollector collector,
			WebResourcesType resourcesType, IDOMNode htmlNode, IFile htmlFile,
			IWebResourcesProvider provider) {
		this.collector = collector;
		this.resourcesType = resourcesType;
		this.htmlNode = htmlNode;
		this.htmlFile = htmlFile;
		this.provider = provider;
	}

	@Override
	public boolean visit(IResource resource) throws CoreException {
		switch (resource.getType()) {
		case IResource.FOLDER:
		case IResource.PROJECT:
			return true;
		case IResource.FILE:
			IFile file = (IFile) resource;
			if (resourcesType.name().equalsIgnoreCase(file.getFileExtension())) {
				collector.add(file, htmlNode, htmlFile, provider);
			}
			return false;
		}
		return false;
	}

}
