package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

public abstract class AbstractWebResourcesProvider implements
		IWebResourcesProvider {

	/*@Override
	public void collect(IContainer container, WebResourcesType type,
			IWebResourcesCollector collector) {
		// TODO Auto-generated method stub

	}*/

	private IResourceVisitor createVisitor() {
		return new IResourceVisitor() {

			@Override
			public boolean visit(IResource resource) throws CoreException {
				switch (resource.getType()) {
				case IResource.FOLDER:
				case IResource.PROJECT:
					return true;
				case IResource.FILE:
					IFile file = (IFile) resource;
					return false;
				}
				return false;
			}
		};
	}
}
