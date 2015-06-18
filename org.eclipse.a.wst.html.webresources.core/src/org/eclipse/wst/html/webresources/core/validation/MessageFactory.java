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
package org.eclipse.wst.html.webresources.core.validation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.core.WebResourcesValidationMessages;
import org.eclipse.wst.html.webresources.core.preferences.WebResourcesCorePreferenceNames;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.html.webresources.internal.core.validation.LocalizedMessage;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

/**
 * Validation message factory for web resources.
 *
 */
public class MessageFactory {

	private final IValidator validator;
	private final IReporter reporter;
	private final Map<WebResourcesFinderType, Integer> severities;
	private final boolean validateExternalURL;

	public MessageFactory(IProject project, IValidator validator,
			IReporter reporter) {
		this.validator = validator;
		this.reporter = reporter;
		this.severities = new HashMap<WebResourcesFinderType, Integer>();
		this.validateExternalURL = init(project, severities);
	}

	public int getSeverity(WebResourcesFinderType type) {
		return severities.get(type);
	}

	private String getPreferenceName(WebResourcesFinderType type) {
		switch (type) {
		case CSS_CLASS_NAME:
			return WebResourcesCorePreferenceNames.CSS_CLASS_UNKWOWN;
		case CSS_ID:
			return WebResourcesCorePreferenceNames.CSS_ID_UNKWOWN;
		case SCRIPT_SRC:
			return WebResourcesCorePreferenceNames.FILE_JS_UNKNOWN;
		case LINK_HREF:
			return WebResourcesCorePreferenceNames.FILE_CSS_UNKNOWN;
		case IMG_SRC:
			return WebResourcesCorePreferenceNames.FILE_IMG_UNKNOWN;
		}
		return null;
	}

	private boolean init(IProject project,
			Map<WebResourcesFinderType, Integer> severities) {
		IPreferencesService preferenceService = Platform
				.getPreferencesService();
		IScopeContext[] lookupOrder = new IScopeContext[] {
				new InstanceScope(), new DefaultScope() };

		if (project != null) {
			ProjectScope projectScope = new ProjectScope(project);
			if (projectScope
					.getNode(getQualifier())
					.getBoolean(
							WebResourcesCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS,
							false))
				lookupOrder = new IScopeContext[] { projectScope,
						new InstanceScope(), new DefaultScope() };
		}
		WebResourcesFinderType[] types = WebResourcesFinderType.values();
		WebResourcesFinderType type = null;
		for (int i = 0; i < types.length; i++) {
			type = types[i];
			severities.put(
					type,
					getSeverity(getPreferenceName(type), preferenceService,
							lookupOrder));
		}

		return preferenceService.getBoolean(getQualifier(),
				WebResourcesCorePreferenceNames.EXTERNAL_URL_UNKWOWN,
				WebResourcesCorePreferenceNames.EXTERNAL_URL_UNKWOWN_DEFAULT,
				lookupOrder);
	}

	private int getSeverity(String key, IPreferencesService preferenceService,
			IScopeContext[] lookupOrder) {
		return preferenceService.getInt(getQualifier(), key,
				ValidationMessage.WARNING, lookupOrder);
	}

	private String getQualifier() {
		return WebResourcesCorePlugin.getDefault().getBundle()
				.getSymbolicName();
	}

	public void addMessage(IDOMAttr attr, WebResourcesFinderType type,
			IFile file) {
		addMessage(attr, type, file, false);
	}

	public void addMessage(IDOMAttr attr, WebResourcesFinderType type,
			IFile file, boolean externalURL) {
		String textContent = attr.getValueRegionText();
		int start = attr.getValueRegionStartOffset();
		addMessage(attr, start, textContent, type, file, externalURL);
	}

	public void addMessage(IDOMAttr node, int start, String textContent,
			WebResourcesFinderType type, IResource resource) {
		addMessage(node, start, textContent, type, resource, false);
	}

	public void addMessage(IDOMAttr node, int start, String textContent,
			WebResourcesFinderType type, IResource resource, boolean externalURL) {
		int length = textContent.trim().length();
		String messageText = NLS.bind(getMessageText(type, externalURL),
				textContent);
		int severity = getSeverity(type);
		IMessage message = createMessage(start, length, messageText, severity,
				node.getStructuredDocument(), resource);
		addMessage(message, type);
	}

	protected void addMessage(IMessage message, WebResourcesFinderType type) {
		reporter.addMessage(validator, message);
	}

	private String getMessageText(WebResourcesFinderType type,
			boolean externalURL) {
		switch (type) {
		case CSS_CLASS_NAME:
			return WebResourcesValidationMessages.Validation_CSS_CLASS_UNDEFINED;
		case CSS_ID:
			return WebResourcesValidationMessages.Validation_CSS_ID_UNDEFINED;
		case SCRIPT_SRC:
			return externalURL ? WebResourcesValidationMessages.Validation_URL_JS_UNDEFINED
					: WebResourcesValidationMessages.Validation_FILE_JS_UNDEFINED;
		case LINK_HREF:
			return externalURL ? WebResourcesValidationMessages.Validation_URL_CSS_UNDEFINED
					: WebResourcesValidationMessages.Validation_FILE_CSS_UNDEFINED;
		case IMG_SRC:
			return externalURL ? WebResourcesValidationMessages.Validation_URL_IMG_UNDEFINED
					: WebResourcesValidationMessages.Validation_FILE_IMG_UNDEFINED;
		}
		return null;
	}

	private IMessage createMessage(int start, int length, String messageText,
			int severity, IStructuredDocument structuredDocument,
			IResource resource) {
		int lineNo = getLineNumber(start, structuredDocument);
		LocalizedMessage message = new LocalizedMessage(severity, messageText,
				resource);
		message.setOffset(start);
		message.setLength(length);
		message.setLineNo(lineNo);
		return message;
	}

	private int getLineNumber(int start, IDocument document) {
		int lineNo = -1;
		try {
			lineNo = document.getLineOfOffset(start);
		} catch (BadLocationException e) {
			Trace.trace(Trace.SEVERE, e.getMessage(), e);
		}
		return lineNo;
	}

	public IValidator getValidator() {
		return validator;
	}

	protected IReporter getReporter() {
		return reporter;
	}

	public boolean isValidateExternalURL() {
		return validateExternalURL;
	}
}
