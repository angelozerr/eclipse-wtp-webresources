package org.eclipse.wst.htmlcss.ui.hyperlink;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.htmlcss.internal.ui.ClassNameRegion;
import org.eclipse.wst.htmlcss.internal.ui.ClassNameFinder;
import org.eclipse.wst.htmlcss.internal.ui.DOMHelper;
import org.eclipse.wst.htmlcss.internal.ui.hyperlink.ClassAttributeHyperlink;
import org.eclipse.wst.htmlcss.internal.ui.hyperlink.HyperlinkCSSClassTraverser;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ClassAttributeHyperlinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		if ((region != null) && (textViewer != null)) {

			IStructuredDocumentRegion sdRegion = ContentAssistUtils
					.getStructuredDocumentRegion(textViewer, region.getOffset());
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
							int startOffset = sdRegion
									.getStartOffset(styleValueRegion);
							int endOffset = sdRegion
									.getEndOffset(styleValueRegion);
							int index = region.getOffset()
									- sdRegion.getStartOffset(styleValueRegion);
							// DOMHelper.getClassName(attrValue, index);
							ClassNameRegion classNameRegion = ClassNameFinder
									.findName(textViewer.getDocument(),
											region.getOffset(), startOffset,
											endOffset);
							if (classNameRegion != null) {
								IDOMNode node = (IDOMNode) ContentAssistUtils
										.getNodeAt(textViewer,
												region.getOffset());
								HyperlinkCSSClassTraverser traverser = new HyperlinkCSSClassTraverser(
										node, classNameRegion);
								traverser.process();
								return traverser.getHyperlinks();
							}
						}
					}
				}
			}
		}
		return null;
	}

}
