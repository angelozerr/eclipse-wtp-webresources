package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

public class DefaultURIResolver implements IURIResolver {

	public static IURIResolver INSTANCE = new DefaultURIResolver();

	@Override
	public IPath resolve(IResource resource, IFile root) {
		IPath location = resource.getLocation().makeRelativeTo(
				root.getParent().getLocation());
		return location;
	}

}
