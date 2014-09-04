package org.eclipse.wst.htmlcss.ui.contentassist;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetListAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.CSSClassTraverser;
import org.eclipse.wst.html.core.internal.htmlcss.HTMLDocumentAdapter;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;
import org.eclipse.wst.htmlcss.internal.ui.contentassist.ContentAssistCSSClassTraverser;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.w3c.dom.stylesheets.StyleSheetList;

public class ClassAttributeCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	public ClassAttributeCompletionProposalComputer() {
		System.err.println("eee");
	}

	@Override
	public List computeCompletionProposals(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return super.computeCompletionProposals(context, monitor);
	}

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
						int offset = sdRegion.getStartOffset(styleValueRegion);

						String attrVale = sdRegion.getText(styleValueRegion);
						int pos = 200 - offset;
						addCSSClassProposals(contentAssistRequest,
								context.getInvocationOffset(), element, offset,
								(char) 0);
					}
				}
			}
		}
	}

	protected void addCSSClassProposals(
			ContentAssistRequest contentAssistRequest, int pos,
			IDOMNode element, int offset, char quote) {
		ContentAssistCSSClassTraverser traverser = new ContentAssistCSSClassTraverser(
				element, contentAssistRequest, pos);
		traverser.process();
	}
}
