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

/**
 * This adapter class provides default implementations for the methods described
 * by the {@link IWebResourcesCollector} interface.
 * 
 * Classes that wish to deal with event can extend this class and override only
 * the methods which they are interested in.
 * 
 */
public class WebResourcesCollectorAdapter implements IWebResourcesCollector {

	@Override
	public void startCollect(WebResourceType resourcesType) {

	}

	@Override
	public void add(Object resource, WebResourceKind resourceKind,
			IDOMNode htmlNode, IFile htmlFile, IURIResolver resolver) {

	}

	@Override
	public void endCollect(WebResourceType resourcesType) {

	}

}
