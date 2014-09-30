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
package org.eclipse.wst.html.webresources.internal.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.utils.ResourceHelper;

/**
 * Manager used to observe, the creation and the delete of web resources files
 * to update {@link WebResourcesProjectConfiguration}.
 *
 */
public class WebResourcesFileManager implements IResourceChangeListener,
		IResourceDeltaVisitor {

	private final static WebResourcesFileManager INSTANCE = new WebResourcesFileManager();

	public static WebResourcesFileManager getInstance() {
		return INSTANCE;
	}

	private WebResourcesFileManager() {

	}

	public void initialize() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE);
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				delta.accept(this);
			}
		} catch (Throwable e) {
			Trace.trace(Trace.SEVERE, "", e);
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (resource == null) {
			return false;
		}
		switch (resource.getType()) {
		case IResource.ROOT:
			return true;
		case IResource.FOLDER:
			updateResource(resource, delta);
			return true;
		case IResource.PROJECT:
			if (resource instanceof IProject) {
				IProject project = (IProject) resource;
				if (!(project.isAccessible())) {
					return false;
				}
				return true;
			}
		case IResource.FILE:
			updateResource(resource, delta);
			return true;
		}
		return false;
	}

	private void updateResource(IResource resource, IResourceDelta delta) {
		WebResourceType[] resourceTypes = WebResourceType.values();
		WebResourceType resourceType = null;
		for (int i = 0; i < resourceTypes.length; i++) {
			resourceType = resourceTypes[i];
			if (ResourceHelper
					.isMatchingWebResourceType(resource, resourceType)) {
				IProject project = resource.getProject();
				try {
					WebResourcesProjectConfiguration configuration = WebResourcesProjectConfiguration
							.getConfiguration(project);
					if (configuration != null) {
						switch (delta.getKind()) {
						case IResourceDelta.ADDED:
							configuration
									.addWebResource(resource, resourceType);
							break;
						case IResourceDelta.REMOVED:
							configuration.removeWebResource(resource,
									resourceType);
							break;
						}
					}
				} catch (CoreException e) {
					Trace.trace(Trace.SEVERE,
							"Error while getting web resources configuration for the project "
									+ project.getName(), e);
				}
				break;
			}
		}
	}
}
