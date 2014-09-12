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
package org.eclipse.wst.html.webresources.internal.ui.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.html.webresources.core.DOMHelper;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.internal.ui.EditorUtils;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIMessages;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;

/**
 * CSS class name or ID hyperlink, used to open a CSS file and select the given
 * CSS style rule.
 *
 */
public class CSSHyperlink implements IHyperlink {

	private final IRegion classNameOrIdRegion;
	private final ICSSStyleRule rule;
	private final WebResourcesFinderType type;

	public CSSHyperlink(IRegion classNameOrIdRegion, ICSSStyleRule rule,
			WebResourcesFinderType type) {
		this.classNameOrIdRegion = classNameOrIdRegion;
		this.rule = rule;
		this.type = type;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return classNameOrIdRegion;
	}

	@Override
	public String getHyperlinkText() {
		String message = (type == WebResourcesFinderType.CSS_ID) ? WebResourcesUIMessages.CSSIDHyperLink_text
				: WebResourcesUIMessages.CSSClassNameHyperLink_text;
		return NLS.bind(message, rule.getSelectorText());
	}

	@Override
	public String getTypeLabel() {
		return type == WebResourcesFinderType.CSS_ID ? WebResourcesUIMessages.CSSIDHyperLink_typeLabel
				: WebResourcesUIMessages.CSSClassNameHyperLink_typeLabel;
	}

	@Override
	public void open() {
		// open the CSS file by selecting the given CSS rule.
		IFile file = DOMHelper.getFile(rule);
		if (file != null && file.exists()) {
			int start = ((IndexedRegion) rule).getStartOffset();
			int length = ((IndexedRegion) rule).getEndOffset() - start;
			EditorUtils.openInEditor(file, start, length, true);
		}
	}
}
