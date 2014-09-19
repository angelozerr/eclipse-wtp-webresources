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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.providers.DefaultURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesProvider;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * 
 * Web resources provider type.
 *
 */
public class WebResourcesProviderType implements IURIResolver {

	private final IWebResourcesProvider provider;
	private final WebResourceType resourcesType;

	public WebResourcesProviderType(IWebResourcesProvider provider,
			WebResourceType resourceType) {
		this.provider = provider;
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
	 */
	public void collect(IDOMNode htmlNode, IFile htmlFile,
			IWebResourcesCollector collector) {
		// get containers for the given DOM node.
		IResource[] resources = provider.getResources(htmlNode, htmlFile,
				resourcesType);
		if (resources != null) {
			// Loop for each containers to visit files and collect it if it
			// matches the given web resource type.
			IWebResourcesCollector collector2 = provider.getCollector(htmlNode,
					htmlFile, resourcesType);
			IResourceVisitor visitor = new WebResourcesVisitor(collector,
					collector2, resourcesType, htmlNode, htmlFile, this);
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

	@Override
	public IPath resolve(IResource resource, IFile root) {
		// TODO : manage resolver with extension point
		return DefaultURIResolver.INSTANCE.resolve(resource, root);
	}

}
