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

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollectorProvider;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesFileSystemProvider;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesProvider;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.providers.WebResourcesProvidersManager;
import org.eclipse.wst.html.webresources.core.utils.ResourceHelper;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * 
 * Web resources provider type.
 *
 */
public class WebResourcesProviderType {

	private final IWebResourcesProvider resourcesProvider;
	private final IWebResourcesCollectorProvider collectorProvider;
	private final IWebResourcesFileSystemProvider fileSystemProvider;
	private final WebResourceType resourcesType;

	public WebResourcesProviderType(IWebResourcesProvider resourcesProvider,
			IWebResourcesFileSystemProvider fileSystemProvider,
			IWebResourcesCollectorProvider collectorProvider,
			WebResourceType resourceType) {
		this.resourcesProvider = resourcesProvider;
		this.fileSystemProvider = fileSystemProvider;
		this.collectorProvider = collectorProvider;
		this.resourcesType = resourceType;
	}

	/**
	 * Collect files in the given collector which match the web resource type.
	 * 
	 * @param collector
	 *            the collector to use to collect file which matches the given
	 *            web resource type.
	 * @param htmlNode
	 *            the DOM node which has triggered the start of this visitor.
	 * @param htmlFile
	 *            the owner HTML file of teh given DOM node.
	 * @param context
	 */
	public void collect(IWebResourcesContext context,
			IWebResourcesCollector collector) {
		IWebResourcesCollector collector2 = collectorProvider != null ? collectorProvider
				.getCollector(context) : null;
		IURIResolver resolver = WebResourcesProvidersManager.getInstance();
		if (resourcesProvider != null) {
			// get containers for the given DOM node.
			IResource[] resources = resourcesProvider.getResources(context);
			if (resources != null) {
				// Loop for each containers to visit files and collect it if it
				// matches the given web resource type.
				IResourceVisitor visitor = new WebResourcesVisitor(collector,
						collector2, context, resolver);
				// start collect
				collector.startCollect(resourcesType);
				if (collector2 != null) {
					collector2.startCollect(resourcesType);
				}
				// collect processes
				for (int i = 0; i < resources.length; i++) {
					try {
						resources[i].accept(visitor);
					} catch (CoreException e) {
						Trace.trace(Trace.SEVERE,
								"Error while collecting resource for container "
										+ resources[i].getName(), e);
					}
				}
				// end collect
				collector.endCollect(resourcesType);
				if (collector2 != null) {
					collector2.endCollect(resourcesType);
				}
			}
		}
		if (fileSystemProvider != null) {
			// get containers for the given DOM node.
			File[] files = fileSystemProvider.getResources(context);
			if (files != null) {
				collector.startCollect(resourcesType);
				if (collector2 != null) {
					collector2.startCollect(resourcesType);
				}
				// collect processes
				boolean stop = false;
				for (int i = 0; i < files.length && !stop; i++) {
					try {
						stop = processFile(files[i], collector, collector2,
								context, resolver);
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE,
								"Error while collecting file system resources for container "
										+ files[i].getName(), e);
					}
				}
				// end collect
				collector.endCollect(resourcesType);
				if (collector2 != null) {
					collector2.endCollect(resourcesType);
				}
			}

		}
	}

	private boolean processFile(File file, IWebResourcesCollector collector,
			IWebResourcesCollector collector2, IWebResourcesContext context,
			IURIResolver resolver) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (processFile(files[i], collector, collector2, context,
						resolver)) {
					return true;
				}
			}
		} else if (file.isFile()) {
			if (ResourceHelper.isMatchingWebResourceType(file, resourcesType)) {
				// current file matches the given web resource type
				// collect it.
				if (collector.add(file, WebResourceKind.FILESYSTEM, context,
						resolver)) {
					return true;
				}
				if (collector2 != null) {
					if (collector2.add(file, WebResourceKind.FILESYSTEM,
							context, resolver)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
