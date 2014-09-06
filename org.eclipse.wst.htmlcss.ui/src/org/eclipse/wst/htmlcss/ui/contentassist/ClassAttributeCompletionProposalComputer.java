package org.eclipse.wst.htmlcss.ui.contentassist;

import org.eclipse.wst.htmlcss.internal.ui.DOMHelper;
import org.eclipse.wst.htmlcss.internal.ui.contentassist.ContentAssistCSSClassTraverser;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;

public class ClassAttributeCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	@Override
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		IDOMNode element = (IDOMNode) contentAssistRequest.getNode();

		IStructuredDocumentRegion documentRegion = contentAssistRequest
				.getDocumentRegion();
		ITextRegion classAttrValueRegion = DOMHelper
				.getClassAttrValueRegion(documentRegion);
		if (classAttrValueRegion != null) {
			String attrValue = DOMHelper.getAttrValue(documentRegion
					.getText(classAttrValueRegion));
			addCSSClassProposals(contentAssistRequest,
					context.getInvocationOffset(), attrValue);
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
