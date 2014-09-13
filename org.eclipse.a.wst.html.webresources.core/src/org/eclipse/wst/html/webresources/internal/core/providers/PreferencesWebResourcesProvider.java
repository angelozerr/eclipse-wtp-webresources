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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.html.webresources.core.providers.AbstractWebResourcesProvider;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * {@link IWebResourcesProvider} implementation which uses preferences.
 *
 */
public class PreferencesWebResourcesProvider extends
		AbstractWebResourcesProvider {

	@Override
	public IContainer[] getContainers(IDOMNode htmlNode, IFile htmlFile) {
		// TODO : use some rpeferences. Today project is the container used to
		// search files.
		IProject project = htmlFile.getProject();
		return new IContainer[] { project };
	}
}
