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
package org.eclipse.wst.htmlcss.internal.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Web Resources Messages.
 * 
 */
public final class WebResourcesMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.wst.htmlcss.internal.ui.WebResourcesMessages"; //$NON-NLS-1$

	private static ResourceBundle fResourceBundle;

	// Hyperlink
	public static String CSSIDHyperLink_typeLabel;
	public static String CSSIDHyperLink_text;
	public static String CSSClassNameHyperLink_typeLabel;
	public static String CSSClassNameHyperLink_text;

	private WebResourcesMessages() {
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
		NLS.initializeMessages(BUNDLE_NAME, WebResourcesMessages.class);
	}
}
