package org.eclipse.wst.html.webresources.core;

public enum WebResourceType {

	css, js, img;

	public static WebResourceType get(String value) {
		WebResourceType[] types = WebResourceType.values();
		WebResourceType type = null;
		for (int i = 0; i < types.length; i++) {
			type = types[i];
			if (type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		return null;
	}
	 
}
