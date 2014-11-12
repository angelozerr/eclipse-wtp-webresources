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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * URI resolver used to resolve web resources file.
 * 
 */
public interface IURIResolver {

	/**
	 * Resolve the given resource by using the given root.
	 * 
	 * @param resource
	 *            web resources file to resolve.
	 * @param root
	 *            the root file.
	 * @return the given resource by using the given root.
	 */
	IPath resolve(IResource resource, IFile root);

	/**
	 * Returns true of the given uri exists and false otherwise.
	 * 
	 * @param uri
	 * @param root
	 * @return
	 */
	boolean exists(String uri, IFile root);
}
