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
package org.eclipse.wst.html.webresources.internal.ui.hover;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.providers.WebResourcesCollectorAdapter;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.internal.ui.utils.HTMLWebResourcesPrinter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * {@link IWebResourcesCollector} implentation used for the hover.
 *
 */
public class WebResourcesCollectorForHover extends WebResourcesCollectorAdapter {

	private final String fileName;
	private final WebResourceType type;
	private String info;

	public WebResourcesCollectorForHover(String fileName, WebResourceType type) {
		this.fileName = fileName;
		this.type = type;
	}

	@Override
	public void add(IResource resource, IDOMNode htmlNode, IFile htmlFile,
			IURIResolver resolver) {
		IPath resourceFileLoc = resolver.resolve(resource, htmlFile);
		if (resourceFileLoc.toString().equals(fileName)) {
			info = HTMLWebResourcesPrinter.getAdditionalProposalInfo(resource,
					type);
		}
	}

	public String getInfo() {
		return info;
	}

}
