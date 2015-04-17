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
package org.eclipse.wst.html.webresources.core;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

/**
 * Helper class to store web root folders configuration in IProject.
 */
public class WebRootFolders {

	public static final QualifiedName PROPERTY_KEY = new QualifiedName(
			WebResourcesCorePlugin.PLUGIN_ID, WebRootFolders.class.getName());

	public static String[] getWebRootFolders(IProject project) {
		String value = null;

		try {
			value = project.getPersistentProperty(PROPERTY_KEY);
		} catch (CoreException e) {
			WebResourcesCorePlugin.log(e);
		}
		
		if (value == null) {
			return new String[0];
		}
		
		value = value.substring(1, value.length() - 1);
		
		if (value.isEmpty()) {
			return new String[0];
		}

		return value.split(",( )*");
	}

	public static void setWebRootFolders(IProject project, String[] roots) {
		String value = Arrays.toString(roots);
		
		try {
			project.setPersistentProperty(PROPERTY_KEY, value);
		} catch (CoreException e) {
			WebResourcesCorePlugin.log(e);
		}
	}

}
