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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.utils.ResourceHelper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * {@link IResourceVisitor} implementation to collect {@link IFile} which match
 * a web resource type {@link WebResourceType}.
 *
 */
public class WebResourcesVisitor implements IResourceVisitor {

	private final IWebResourcesCollector collector;
	private final IWebResourcesCollector collector2;
	private final WebResourceType resourcesType;
	private final IDOMNode htmlNode;
	private final IFile htmlFile;
	private final IURIResolver resolver;

	/**
	 * Constrctor of the web resources visitor;
	 * 
	 * @param collector
	 *            the collector to use to collect file which matches the given
	 *            web resource type.
	 * @param resourceType
	 *            the web resource type (css, js, img).
	 * @param htmlNode
	 *            the DOM node which has triggered the start of this visitor.
	 * @param htmlFile
	 *            the owner HTML file of the given DOM node.
	 * @param resolver
	 *            the resolver to use to resolve fine name.
	 */
	public WebResourcesVisitor(IWebResourcesCollector collector,
			IWebResourcesCollector collector2, WebResourceType resourceType,
			IDOMNode htmlNode, IFile htmlFile, IURIResolver resolver) {
		this.collector = collector;
		this.collector2 = collector2;
		this.resourcesType = resourceType;
		this.htmlNode = htmlNode;
		this.htmlFile = htmlFile;
		this.resolver = resolver;
	}

	@Override
	public boolean visit(IResource resource) throws CoreException {
		switch (resource.getType()) {
		case IResource.FOLDER:
		case IResource.PROJECT:
			return true;
		case IResource.FILE:
			IFile file = (IFile) resource;
			if (ResourceHelper.isMatchingWebResourceType(file, resourcesType)) {
				// current file matches the given web resource type
				// collect it.
				collector.add(file, WebResourceKind.ECLIPSE_RESOURCE, htmlNode,
						htmlFile, resolver);
				if (collector2 != null) {
					collector2.add(file, WebResourceKind.ECLIPSE_RESOURCE,
							htmlNode, htmlFile, resolver);
				}
			}
			return false;
		}
		return false;
	}

}
