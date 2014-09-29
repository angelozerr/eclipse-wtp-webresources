package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.utils.DOMHelper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class WebResourcesContext implements IWebResourcesContext {

	private final IDOMNode htmlNode;
	private final WebResourceType resourceType;
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
	public WebResourcesContext(IDOMNode htmlNode, WebResourceType resourceType) {
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
	public WebResourcesContext(IDOMNode htmlNode, WebResourceType resourceType,
			boolean hasExternalCSS) {
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
	public WebResourceType getResourceType() {
		return resourceType;
	}

	@Override
	public boolean hasExternalCSS() {
		return hasExternalCSS;
	}

}
