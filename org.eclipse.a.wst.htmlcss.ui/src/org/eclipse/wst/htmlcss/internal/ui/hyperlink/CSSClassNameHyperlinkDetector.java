package org.eclipse.wst.htmlcss.internal.ui.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.htmlcss.internal.ui.ClassNameFinder;
import org.eclipse.wst.htmlcss.internal.ui.ClassNameRegion;
import org.eclipse.wst.htmlcss.internal.ui.DOMHelper;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class CSSClassNameHyperlinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		if ((region != null) && (textViewer != null)) {

			IStructuredDocumentRegion documentRegion = ContentAssistUtils
					.getStructuredDocumentRegion(textViewer, region.getOffset());
			ITextRegion classAttrValueRegion = DOMHelper
					.getClassAttrValueRegion(documentRegion, region.getOffset());
			if (classAttrValueRegion != null) {
				String attrValue = DOMHelper.getAttrValue(documentRegion
						.getText(classAttrValueRegion));
				int startOffset = documentRegion
						.getStartOffset(classAttrValueRegion);
				int endOffset = documentRegion
						.getEndOffset(classAttrValueRegion);
				int index = region.getOffset()
						- documentRegion.getStartOffset(classAttrValueRegion);
				ClassNameRegion classNameRegion = ClassNameFinder.findName(
						textViewer.getDocument(), region.getOffset(),
						startOffset, endOffset);
				if (classNameRegion != null) {
					IDOMNode node = (IDOMNode) ContentAssistUtils.getNodeAt(
							textViewer, region.getOffset());
					HyperlinkCSSClassTraverser traverser = new HyperlinkCSSClassTraverser(
							node, classNameRegion);
					traverser.process();
					return traverser.getHyperlinks();
				}
			}
		}
		return null;
	}

}
