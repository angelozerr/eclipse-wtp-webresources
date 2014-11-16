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
package org.eclipse.wst.html.webresources.internal.core.validation;

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
import org.eclipse.wst.html.webresources.internal.core.WebResourcesCoreMessages;
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
	private final IProject fProject;
	private IScopeContext[] fLookupOrder;
	private IPreferencesService fPreferenceService;

	public MessageFactory(IProject project, IValidator validator,
			IReporter reporter) {
		fProject = project;
		this.validator = validator;
		this.reporter = reporter;
		init();
	}

	public int getSeverity(WebResourcesFinderType type) {
		return getSeverity(getPreferenceName(type));
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

	private int getSeverity(String key) {
		return fPreferenceService.getInt(getQualifier(), key,
				ValidationMessage.WARNING, fLookupOrder);
	}

	private void init() {
		fPreferenceService = Platform.getPreferencesService();
		fLookupOrder = new IScopeContext[] { new InstanceScope(),
				new DefaultScope() };

		if (fProject != null) {
			ProjectScope projectScope = new ProjectScope(fProject);
			if (projectScope
					.getNode(getQualifier())
					.getBoolean(
							WebResourcesCorePreferenceNames.USE_PROJECT_SETTINGS,
							false))
				fLookupOrder = new IScopeContext[] { projectScope,
						new InstanceScope(), new DefaultScope() };
		}
	}

	private String getQualifier() {
		return WebResourcesCorePlugin.getDefault().getBundle()
				.getSymbolicName();
	}

	public void addMessage(IDOMAttr attr, WebResourcesFinderType type,
			IFile file) {
		String textContent = attr.getValue();
		int start = attr.getValueRegionStartOffset();
		addMessage(attr, start, textContent, type, file);
	}

	public void addMessage(IDOMAttr node, int start, String textContent,
			WebResourcesFinderType type, IResource resource) {
		int length = textContent.trim().length();
		String messageText = NLS.bind(getMessageText(type), textContent);
		int severity = getSeverity(type);
		IMessage message = createMessage(start, length, messageText, severity,
				node.getStructuredDocument(), resource);
		reporter.addMessage(validator, message);
	}

	private String getMessageText(WebResourcesFinderType type) {
		switch (type) {
		case CSS_CLASS_NAME:
			return WebResourcesValidationMessages.Validation_CSS_CLASS_UNDEFINED;
		case CSS_ID:
			return WebResourcesValidationMessages.Validation_CSS_ID_UNDEFINED;
		case SCRIPT_SRC:
			return WebResourcesValidationMessages.Validation_FILE_JS_UNDEFINED;
		case LINK_HREF:
			return WebResourcesValidationMessages.Validation_FILE_CSS_UNDEFINED;
		case IMG_SRC:
			return WebResourcesValidationMessages.Validation_FILE_IMG_UNDEFINED;
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

}
