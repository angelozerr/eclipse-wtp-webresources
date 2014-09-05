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
package org.eclipse.wst.htmlcss.internal.ui.contentassist;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.htmlcss.internal.ui.DOMHelper;
import org.eclipse.wst.htmlcss.internal.ui.ImageResource;
import org.eclipse.wst.htmlcss.ui.core.AbstractCSSClassTraverser;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;

/**
 * 
 */
public class ContentAssistCSSClassTraverser extends AbstractCSSClassTraverser {

	private final ContentAssistRequest contentAssistRequest;
	private String matchingClassName;
	private int replacementOffset;
	private int replacementLength;

	public ContentAssistCSSClassTraverser(
			ContentAssistRequest contentAssistRequest, int documentPosition,
			String attrValue) {
		super((IDOMNode) contentAssistRequest.getNode());
		this.contentAssistRequest = contentAssistRequest;

		// ex : <input class="todo-done myClass" />
		// completion done after my (todo-done my // Here Ctrl+Space)

		// matching string = "todo-done my"
		String matchingString = DOMHelper.getAttrValue(contentAssistRequest
				.getMatchString());

		// matching class name = "my"
		matchingClassName = matchingString;
		int index = matchingClassName.lastIndexOf(" ");
		if (index != -1) {
			matchingClassName = matchingClassName.substring(index + 1,
					matchingClassName.length());
		}

		// after matching class name = "Class"
		String afterMatchingClassName = attrValue.substring(
				matchingString.length(), attrValue.length());
		index = afterMatchingClassName.indexOf(" ");
		if (index != -1) {
			afterMatchingClassName = afterMatchingClassName.substring(0, index);
		}

		this.replacementOffset = documentPosition - matchingClassName.length();
		this.replacementLength = matchingClassName.length()
				+ afterMatchingClassName.length();

	}

	@Override
	protected void addClassName(String className, ICSSStyleRule rule) {
		// check if visited class name starts with matching class name?
		if (!className.startsWith(matchingClassName)) {
			return;
		}

		// Retrieve the file name of the CSS style rule.
		String fileName = rule.getOwnerDocument().getModel().getBaseLocation();
		if (IModelManager.UNMANAGED_MODEL.equals(fileName)) {
			fileName = null;
		}

		StringBuilder info = new StringBuilder();
		info.append("<pre>");
		info.append(rule.getCssText());
		info.append("</pre>");
		if (fileName != null) {
			info.append("<p>");
			info.append(fileName);
			info.append("</p>");
		}
		String displayString = fileName != null ? new StringBuilder(className)
				.append(" - ").append(fileName).toString() : className;

		int cursorPosition = className.length();
		contentAssistRequest.addProposal(new CompletionProposal(className,
				replacementOffset, replacementLength, cursorPosition,
				ImageResource.getImage(ImageResource.IMG_CLASSNAME),
				displayString, null, info.toString()));
	}

}
