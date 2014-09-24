package org.eclipse.wst.html.webresources.internal.core;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.webresources.core.IWebResourcesFinderTypeProvider;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public class HTMLWebResourcesFinderTypeProvider implements
		IWebResourcesFinderTypeProvider {

	@Override
	public WebResourcesFinderType getWebResourcesFinderType(String elementName,
			String attrName, IStructuredDocumentRegion documentRegion,
			int documentPosition) {
		if (HTML40Namespace.ATTR_NAME_CLASS.equalsIgnoreCase(attrName)) {
			// @class
			return WebResourcesFinderType.CSS_CLASS_NAME;
		}
		if (HTML40Namespace.ATTR_NAME_ID.equalsIgnoreCase(attrName)) {
			// @id
			return WebResourcesFinderType.CSS_ID;
		}
		if (HTML40Namespace.ElementName.LINK.equalsIgnoreCase(elementName)
				&& HTML40Namespace.ATTR_NAME_HREF.equalsIgnoreCase(attrName)) {
			// link/@href
			return WebResourcesFinderType.LINK_HREF;
		}
		if (HTML40Namespace.ElementName.SCRIPT.equalsIgnoreCase(elementName)
				&& HTML40Namespace.ATTR_NAME_SRC.equalsIgnoreCase(attrName)) {
			// script/@src
			return WebResourcesFinderType.SCRIPT_SRC;
		}
		if (HTML40Namespace.ElementName.IMG.equalsIgnoreCase(elementName)
				&& HTML40Namespace.ATTR_NAME_SRC.equalsIgnoreCase(attrName)) {
			// img/@src
			return WebResourcesFinderType.IMG_SRC;
		}
		return null;
	}

}
