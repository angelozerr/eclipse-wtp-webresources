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
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.core.preferences.WebResourcesCorePreferenceNames;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesProvider;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.html.webresources.internal.core.WebResourcesProjectConfiguration;

/**
 * {@link IWebResourcesProvider} implementation which uses preferences.
 *
 */
public class PreferencesWebResourcesProvider implements IWebResourcesProvider {

	private IPreferencesService fPreferenceService = null;

	@Override
	public IResource[] getResources(IWebResourcesContext context,
			IProgressMonitor monitor) {
		if (context.hasExternalCSS()) {
			// the given HTML file has external CSS, don't search CSS from the
			// given project.
			// TODO : manage that with preferences.
			return null;
		}
		IFile htmlFile = context.getHtmlFile();
		IProject project = htmlFile.getProject();

		if ((context.getResourceType().equals(
				WebResourcesFinderType.CSS_CLASS_NAME) || context
				.getResourceType().equals(WebResourcesFinderType.CSS_ID))
				&& !isSearchInAllCSSFiles(project)) {
			// the HTML file doesn't contains link elements (external
			// stylesheet) and preferences "search in all CSS files" is setted
			// to false
			// don't search CSS files inside the project
			// see
			// https://github.com/angelozerr/eclipse-wtp-webresources/issues/20
			// to avoid Freeze UI (when there are a lot of CSS files).
			return null;
		}
		// 1) check if the project is already linked to a web resources
		// configuration.
		try {
			WebResourcesProjectConfiguration configuration = WebResourcesProjectConfiguration
					.getOrCreateConfiguration(project);
			WebResourceType resourceType = context.getResourceType().getType();
			IResource[] resources = configuration.getResources(resourceType,
					monitor);
			if (resources != null) {
				return resources;
			}
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE,
					"Error while getting web resources configuration for the project "
							+ project.getName(), e);
		}
		return null;
	}

	private boolean isSearchInAllCSSFiles(IProject project) {
		if (fPreferenceService == null) {
			fPreferenceService = Platform.getPreferencesService();
		}

		IScopeContext[] fLookupOrder;
		ProjectScope projectScope = new ProjectScope(project);
		if (projectScope
				.getNode(getQualifier())
				.getBoolean(
						WebResourcesCorePreferenceNames.CSS_USE_PROJECT_SETTINGS,
						false)) {
			fLookupOrder = new IScopeContext[] { projectScope,
					new InstanceScope(), new DefaultScope() };
		} else {
			fLookupOrder = new IScopeContext[] { new InstanceScope(),
					new DefaultScope() };
		}
		return fPreferenceService
				.getBoolean(
						getQualifier(),
						WebResourcesCorePreferenceNames.SEARCH_IN_ALL_CSS_FILES_IF_NO_LINKS,
						false, fLookupOrder);
	}

	private String getQualifier() {
		return WebResourcesCorePlugin.getDefault().getBundle()
				.getSymbolicName();
	}
}
