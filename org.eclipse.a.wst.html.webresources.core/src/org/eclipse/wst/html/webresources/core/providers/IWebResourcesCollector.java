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

import org.eclipse.wst.html.webresources.core.WebResourceType;

public interface IWebResourcesCollector {

	void startCollect(WebResourceType resourcesType);

	/**
	 * Collect the given resource and returns true if the search must be
	 * stopped.
	 * 
	 * @param resource
	 * @param resourceKind
	 * @param context
	 * @param resolver
	 * @return
	 */
	boolean add(Object resource, WebResourceKind resourceKind,
			IWebResourcesContext context, IURIResolver resolver);

	void endCollect(WebResourceType resourcesType);

}
