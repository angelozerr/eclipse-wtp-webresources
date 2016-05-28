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
package org.eclipse.wst.html.webresources.internal.ui.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.core.preferences.WebResourcesCorePreferenceNames;
import org.eclipse.wst.html.webresources.internal.ui.Trace;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIMessages;
import org.osgi.service.prefs.BackingStoreException;

/**
 * CSS web resources preferences pages.
 *
 */
public class CSSWebResourcesPropertyPage extends PropertyPreferencePage {

	public static final String PROPERTY_PAGE_ID = "org.eclipse.wst.html.webresources.ui.properties.CSSWebResourcesPropertyPage";
	public static final String PREFERENCE_PAGE_ID = "org.eclipse.wst.html.webresources.ui.preferences.CSSWebResourcesPropertyPage";

	private final IPreferencesService fPreferencesService;

	private Button searchInAllCSSFilesCheckbox;

	public CSSWebResourcesPropertyPage() {
		fPreferencesService = Platform.getPreferencesService();
	}

	@Override
	protected Control createCommonContents(Composite parent) {
		final Composite page = new Composite(parent, SWT.NULL);
		page.setLayout(new GridLayout());

		IScopeContext[] preferenceScopes = createPreferenceScopes();
		searchInAllCSSFilesCheckbox = createCheckbox(
				page,
				WebResourcesCorePreferenceNames.SEARCH_IN_ALL_CSS_FILES_IF_NO_LINKS,
				preferenceScopes,
				WebResourcesUIMessages.CSSPreferencesPage_searchInAllCSSFiles_label);
		return page;
	}

	private Button createCheckbox(Composite parent, String preferenceName,
			IScopeContext[] preferenceScopes, String label) {

		Button checkbox = new Button(parent, SWT.CHECK);
		checkbox.setText(label); //$NON-NLS-1$
		checkbox.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING,
				GridData.VERTICAL_ALIGN_END, false, false, 1, 1));
		updateCheckbox(checkbox, preferenceName, preferenceScopes);
		return checkbox;
	}

	private void updateCheckbox(Button checkbox, String preferenceName,
			IScopeContext[] preferenceScopes) {
		boolean checked = fPreferencesService.getBoolean(
				getPreferenceNodeQualifier(), preferenceName, false,
				preferenceScopes);
		checkbox.setSelection(checked);
	}

	private void updateCheckbox(Button checkbox, String preferenceName,
			IEclipsePreferences defaultPreferences) {
		boolean checked = defaultPreferences.getBoolean(preferenceName, false);
		checkbox.setSelection(checked);
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		IEclipsePreferences defaultPreferences = createPreferenceScopes()[1]
				.getNode(getPreferenceNodeQualifier());
		updateCheckbox(
				searchInAllCSSFilesCheckbox,
				WebResourcesCorePreferenceNames.SEARCH_IN_ALL_CSS_FILES_IF_NO_LINKS,
				defaultPreferences);
	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		IScopeContext[] contexts = createPreferenceScopes();
		// remove project-specific information if it's not enabled
		boolean remove = getProject() != null && !isElementSettingsEnabled();
		updateContexts(
				searchInAllCSSFilesCheckbox,
				WebResourcesCorePreferenceNames.SEARCH_IN_ALL_CSS_FILES_IF_NO_LINKS,
				contexts, remove);
		flushContexts(contexts);
		return ok;
	}

	private void updateContexts(Button checkbox, String preferenceName,
			IScopeContext[] contexts, boolean remove) {
		if (remove) {
			contexts[0].getNode(getPreferenceNodeQualifier()).remove(
					preferenceName);

		} else {
			contexts[0].getNode(getPreferenceNodeQualifier()).putBoolean(
					preferenceName, checkbox.getSelection());
		}
	}

	@Override
	protected String getPreferenceNodeQualifier() {
		return WebResourcesCorePlugin.getDefault().getBundle()
				.getSymbolicName();
	}

	@Override
	protected String getPreferencePageID() {
		return PREFERENCE_PAGE_ID;
	}

	@Override
	protected String getProjectSettingsKey() {
		return WebResourcesCorePreferenceNames.CSS_USE_PROJECT_SETTINGS;
	}

	@Override
	protected String getPropertyPageID() {
		return PROPERTY_PAGE_ID;
	}

	@Override
	public void init(IWorkbench workbencsh) {

	}

	protected void flushContexts(IScopeContext[] contexts) {
		for (int i = 0; i < contexts.length; i++) {
			try {
				contexts[i].getNode(getPreferenceNodeQualifier()).flush();
			} catch (BackingStoreException e) {
				Trace.trace(
						Trace.WARNING,
						"problem saving preference settings to scope " + contexts[i].getName(), e); //$NON-NLS-1$
			}
		}
	}
}
