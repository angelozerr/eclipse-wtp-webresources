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
package org.eclipse.wst.html.webresources.core.helpers;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * Helper for CSS-SSE.
 *
 */
public class CSSHelper {

	/**
	 * Returns the information of the given CSS style rule.
	 * 
	 * @param rule
	 * @param node
	 * @return the information of the given CSS style rule.
	 */
	public static String getInformation(ICSSStyleRule rule, IDOMNode node) {
		StringBuilder information = new StringBuilder();
		addInformation(rule, node, information);
		return information.toString();
	}

	/**
	 * Add information of the information of the given CSS style rule in the
	 * given buffer.
	 * 
	 * @param rule
	 * @param node
	 * @param information
	 */
	public static void addInformation(ICSSStyleRule rule, IDOMNode node,
			StringBuilder information) {
		information.append("<b>CSS text:</b><br/>");
		information.append("<pre>");
		information.append(rule.getCssText());
		information.append("</pre>");
		information.append("<dl>");
		String fileName = DOMHelper.getFileName(rule, node);
		if (fileName != null) {
			information.append("<dt><b>File:</b></dt>");
			information.append("<dd>");
			information.append(fileName);
			information.append("</dd>");
		}
		information.append("</dl>");
	}
}
