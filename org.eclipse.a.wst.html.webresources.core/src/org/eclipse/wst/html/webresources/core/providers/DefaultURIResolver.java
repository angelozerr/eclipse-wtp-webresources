/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.html.webresources.core.WebRootFolders;

public class DefaultURIResolver implements IURIResolver {

	public static IURIResolver INSTANCE = new DefaultURIResolver();

	@Override
	public IPath resolve(IResource resource, IFile root) {
		IPath location = resource.getLocation().makeRelativeTo(
				root.getParent().getLocation());
		return location;
	}

	@Override
	public boolean exists(String uri, IFile root) {
		if (uri.startsWith("http")) {
			// TODO : validate http web resources
			return true;
		} else if (uri.startsWith("/")) {
			IProject project = root.getProject();
			String[] webRootFolders = WebRootFolders.getWebRootFolders(project);
			for (String webRootFolder : webRootFolders) {
				IResource resource = project.findMember(webRootFolder);
				if (resource instanceof IContainer && ((IContainer) resource).exists(new Path(uri))) {
					return true;
				}
			}
		}
		return root.getParent().exists(new Path(uri));
	}

}
