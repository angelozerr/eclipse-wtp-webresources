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
package org.eclipse.wst.html.webresources.internal.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Web Resources Messages.
 * 
 */
public final class WebResourcesUIMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIMessages"; //$NON-NLS-1$

	private static ResourceBundle fResourceBundle;

	// Hyperlink
	public static String CSSIDHyperLink_typeLabel;
	public static String CSSIDHyperLink_text;
	public static String CSSClassNameHyperLink_typeLabel;
	public static String CSSClassNameHyperLink_text;

	// below are the strings for the validation page
	public static String Validation_description;
	public static String Validation_Warning;
	public static String Validation_Error;
	public static String Validation_Ignore;
	public static String Expandable_label_files;
	public static String Expandable_label_css;
	public static String WebResourcesValidationPreferencePage_FILE_JS_UNKNOWN;
	public static String WebResourcesValidationPreferencePage_FILE_CSS_UNKNOWN;
	public static String WebResourcesValidationPreferencePage_FILE_IMG_UNKNOWN;
	public static String WebResourcesValidationPreferencePage_CSS_CLASS_UNKNOWN;
	public static String WebResourcesValidationPreferencePage_CSS_ID_UNKNOWN;

	// CSS preferences
	public static String CSSPreferencesPage_searchInAllCSSFiles_label;
	
	// Quick fix
	public static String CreateFileCompletionProposal_errorTitle;
	public static String CreateFileCompletionProposal_errorMessage;
	public static String CreateFileCompletionProposal_displayString;



	private WebResourcesUIMessages() {
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
		NLS.initializeMessages(BUNDLE_NAME, WebResourcesUIMessages.class);
	}
}
