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
package org.eclipse.wst.html.webresources.internal.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.core.preferences.WebResourcesCorePreferenceNames;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;

/**
 * Sets default values for Web Resources Core preferences
 */
public class WebResourcesCorePreferenceInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope()
				.getNode(WebResourcesCorePlugin.getDefault().getBundle()
						.getSymbolicName());
		initializeValidationPreferences(node);
		initializeSearchPreferences(node);
	}

	/**
	 * Initializes the default validation preferences
	 * 
	 * @param node
	 *            the Eclipse preference node
	 */
	private void initializeValidationPreferences(IEclipsePreferences node) {
		node.putInt(WebResourcesCorePreferenceNames.FILE_JS_UNKNOWN,
				ValidationMessage.ERROR);
		node.putInt(WebResourcesCorePreferenceNames.FILE_CSS_UNKNOWN,
				ValidationMessage.ERROR);
		node.putInt(WebResourcesCorePreferenceNames.FILE_IMG_UNKNOWN,
				ValidationMessage.ERROR);

		node.putInt(WebResourcesCorePreferenceNames.CSS_CLASS_UNKWOWN,
				ValidationMessage.WARNING);
		node.putInt(WebResourcesCorePreferenceNames.CSS_ID_UNKWOWN,
				ValidationMessage.IGNORE);

		node.putBoolean(WebResourcesCorePreferenceNames.EXTERNAL_URL_UNKWOWN,
				WebResourcesCorePreferenceNames.EXTERNAL_URL_UNKWOWN_DEFAULT);
	}

	private void initializeSearchPreferences(IEclipsePreferences node) {
		node.putBoolean(
				WebResourcesCorePreferenceNames.SEARCH_IN_ALL_CSS_FILES_IF_NO_LINKS,
				false);
	}
}
