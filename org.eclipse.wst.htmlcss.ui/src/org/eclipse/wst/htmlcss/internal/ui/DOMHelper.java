package org.eclipse.wst.htmlcss.internal.ui;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class DOMHelper {

	/**
	 * Returns the "class" attribute value region from the given document region
	 * and position.
	 * 
	 * @param documentRegion
	 *            the structured document region.
	 * @param documentPosition
	 *            the position.
	 * @return the "class" attribute value region from the given document region
	 *         and position.
	 */
	public static ITextRegion getClassAttrValueRegion(
			IStructuredDocumentRegion documentRegion, int documentPosition) {
		Iterator regions = documentRegion.getRegions().iterator();
		ITextRegion classAttrNameRegion = null;
		int startOffset = documentPosition - documentRegion.getStartOffset();
		while (regions.hasNext()) {
			classAttrNameRegion = (ITextRegion) regions.next();
			if (classAttrNameRegion.getType().equals(
					DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
					&& documentRegion.getText(classAttrNameRegion)
							.equalsIgnoreCase(HTML40Namespace.ATTR_NAME_CLASS)) { //$NON-NLS-1$
				// the next region should be "="
				if (regions.hasNext()) {
					regions.next(); // skip the "="
					// next region should be attr value region
					if (regions.hasNext()) {
						ITextRegion classAttrValueRegion = (ITextRegion) regions
								.next();
						if (startOffset >= classAttrValueRegion.getStart()
								&& startOffset <= classAttrValueRegion.getEnd()) {
							return classAttrValueRegion;
						}
					}
				}
			}
		}
		return null;
	}

	public static String getInformation(ICSSStyleRule rule) {
		StringBuilder information = new StringBuilder();
		addInformation(rule, information);
		return information.toString();
	}

	public static void addInformation(ICSSStyleRule rule,
			StringBuilder information) {
		information.append("<b>CSS text:</b><br/>");
		information.append("<pre>");
		information.append(rule.getCssText());
		information.append("</pre>");
		information.append("<dl>");
		String fileName = getFileName(rule);
		if (fileName != null) {
			information.append("<dt><b>File:</b></dt>");
			information.append("<dd>");
			information.append(fileName);
			information.append("</dd>");
		}
		information.append("</dl>");
	}

	public static String getFileName(ICSSStyleRule rule) {
		String fileName = rule.getOwnerDocument().getModel().getBaseLocation();
		if (IModelManager.UNMANAGED_MODEL.equals(fileName)) {
			fileName = null;
		}
		return fileName;
	}

	public static String getAttrValue(String value) {
		if (value.startsWith("\"")) {
			value = value.substring(1, value.length());
		}
		if (value.endsWith("\"")) {
			value = value.substring(0, value.length() - 1);
		}
		return value;
	}

	/**
	 * Returns the owner file of the SSE DOM Node {@link IDOMNode}.
	 * 
	 * @param node
	 *            the SSE DOM Node.
	 * @return
	 */
	public static final IFile getFile(ICSSNode node) {
		return getFile(node.getOwnerDocument().getModel());
	}

	/**
	 * Returns the owner file of the JFace document {@link IDocument}.
	 * 
	 * @param document
	 * @return
	 */
	public static final IFile getFile(IDocument document) {
		if (document == null) {
			return null;
		}
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			if (model != null) {
				return getFile(model);
			}
		} finally {
			if (model != null)
				model.releaseFromRead();
		}
		return null;
	}

	/**
	 * Returns the owner file of the SSE model {@link IStructuredModel}.
	 * 
	 * @param node
	 *            the SSE model.
	 * @return
	 */
	public static final IFile getFile(IStructuredModel model) {
		String baselocation = model.getBaseLocation();
		if (baselocation != null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baselocation);
			if (filePath.segmentCount() > 1) {
				return root.getFile(filePath);
			}
		}
		return null;
	}

}
