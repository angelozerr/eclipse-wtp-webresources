package org.eclipse.wst.html.webresources.core.providers;

import java.util.HashMap;

public class WebResourcesProviderContext extends HashMap<String, Object> {

	private static final String HAS_EXTERNAL_CSS = "hasExternalCSS";

	public WebResourcesProviderContext setHasExternalCSS(boolean hasExternalCSS) {
		super.put(HAS_EXTERNAL_CSS, hasExternalCSS);
		return this;
	}

	public boolean hasExternalCSS() {
		Boolean result = (Boolean) super.get(HAS_EXTERNAL_CSS);
		return result != null ? result.booleanValue() : false;
	}
}
