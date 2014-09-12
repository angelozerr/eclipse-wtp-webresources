package org.eclipse.wst.html.webresources.internal.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.webresources.core.WebResourcesType;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;

public class WebResourceVisitor implements IResourceVisitor {

	private final IWebResourcesCollector collector;
	private final WebResourcesType resourcesType;

	public WebResourceVisitor(IWebResourcesCollector collector,
			WebResourcesType resourcesType) {
		this.collector = collector;
		this.resourcesType = resourcesType;
	}

	@Override
	public boolean visit(IResource resource) throws CoreException {
		switch (resource.getType()) {
		case IResource.FOLDER:
		case IResource.PROJECT:
			return true;
		case IResource.FILE:
			IFile file = (IFile) resource;
			if (file.getFileExtension().equalsIgnoreCase(resourcesType.name())) {
				collector.add(file);
			}
			return false;
		}
		return false;
	}

}
