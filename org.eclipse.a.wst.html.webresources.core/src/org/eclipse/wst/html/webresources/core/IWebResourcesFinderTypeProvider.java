package org.eclipse.wst.html.webresources.core;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public interface IWebResourcesFinderTypeProvider {

	WebResourcesFinderType getWebResourcesFinderType(String elementName,
			String attrName, IStructuredDocumentRegion documentRegion,
			int documentPosition);
}
