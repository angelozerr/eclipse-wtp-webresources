package org.eclipse.wst.htmlcss.ui.hover;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.htmlcss.internal.ui.ClassNameFinder;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.Logger;

public class ClassAttributeHoverProcessor extends HTMLTagInfoHoverProcessor {

	@Override
	protected String computeTagAttNameHelp(IDOMNode xmlnode,
			IDOMNode parentNode, IStructuredDocumentRegion flatNode,
			ITextRegion region) {
		String attName = flatNode.getText(region);

		return super.computeTagAttNameHelp(xmlnode, parentNode, flatNode,
				region);
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if ((textViewer == null) || (textViewer.getDocument() == null)) {
			return null;
		}
		IStructuredDocumentRegion flatNode = ((IStructuredDocument) textViewer
				.getDocument()).getRegionAtCharacterOffset(offset);
		ITextRegion region = null;

		if (flatNode != null) {
			region = flatNode.getRegionAtCharacterOffset(offset);
		}
		if (region != null) {
			// only supply hoverhelp for tag name, attribute name, or
			// attribute value
			String regionType = region.getType();
			if ((regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
				try {
					// check if we are at whitespace before or after line
					IRegion line = textViewer.getDocument()
							.getLineInformationOfOffset(offset);
					if ((offset > (line.getOffset()))
							&& (offset < (line.getOffset() + line.getLength()))) {
						// check if we are in region's trailing whitespace
						// (whitespace after relevant info)
						if (offset < flatNode.getTextEndOffset(region)) {
							return new Region(flatNode.getStartOffset(region),
									region.getTextLength());
						}
					}
				} catch (BadLocationException e) {
					Logger.logException(e);
				}
			}
		}
		return null;//ClassWordFinder.findWord(textViewer.getDocument(), offset);
	}
}
