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
		StyleSheetType type = null;
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
