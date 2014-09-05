package org.eclipse.wst.htmlcss.internal.ui;

public class DOMHelper {

	public static String getAttrValue(String value) {
		if (value.startsWith("\"")) {
			value = value.substring(1, value.length());
		}
		if (value.endsWith("\"")) {
			value = value.substring(0, value.length() - 1);
		}
		return value;
	}
}
