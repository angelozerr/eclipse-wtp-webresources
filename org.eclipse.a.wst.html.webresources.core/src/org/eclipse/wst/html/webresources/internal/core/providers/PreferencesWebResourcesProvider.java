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
package org.eclipse.wst.html.webresources.internal.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollectorProvider;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesProvider;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.html.webresources.internal.core.WebResourcesProjectConfiguration;

/**
 * {@link IWebResourcesProvider} implementation which uses preferences.
 *
 */
public class PreferencesWebResourcesProvider implements IWebResourcesProvider,
		IWebResourcesCollectorProvider {

	@Override
	public IResource[] getResources(IWebResourcesContext context) {
		if (context.hasExternalCSS()) {
			// the given HTML file has external CSS, don't search CSS from the
			// given project.
			// TODO : manage that with preferences.
			return null;
		}
		IFile htmlFile = context.getHtmlFile();
		IProject project = htmlFile.getProject();
		// 1) check if the project is already linked to a web resources
		// configuration.
		try {
			WebResourcesProjectConfiguration configuration = WebResourcesProjectConfiguration
					.getOrCreateConfiguration(project);
			WebResourceType resourceType = context.getResourceType();
			IResource[] resources = configuration.getResources(resourceType);
			if (resources != null) {
				return resources;
			}
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE,
					"Error while getting web resources configuration for the project "
							+ project.getName(), e);
		}

		// 2) no web resources configuration, create it.
		IProject[] referencedProjects = null;
		try {
			referencedProjects = project.getReferencedProjects();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		IProject[] containers = new IProject[1 + (referencedProjects != null ? referencedProjects.length
				: 0)];
		containers[0] = project;
		for (int i = 0; i < referencedProjects.length; i++) {
			containers[i + 1] = referencedProjects[i];
		}
		return containers;
	}

	@Override
	public IWebResourcesCollector getCollector(IWebResourcesContext context) {
		IFile htmlFile = context.getHtmlFile();
		IProject project = htmlFile.getProject();
		try {
			WebResourcesProjectConfiguration configuration = WebResourcesProjectConfiguration
					.getOrCreateConfiguration(project);
			IResource[] resources = configuration.getResources(context
					.getResourceType());
			if (resources == null) {
				return configuration;
			}
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE,
					"Error while getting web resources configuration for the project "
							+ project.getName(), e);
		}
		return null;
	}
}
