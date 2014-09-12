package org.eclipse.wst.html.webresources.core;

public enum WebResourcesType {

	css, js, img;

	public static WebResourcesType get(String value) {
		WebResourcesType[] types = WebResourcesType.values();
		WebResourcesType type = null;
		for (int i = 0; i < types.length; i++) {
			type = types[i];
			if (type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		return null;
	}
}
