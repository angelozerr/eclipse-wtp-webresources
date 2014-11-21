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
package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.core.utils.DOMHelper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class WebResourcesContext implements IWebResourcesContext {

	private final IDOMNode htmlNode;
	private final WebResourcesFinderType resourceType;
	private final boolean hasExternalCSS;
	private IFile htmlFile;

	/**
	 * Constructor for web resources context.
	 * 
	 * @param htmlNode
	 *            the HTML node which have triggers this provider.
	 * @param resourceType
	 *            the resource type (css, js, img);
	 */
	public WebResourcesContext(IDOMNode htmlNode,
			WebResourcesFinderType resourceType) {
		this(htmlNode, resourceType, false);
	}

	/**
	 * Constructor for web resources context.
	 * 
	 * @param htmlNode
	 *            the HTML node which have triggers this provider.
	 * @param resourceType
	 *            the resource type (css, js, img);
	 * @param hasExternalCSS
	 */
	public WebResourcesContext(IDOMNode htmlNode,
			WebResourcesFinderType resourceType, boolean hasExternalCSS) {
		this.htmlNode = htmlNode;
		this.htmlFile = null;
		this.resourceType = resourceType;
		this.hasExternalCSS = hasExternalCSS;
	}

	@Override
	public IDOMNode getHtmlNode() {
		return htmlNode;
	}

	@Override
	public IFile getHtmlFile() {
		if (htmlFile == null) {
			htmlFile = DOMHelper.getFile(htmlNode);
		}
		return htmlFile;
	}

	@Override
	public WebResourcesFinderType getResourceType() {
		return resourceType;
	}

	@Override
	public boolean hasExternalCSS() {
		return hasExternalCSS;
	}

}
