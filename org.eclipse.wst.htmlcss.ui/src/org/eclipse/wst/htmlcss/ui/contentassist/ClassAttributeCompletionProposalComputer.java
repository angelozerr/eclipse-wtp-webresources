package org.eclipse.wst.htmlcss.ui.contentassist;

import java.util.Iterator;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.htmlcss.internal.ui.DOMHelper;
import org.eclipse.wst.htmlcss.internal.ui.contentassist.ContentAssistCSSClassTraverser;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;

public class ClassAttributeCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	@Override
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		IDOMNode element = (IDOMNode) contentAssistRequest.getNode();

		IStructuredDocumentRegion sdRegion = contentAssistRequest
				.getDocumentRegion();
		Iterator regions = sdRegion.getRegions().iterator();
		ITextRegion styleNameRegion = null;
		ITextRegion styleValueRegion = null;
		while (regions.hasNext()) {
			styleNameRegion = (ITextRegion) regions.next();
			if (styleNameRegion.getType().equals(
					DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
					&& sdRegion.getText(styleNameRegion).equalsIgnoreCase(
							HTML40Namespace.ATTR_NAME_CLASS)) { //$NON-NLS-1$
				// the next region should be "="
				if (regions.hasNext()) {
					regions.next(); // skip the "="
					// next region should be attr value region
					if (regions.hasNext()) {
						styleValueRegion = (ITextRegion) regions.next();
						String attrValue = DOMHelper.getAttrValue(sdRegion
								.getText(styleValueRegion));
						addCSSClassProposals(contentAssistRequest,
								context.getInvocationOffset(), attrValue);
					}
				}
			}
		}
	}

	protected void addCSSClassProposals(
			ContentAssistRequest contentAssistRequest, int documentPosition,
			String attrValue) {
		ContentAssistCSSClassTraverser traverser = new ContentAssistCSSClassTraverser(
				contentAssistRequest, documentPosition, attrValue);
		traverser.process();
	}
}
