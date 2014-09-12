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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.html.webresources.core.AbstractCSSClassNameOrIdTraverser;
import org.eclipse.wst.html.webresources.core.WebResourceRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * CSS traverser implementation for CSS class name and CSS id hyperlink.
 */
public class CSSHyperlinkTraverser extends AbstractCSSClassNameOrIdTraverser {

	private static final IHyperlink[] EMPTY_HYPERLINK = new IHyperlink[0];
	private List<IHyperlink> hyperlinks;
	private final WebResourceRegion cssRegion;

	public CSSHyperlinkTraverser(IDOMNode node, WebResourceRegion cssRegion) {
		super(node, cssRegion.getType());
		this.cssRegion = cssRegion;
	}

	@Override
	protected void collect(String classNameOrId, ICSSStyleRule rule) {
		if (cssRegion.getValue().equals(classNameOrId)) {
			if (hyperlinks == null) {
				hyperlinks = new ArrayList<IHyperlink>();
			}
			hyperlinks.add(new CSSHyperlink(cssRegion, rule, cssRegion
					.getType()));
		}
	}

	public IHyperlink[] getHyperlinks() {
		if (hyperlinks == null) {
			return null;
		}
		return hyperlinks.toArray(EMPTY_HYPERLINK);
	}

}
