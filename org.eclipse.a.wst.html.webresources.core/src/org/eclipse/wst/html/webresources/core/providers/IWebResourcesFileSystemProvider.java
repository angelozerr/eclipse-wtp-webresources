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

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * API for web resources provider which works with file system resource
 * {@link File}.
 *
 */
public interface IWebResourcesFileSystemProvider {

	/**
	 * Returns the list of web resources (css, js, img files, or folders)
	 * available for the given DOM node and which matches the given resource
	 * type.
	 * 
	 * @param htmlNode
	 *            the HTML node which have triggers this provider.
	 * @param htmlFile
	 *            the HTML file of the HTML node.
	 * @param resourceType
	 *            the resource type (css, js, img);
	 * @param context
	 * @return
	 */
	File[] getResources(IDOMNode htmlNode, IFile htmlFile,
			WebResourceType resourceType, WebResourcesProviderContext context);

}
