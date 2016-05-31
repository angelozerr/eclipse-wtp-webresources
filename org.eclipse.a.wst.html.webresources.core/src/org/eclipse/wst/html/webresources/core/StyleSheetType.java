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
package org.eclipse.wst.html.webresources.core;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;

public enum StyleSheetType {

	EXTERNAL(ICSSModel.EXTERNAL), EMBEDDED(ICSSModel.EMBEDDED), INLINE(
			ICSSModel.INLINE);

	private final String value;

	private StyleSheetType(String value) {
		this.value = value;
	}

	public static StyleSheetType getType(String value) {
		StyleSheetType[] types = values();
		StyleSheetType type;
		for (int i = 0; i < types.length; i++) {
			type = types[i];
			if (type.value.equals(value)) {
				return type;
			}
		}
		return INLINE;
	}

	public static StyleSheetType getType(ICSSNode node) {
		return getType(node.getOwnerDocument().getModel().getStyleSheetType()
				.toString());
	}
}
