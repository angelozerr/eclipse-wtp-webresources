/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.webresources.internal.ui.wizard.css.org.eclipse.wst.css.ui.internal.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.css.ui.internal.Logger;

/**
 * Wrapper class for all Facet-related calls. If the Facet or ModuleCore
 * bundles are not available, this class will not load, or if it does, its
 * methods will cause NoClassDefFoundErrors. This allows us to
 * compartmentalize the dependencies.
 * 
 */
final class FacetModuleCoreSupportDelegate {
	/**
	 * Copied to avoid unneeded extra dependency (plus it's unclear why the
	 * value is in that plug-in).
	 * 
	 * @see org.eclipse.wst.common.componentcore.internal.util.IModuleConstants.JST_WEB_MODULE
	 */
	private final static String JST_WEB_MODULE = "jst.web"; //$NON-NLS-1$

	private final static String WST_WEB_MODULE = "wst.web"; //$NON-NLS-1$
	private final static String JST_WEBFRAGMENT_MODULE = "jst.webfragment"; //$NON-NLS-1$

	/**
	 * @param project
	 * @return the IPath to the "root" of the web contents
	 */
	static IPath getWebContentRootPath(IProject project) {
		if (project == null)
			return null;

		if (!ModuleCoreNature.isFlexibleProject(project))
			return null;

		IPath path = null;
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null && component.exists() && component.getRootFolder() != null) {
			path = component.getRootFolder().getWorkspaceRelativePath();
			if (component.getRootFolder().getFolder(FacetModuleCoreSupport.META_INF_RESOURCES_PATH).getUnderlyingFolder().isAccessible()) {
				path = path.append(FacetModuleCoreSupport.META_INF_RESOURCES_PATH);
			}
		}
		return path;
	}

	/**
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	static boolean isWebProject(IProject project) {
		boolean is = false;
		try {
			IFacetedProject faceted = ProjectFacetsManager.create(project);
			if (ProjectFacetsManager.isProjectFacetDefined(JST_WEB_MODULE)) {
				IProjectFacet facet = ProjectFacetsManager.getProjectFacet(JST_WEB_MODULE);
				is = is || (faceted != null && faceted.hasProjectFacet(facet));
			}
			if (!is && ProjectFacetsManager.isProjectFacetDefined(WST_WEB_MODULE)) {
				IProjectFacet facet = ProjectFacetsManager.getProjectFacet(WST_WEB_MODULE);
				is = is || (faceted != null && faceted.hasProjectFacet(facet));
			}
			if (!is && ProjectFacetsManager.isProjectFacetDefined(JST_WEBFRAGMENT_MODULE)) {
				IProjectFacet facet = ProjectFacetsManager.getProjectFacet(JST_WEBFRAGMENT_MODULE);
				is = is || (faceted != null && faceted.hasProjectFacet(facet));
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		return is;
	}

	static IPath[] getAcceptableRootPaths(IProject project) {
		if (!ModuleCoreNature.isFlexibleProject(project)) {
			return new IPath[]{project.getFullPath()};
		}

		List paths = new ArrayList();
		IVirtualFolder componentFolder = ComponentCore.createFolder(project, Path.ROOT);
		if (componentFolder != null && componentFolder.exists()) {
			IContainer[] workspaceFolders = componentFolder.getUnderlyingFolders();
			for (int i = 0; i < workspaceFolders.length; i++) {
				if (workspaceFolders[i].getFolder(FacetModuleCoreSupport.META_INF_RESOURCES_PATH).isAccessible())
					paths.add(workspaceFolders[i].getFullPath().append(FacetModuleCoreSupport.META_INF_RESOURCES_PATH));
				else
					paths.add(workspaceFolders[i].getFullPath());
			}
			
			IVirtualReference[] references = ComponentCore.createComponent(project).getReferences();
			if (references != null) {
				for (int i = 0; i < references.length; i++) {
					IVirtualComponent referencedComponent = references[i].getReferencedComponent();
					if (referencedComponent == null)
						continue;
					IVirtualComponent component = referencedComponent.getComponent();
					if (component == null)
						continue;
					IVirtualFolder rootFolder = component.getRootFolder();
					if (rootFolder == null)
						continue;
					IPath referencedPathRoot = rootFolder.getWorkspaceRelativePath();
					/* http://bugs.eclipse.org/410161 */
					if (referencedPathRoot != null) {
						/*
						 * See Servlet 3.0, section 4.6 ; this is the only
						 * referenced module/component type we support
						 */
						IPath resources = referencedPathRoot.append(FacetModuleCoreSupport.META_INF_RESOURCES);
						if (resources != null && component.getProject().findMember(resources.removeFirstSegments(1)) != null) {
							paths.add(resources);
						}
					}
				}
			}

		}
		else {
			paths.add(new IPath[]{project.getFullPath()});
		}
		return (IPath[]) paths.toArray(new IPath[paths.size()]);
	}

	static IPath getDefaultRoot(IProject project) {
		if (ModuleCoreNature.isFlexibleProject(project)) {
			IVirtualFolder componentFolder = ComponentCore.createFolder(project, Path.ROOT);
			if (componentFolder != null && componentFolder.exists()) {
				return componentFolder.getWorkspaceRelativePath();
			}
		}
		return null;
	}

	static IPath getRootContainerForPath(IProject project, IPath path) {
		if (ModuleCoreNature.isFlexibleProject(project)) {
			IVirtualFolder componentFolder = ComponentCore.createFolder(project, Path.ROOT);
			if (componentFolder != null && componentFolder.exists()) {
				IContainer[] workspaceFolders = componentFolder.getUnderlyingFolders();
				for (int i = 0; i < workspaceFolders.length; i++) {
					if (workspaceFolders[i].getFullPath().isPrefixOf(path)) {
						return workspaceFolders[i].getFullPath();
					}
				}
			}
		}
		return null;
	}
}
