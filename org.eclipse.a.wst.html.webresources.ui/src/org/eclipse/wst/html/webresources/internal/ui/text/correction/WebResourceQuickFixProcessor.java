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
package org.eclipse.wst.html.webresources.internal.ui.text.correction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension2;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.wst.html.core.internal.validate.StringMatcher;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.core.WebResourcesValidationMessages;
import org.eclipse.wst.html.webresources.core.utils.DOMHelper;
import org.eclipse.wst.html.webresources.internal.ui.utils.EditorUtils;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

public class WebResourceQuickFixProcessor implements IQuickAssistProcessor {

	private final StringMatcher Validation_FILE_CSS_UNDEFINED_MATCHER;
	private final StringMatcher Validation_FILE_JS_UNDEFINED_MATCHER;

	public WebResourceQuickFixProcessor() {
		Validation_FILE_CSS_UNDEFINED_MATCHER = createMatcher(WebResourcesValidationMessages.Validation_FILE_CSS_UNDEFINED);
		Validation_FILE_JS_UNDEFINED_MATCHER = createMatcher(WebResourcesValidationMessages.Validation_FILE_JS_UNDEFINED);
	}

	private StringMatcher createMatcher(String templ) {
		templ = templ.replaceAll("\\{[0-9]*\\}", "\\*");
		return new StringMatcher(templ);
	}

	@Override
	public boolean canFix(Annotation annotation) {
		boolean result = false;

		String text = null;
		if (annotation instanceof TemporaryAnnotation) {
			TemporaryAnnotation tempAnnotation = (TemporaryAnnotation) annotation;
			int problemID = tempAnnotation.getProblemID();
			text = tempAnnotation.getText();

			if (problemID == 0 && text != null)
				result = true;
		} else if (annotation instanceof MarkerAnnotation) {
			MarkerAnnotation markerAnnotation = (MarkerAnnotation) annotation;
			text = markerAnnotation.getText();
			IMarker marker = markerAnnotation.getMarker();
			IResource resource = marker == null ? null : marker.getResource();
			if (resource != null && resource.exists()
					&& resource.isAccessible() && text != null) {
				result = true;
			}
		}

		result = result && isWebResourceError(text);

		return result;
	}

	private boolean isWebResourceError(String text) {
		return getWebResourcesFinderType(text) != null;
	}

	private WebResourcesFinderType getWebResourcesFinderType(String text) {
		if (Validation_FILE_CSS_UNDEFINED_MATCHER.match(text)) {
			return WebResourcesFinderType.LINK_HREF;
		}
		if (Validation_FILE_JS_UNDEFINED_MATCHER.match(text)) {
			return WebResourcesFinderType.SCRIPT_SRC;
		}
		return null;
	}

	@Override
	public ICompletionProposal[] computeQuickAssistProposals(
			IQuickAssistInvocationContext invocationContext) {
		ISourceViewer viewer = invocationContext.getSourceViewer();
		int documentOffset = invocationContext.getOffset();
		int length = viewer != null ? viewer.getSelectedRange().y : 0;

		IAnnotationModel model = viewer.getAnnotationModel();
		if (model == null)
			return null;

		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		if (model instanceof IAnnotationModelExtension2) {
			Iterator iter = ((IAnnotationModelExtension2) model)
					.getAnnotationIterator(documentOffset, length, true, true);
			while (iter.hasNext()) {
				Annotation anno = (Annotation) iter.next();
				if (canFix(anno)) {
					int offset = -1;

					if (anno instanceof TemporaryAnnotation) {
						offset = ((TemporaryAnnotation) anno).getPosition()
								.getOffset();
					} else if (anno instanceof MarkerAnnotation) {
						offset = ((MarkerAnnotation) anno).getMarker()
								.getAttribute(IMarker.CHAR_START, -1);
					}
					if (offset == -1)
						continue;

					// add "New file wizard" completion proposal
					IDOMAttr attr = DOMHelper.getAttrByOffset(
							viewer.getDocument(), offset);
					if (attr != null) {
						IFile file = EditorUtils.getFile(viewer.getDocument());
						CreateFileCompletionProposal proposal = new CreateFileCompletionProposal(
								file, attr);
						if (!proposals.contains(proposal)) {
							proposals.add(proposal);
						}
					}
				}
			}
		}

		if (proposals.isEmpty())
			return null;

		return (ICompletionProposal[]) proposals
				.toArray(new ICompletionProposal[proposals.size()]);

	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		return true;
	}

}
