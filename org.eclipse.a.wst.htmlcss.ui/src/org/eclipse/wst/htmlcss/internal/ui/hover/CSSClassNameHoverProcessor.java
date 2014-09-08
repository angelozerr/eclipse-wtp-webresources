package org.eclipse.wst.htmlcss.internal.ui.hover;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.htmlcss.internal.ui.ClassNameFinder;
import org.eclipse.wst.htmlcss.internal.ui.ClassNameRegion;
import org.eclipse.wst.htmlcss.internal.ui.DOMHelper;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.taginfo.AbstractHoverProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class CSSClassNameHoverProcessor extends AbstractHoverProcessor {

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (hoverRegion instanceof ClassNameRegion) {
			IDOMNode xmlnode = (IDOMNode) ContentAssistUtils.getNodeAt(
					textViewer, hoverRegion.getOffset());
			HoverCSSClassTraverser traverser = new HoverCSSClassTraverser(
					xmlnode, ((ClassNameRegion) hoverRegion));
			traverser.process();
			return traverser.getInfo();
		}
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if ((textViewer == null) || (textViewer.getDocument() == null)) {
			return null;
		}

		IStructuredDocumentRegion documentRegion = ContentAssistUtils
				.getStructuredDocumentRegion(textViewer, offset);
		ITextRegion classAttrValueRegion = DOMHelper.getClassAttrValueRegion(
				documentRegion, offset);
		if (classAttrValueRegion != null) {
			int startOffset = documentRegion
					.getStartOffset(classAttrValueRegion);
			int endOffset = documentRegion.getEndOffset(classAttrValueRegion);
			int index = offset
					- documentRegion.getStartOffset(classAttrValueRegion);
			return ClassNameFinder.findName(textViewer.getDocument(), offset,
					startOffset, endOffset);
		}
		return null;
	}
}
