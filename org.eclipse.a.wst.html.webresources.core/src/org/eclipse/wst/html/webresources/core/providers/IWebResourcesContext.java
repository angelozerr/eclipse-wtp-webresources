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
