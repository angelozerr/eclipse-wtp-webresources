package org.eclipse.wst.html.webresources.internal.core.search;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.core.utils.ResourceHelper;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.html.webresources.internal.core.WebResourcesCoreMessages;
import org.eclipse.wst.html.webresources.internal.core.WebResourcesProjectConfiguration;
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
import org.eclipse.wst.sse.core.indexing.AbstractIndexManager;

/**
 * Index manger used to update the web resources file hosted by a project.
 *
 */
public class WebResourcesIndexManager extends AbstractIndexManager {

	private static final String BIN_FOLDER = "bin";

	private static final WebResourcesIndexManager INSTANCE = new WebResourcesIndexManager();

	public static WebResourcesIndexManager getDefault() {
		return INSTANCE;
	}

	private IPath fWorkingLocation;

	protected WebResourcesIndexManager() {
		super(WebResourcesCoreMessages.WebResourcesIndexManager);
	}

	@Override
	protected boolean isResourceToIndex(int type, IPath path) {
		String name = path.lastSegment();
		switch (type) {
		case IResource.ROOT:
		case IResource.PROJECT:
			return true;
		case IResource.FOLDER:
			return !name.equals(BIN_FOLDER) && !name.startsWith(".");
		case IResource.FILE:
			return ResourceHelper.isWebResource(name);
		}
		return false;
	}

	@Override
	protected void performAction(byte source, byte action, IResource resource,
			IPath movePath) {
		WebResourceType resourceType = ResourceHelper
				.getWebResourceType(resource);
		if (resourceType != null) {
			IProject project = resource.getProject();
			if (!project.isAccessible()) {
				return;
			}
			// it's a web resources file (css, img or js).
			try {
				IContainer container = resource.getParent();
				WebResourcesProjectConfiguration configuration = WebResourcesProjectConfiguration
						.getOrCreateConfiguration(project);
				switch (action) {
				case AbstractIndexManager.ACTION_ADD:
					// add (css, img or js) file
					// System.err.println(resource.getLocation());
					configuration.addWebResource(container, resourceType);
					break;
				case AbstractIndexManager.ACTION_REMOVE:
					// remove (css, img or js) file
					configuration.removeWebResource(container, resourceType);
					break;
				}
			} catch (CoreException e) {
				Trace.trace(
						Trace.SEVERE,
						"Error while updating web resources project configuration",
						e);
			}
		}
	}

	@Override
	protected IPath getWorkingLocation() {
		if (this.fWorkingLocation == null) {
			// create path to working area
			IPath workingLocation = WebResourcesCorePlugin.getDefault()
					.getStateLocation().append("WebResourcesIndexManager"); //$NON-NLS-1$

			// ensure that it exists on disk
			File folder = new File(workingLocation.toOSString());
			if (!folder.isDirectory()) {
				try {
					folder.mkdir();
				} catch (SecurityException e) {
					Trace.trace(
							Trace.SEVERE,
							this.getName()
									+ ": Error while creating state location: " + folder + //$NON-NLS-1$
									" This renders the index manager irrevocably broken for this workspace session", //$NON-NLS-1$
							e);
				}
			}

			this.fWorkingLocation = workingLocation;
		}

		return this.fWorkingLocation;
	}

}
