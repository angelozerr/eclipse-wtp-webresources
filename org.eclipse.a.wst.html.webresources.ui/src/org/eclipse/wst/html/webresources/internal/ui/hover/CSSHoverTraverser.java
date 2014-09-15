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
package org.eclipse.wst.html.webresources.internal.ui.hover;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.html.webresources.core.AbstractCSSClassNameOrIdTraverser;
import org.eclipse.wst.html.webresources.core.WebResourceRegion;
import org.eclipse.wst.html.webresources.internal.ui.utils.HTMLWebResourcesPrinter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * CSS traverser implementation for CSS class name and CSS id hover.
 */
public class CSSHoverTraverser extends AbstractCSSClassNameOrIdTraverser {

	private final WebResourceRegion cssRegion;
	private String info;

	public CSSHoverTraverser(IDOMNode node, WebResourceRegion cssRegion) {
		super(node, cssRegion.getType());
		this.cssRegion = cssRegion;
	}

	@Override
	protected void collect(String className, ICSSStyleRule rule) {
		if (cssRegion.getValue().equals(className)) {
			info = HTMLWebResourcesPrinter.getAdditionalProposalInfo(rule,
					cssRegion.getType(), getNode());
		}
	}

	public String getInfo() {
		return info;
	}
}
