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
package org.eclipse.wst.html.webresources.internal.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Angular Core Messages.
 * 
 */
public final class WebResourcesCoreMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.wst.html.webresources.internal.core.WebResourcesCoreMessages"; //$NON-NLS-1$

	private static ResourceBundle fResourceBundle;

	// Validation
	public static String Validation_CSS_CLASS_UNDEFINED;
	public static String Validation_CSS_ID_UNDEFINED;
	public static String Validation_FILE_JS_UNDEFINED;
	public static String Validation_FILE_CSS_UNDEFINED;
	public static String Validation_FILE_IMG_UNDEFINED;

	public static String WebResourcesIndexManager;
	public static String WebResourcesCorePlugin_Initializing_WebResources_Tools;

	private WebResourcesCoreMessages() {
	}

	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null)
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		} catch (MissingResourceException x) {
			fResourceBundle = null;
		}
		return fResourceBundle;
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, WebResourcesCoreMessages.class);
	}
}
