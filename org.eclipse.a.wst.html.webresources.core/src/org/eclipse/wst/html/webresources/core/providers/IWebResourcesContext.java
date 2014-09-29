package org.eclipse.wst.html.webresources.core.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public interface IWebResourcesContext {

	/**
	 * Returns the HTML node which have triggers this provider.
	 * 
	 * @return the HTML node which have triggers this provider.
	 */
	IDOMNode getHtmlNode();

	/**
	 * Returns the HTML node which have triggers this provider.
	 * 
	 * @return the HTML node which have triggers this provider.
	 */
	IFile getHtmlFile();

	/**
	 * Returns true if there is external CSS and false otherwise.
	 * 
	 * @return true if there is external CSS and false otherwise.
	 */
	boolean hasExternalCSS();

	/**
	 * Return the resource type (css, js, img).
	 * 
	 * @return the resource type (css, js, img).
	 */
	WebResourceType getResourceType();

}
