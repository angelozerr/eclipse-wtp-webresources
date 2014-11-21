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

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.html.ui.internal.Logger;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.core.preferences.WebResourcesCorePreferenceNames;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIMessages;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIPlugin;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractValidationSettingsPage;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Web Resources Validation preferences page.
 *
 */
public class WebResourcesValidationPreferencePage extends
		AbstractValidationSettingsPage {

	public static final String PROPERTY_PAGE_ID = "org.eclipse.wst.html.webresources.ui.propertyPage.project.validation";

	public static final String PREFERENCE_PAGE_ID = "org.eclipse.wst.html.webresources.ui.preferences.validation";

	private static final int[] SEVERITIES = { ValidationMessage.ERROR,
			ValidationMessage.WARNING, ValidationMessage.IGNORE };

	private static final String SETTINGS_SECTION_NAME = "WebResourcesValidationSeverities";//$NON-NLS-1$

	private class BooleanData {
		private String fKey;
		private boolean fValue;
		boolean originalValue = false;

		public BooleanData(String key) {
			fKey = key;
		}

		public String getKey() {
			return fKey;
		}

		/**
		 * Sets enablement for the attribute names ignorance
		 * 
		 * @param severity
		 *            the severity level
		 */
		public void setValue(boolean value) {
			fValue = value;
		}

		/**
		 * Returns the value for the attribute names ignorance
		 * 
		 * @return
		 */
		public boolean getValue() {
			return fValue;
		}

		boolean isChanged() {
			return (originalValue != fValue);
		}
	}

	private class TextData {
		private String fKey;
		private String fValue;
		String originalValue = ""; //$NON-NLS-1$

		public TextData(String key) {
			fKey = key;
		}

		public String getKey() {
			return fKey;
		}

		/**
		 * Sets the ignored attribute names pattern
		 * 
		 * @param severity
		 *            the severity level
		 */
		public void setValue(String value) {
			fValue = value;
		}

		/**
		 * Returns non-null value for the ignored attribute names pattern
		 * 
		 * @return
		 */
		public String getValue() {
			return fValue != null ? fValue : ""; //$NON-NLS-1$
		}

		boolean isChanged() {
			return !originalValue.equalsIgnoreCase(fValue);
		}
	}

	public WebResourcesValidationPreferencePage() {
		super();
	}

	private PixelConverter fPixelConverter;

	protected Control createCommonContents(Composite parent) {
		final Composite page = new Composite(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		page.setLayout(layout);

		fPixelConverter = new PixelConverter(parent);

		final Composite content = createValidationSection(page);

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		gridData.heightHint = fPixelConverter.convertHeightInCharsToPixels(20);
		content.setLayoutData(gridData);

		return page;
	}

	private Composite createValidationSection(Composite page) {
		int nColumns = 3;

		final ScrolledPageContent spContent = new ScrolledPageContent(page);

		Composite composite = spContent.getBody();

		GridLayout layout = new GridLayout(nColumns, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		Label description = new Label(composite, SWT.NONE);
		description.setText(WebResourcesUIMessages.Validation_description);
		description.setFont(page.getFont());

		ExpandableComposite ec;
		Composite inner;
		String label;

		String[] errorWarningIgnoreLabel = new String[] {
				WebResourcesUIMessages.Validation_Error,
				WebResourcesUIMessages.Validation_Warning,
				WebResourcesUIMessages.Validation_Ignore };

		// The Files validation section

		ec = createStyleSection(composite,
				WebResourcesUIMessages.Expandable_label_files, nColumns);

		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);

		label = WebResourcesUIMessages.WebResourcesValidationPreferencePage_FILE_JS_UNKNOWN;
		addComboBox(inner, label,
				WebResourcesCorePreferenceNames.FILE_JS_UNKNOWN, SEVERITIES,
				errorWarningIgnoreLabel, 0);

		label = WebResourcesUIMessages.WebResourcesValidationPreferencePage_FILE_CSS_UNKNOWN;
		addComboBox(inner, label,
				WebResourcesCorePreferenceNames.FILE_CSS_UNKNOWN, SEVERITIES,
				errorWarningIgnoreLabel, 0);

		label = WebResourcesUIMessages.WebResourcesValidationPreferencePage_FILE_IMG_UNKNOWN;
		addComboBox(inner, label,
				WebResourcesCorePreferenceNames.FILE_IMG_UNKNOWN, SEVERITIES,
				errorWarningIgnoreLabel, 0);

		// End Files validation section

		// The CSS validation section

		ec = createStyleSection(composite,
				WebResourcesUIMessages.Expandable_label_css, nColumns);
		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);

		label = WebResourcesUIMessages.WebResourcesValidationPreferencePage_CSS_CLASS_UNKNOWN;
		addComboBox(inner, label,
				WebResourcesCorePreferenceNames.CSS_CLASS_UNKWOWN, SEVERITIES,
				errorWarningIgnoreLabel, 0);

		label = WebResourcesUIMessages.WebResourcesValidationPreferencePage_CSS_ID_UNKNOWN;
		addComboBox(inner, label,
				WebResourcesCorePreferenceNames.CSS_ID_UNKWOWN, SEVERITIES,
				errorWarningIgnoreLabel, 0);

		// End CSS validation section

		restoreSectionExpansionStates(getDialogSettings().getSection(
				SETTINGS_SECTION_NAME));

		return spContent;
	}

	private void setHorizontalIndent(Control control, int indent) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).horizontalIndent = indent;
		}
	}

	private void setWidthHint(Control control, int widthHint) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).widthHint = widthHint;
		}
	}

	@Override
	protected void controlChanged(Widget widget) {
		if (widget instanceof Text) {
			TextData data = (TextData) widget.getData();
			data.setValue(((Text) widget).getText());
		} else {
			super.controlChanged(widget);
		}
	}

	@Override
	protected void storeValues() {
		IScopeContext[] contexts = createPreferenceScopes();

		for (int i = 0; i < contexts.length; i++) {
			try {
				contexts[i].getNode(getPreferenceNodeQualifier()).flush();
			} catch (BackingStoreException e) {
				Logger.logException(e);
			}
		}

		super.storeValues();
	}

	@Override
	protected void performDefaults() {
		resetSeverities();
		super.performDefaults();
	}

	protected IDialogSettings getDialogSettings() {
		return WebResourcesUIPlugin.getDefault().getDialogSettings();
	}

	@Override
	public void dispose() {
		storeSectionExpansionStates(getDialogSettings().addNewSection(
				SETTINGS_SECTION_NAME));
		super.dispose();
	}

	protected String getQualifier() {
		return WebResourcesCorePlugin.getDefault().getBundle()
				.getSymbolicName();
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
		return WebResourcesCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS;
	}

	@Override
	protected String getPropertyPageID() {
		return PROPERTY_PAGE_ID;
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
