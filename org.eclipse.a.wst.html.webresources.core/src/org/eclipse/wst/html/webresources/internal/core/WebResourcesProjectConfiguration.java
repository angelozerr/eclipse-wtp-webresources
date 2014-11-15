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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.internal.core.search.WebResourcesIndexManager;

/**
 * Web Resources configuration for a project.
 *
 */
public class WebResourcesProjectConfiguration {

	private static final QualifiedName CONFIGURATION = new QualifiedName(
			WebResourcesCorePlugin.PLUGIN_ID + ".sessionprops",
			"WebResourcesConfiguration");

	private static final IResource[] EMPTY_RESOURCE = new IResource[0];

	private static final IProgressMonitor NULL_PROGRESS_MONITOR = new NullProgressMonitor();

	private final Map<WebResourceType, List<IResource>> resourcesMap;

	private WebResourcesProjectConfiguration(IProject project)
			throws CoreException {
		project.setSessionProperty(CONFIGURATION, this);
		this.resourcesMap = new HashMap<WebResourceType, List<IResource>>();
	}

	public static WebResourcesProjectConfiguration getConfiguration(
			IProject project) throws CoreException {
		return (WebResourcesProjectConfiguration) project
				.getSessionProperty(CONFIGURATION);
	}

	public static WebResourcesProjectConfiguration getOrCreateConfiguration(
			IProject project) throws CoreException {
		WebResourcesProjectConfiguration configuration = getConfiguration(project);
		if (configuration == null) {
			configuration = createConfiguration(project);
		}
		return configuration;
	}

	private static synchronized WebResourcesProjectConfiguration createConfiguration(
			IProject project) throws CoreException {
		WebResourcesProjectConfiguration configuration = getConfiguration(project);
		if (configuration != null) {
			return configuration;
		}
		return new WebResourcesProjectConfiguration(project);
	}

	public IResource[] getResources(WebResourceType resourceType,
			IProgressMonitor monitor) {
		// wait for the index
		WebResourcesIndexManager.getDefault().waitForConsistent(
				monitor != null ? monitor : NULL_PROGRESS_MONITOR);
		List<IResource> resources = resourcesMap.get(resourceType);
		if (resources == null) {
			return null;
		}
		return resources.toArray(EMPTY_RESOURCE);
	}

	public void addWebResource(IResource resource, WebResourceType resourceType) {
		synchronized (resourcesMap) {
			List<IResource> resources = resourcesMap.get(resourceType);
			if (resources == null) {
				resources = new ArrayList<IResource>();
				resourcesMap.put(resourceType, resources);
			}
			if (!resources.contains(resource)) {
				resources.add(resource);
			}
		}
	}

	public void removeWebResource(IResource resource,
			WebResourceType resourceType) {
		synchronized (resourcesMap) {
			List<IResource> resources = resourcesMap.get(resourceType);
			if (resources != null) {
				resources.remove(resource);
			}
		}
	}
}
