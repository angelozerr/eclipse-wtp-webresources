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
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.utils.ResourceHelper;

/**
 * {@link IResourceProxyVisitor} implementation to collect {@link IFile} which
 * match a web resource type {@link WebResourceType}.
 *
 */
public class WebResourcesProxyVisitor implements IResourceProxyVisitor {

	private final IWebResourcesCollector collector;
	private final IWebResourcesContext context;
	private final IURIResolver resolver;
	private boolean stop;
	private IResource current;

	/**
	 * Constrctor of the web resources visitor;
	 * 
	 * @param collector
	 *            the collector to use to collect file which matches the given
	 *            web resource type.
	 * @param context
	 *            the web resources context (html file, html node, web resource
	 *            type...)
	 * @param resolver
	 *            the resolver to use to resolve fine name.
	 */
	public WebResourcesProxyVisitor(IWebResourcesCollector collector,
			IWebResourcesContext context, IURIResolver resolver) {
		this.collector = collector;
		this.context = context;
		this.resolver = resolver;
		this.stop = false;
	}

	@Override
	public boolean visit(IResourceProxy proxy) throws CoreException {
		int type = proxy.getType();
		switch (type) {
		case IResource.FOLDER:
		case IResource.PROJECT:
			IResource resource = proxy.requestResource();
			return resource.equals(current);
		case IResource.FILE:
			if (ResourceHelper.isMatchingWebResourceType(proxy.getName(),
					context.getResourceType().getType())) {
				// current file matches the given web resource type
				// collect it.
				IResource file = proxy.requestResource();
				if (collector.add(file, WebResourceKind.ECLIPSE_RESOURCE,
						context, resolver)) {
					stop = true;
				}
			}
		default:
			break;
		}
		return !stop;
	}

	public void setCurrent(IResource current) {
		this.current = current;
	}

}
