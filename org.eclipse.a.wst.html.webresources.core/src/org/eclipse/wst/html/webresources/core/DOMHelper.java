/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.html.webresources.core;

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

/**
 * DOM-SSE Utilities.
 *
 */
public class DOMHelper {

	/**
	 * Returns the "@class", "@id", "script/@src", "link/@href" or , "img/@src"
	 * attribute value region from the given document region and position and
	 * null otherwise.
	 * 
	 * @param documentRegion
	 *            the structured document region.
	 * @param documentPosition
	 *            the position.
	 * @return the "@class", "@id", "script/@src", "link/@href" or , "img/@src"
	 *         attribute value region from the given document region and
	 *         position and null otherwise.
	 */
	public static WebResourcesTextRegion getTextRegion(
			IStructuredDocumentRegion documentRegion, int documentPosition) {
		Iterator regions = documentRegion.getRegions().iterator();
		int startOffset = documentPosition - documentRegion.getStartOffset();
		ITextRegion currentRegion = null;
		String regionType = null;
		String elementName = null;
		String attrName = null;
		while (regions.hasNext()) {
			currentRegion = (ITextRegion) regions.next();
			regionType = currentRegion.getType();
			if (regionType.equals(DOMRegionContext.XML_TAG_NAME)) {
				// element name region
				elementName = documentRegion.getText(currentRegion);
			} else if (regionType
					.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
				// attribute name region
				attrName = documentRegion.getText(currentRegion);
				WebResourcesType type = getWebResourcesType(elementName,
						attrName);
				if (type != null) { //$NON-NLS-1$
					// the next region should be "="
					if (regions.hasNext()) {
						regions.next(); // skip the "="
						// next region should be attr value region
						if (regions.hasNext()) {
							ITextRegion attrValueRegion = (ITextRegion) regions
									.next();
							if (startOffset >= attrValueRegion.getStart()
									&& startOffset <= attrValueRegion.getEnd()) {
								return new WebResourcesTextRegion(
										attrValueRegion, type);
							}
						}
					}
				}
			}
		}
		return null;
	}

	private static WebResourcesType getWebResourcesType(String elementName,
			String attrName) {
		if (HTML40Namespace.ATTR_NAME_CLASS.equalsIgnoreCase(attrName)) {
			// @class
			return WebResourcesType.CSS_CLASS_NAME;
		}
		if (HTML40Namespace.ATTR_NAME_ID.equalsIgnoreCase(attrName)) {
			// @id
			return WebResourcesType.CSS_ID;
		}
		if (HTML40Namespace.ElementName.LINK.equalsIgnoreCase(elementName)
				&& HTML40Namespace.ATTR_NAME_HREF.equalsIgnoreCase(attrName)) {
			// link/@href
			return WebResourcesType.LINK_HREF;
		}
		if (HTML40Namespace.ElementName.SCRIPT.equalsIgnoreCase(elementName)
				&& HTML40Namespace.ATTR_NAME_SRC.equalsIgnoreCase(attrName)) {
			// script/@src
			return WebResourcesType.SCRIPT_SRC;
		}
		if (HTML40Namespace.ElementName.IMG.equalsIgnoreCase(elementName)
				&& HTML40Namespace.ATTR_NAME_SRC.equalsIgnoreCase(attrName)) {
			// img/@src
			return WebResourcesType.IMG_SRC;
		}
		return null;
	}

	public static String getInformation(ICSSStyleRule rule, IDOMNode node) {
		StringBuilder information = new StringBuilder();
		addInformation(rule, node, information);
		return information.toString();
	}

	public static void addInformation(ICSSStyleRule rule, IDOMNode node,
			StringBuilder information) {
		information.append("<b>CSS text:</b><br/>");
		information.append("<pre>");
		information.append(rule.getCssText());
		information.append("</pre>");
		information.append("<dl>");
		String fileName = getFileName(rule, node);
		if (fileName != null) {
			information.append("<dt><b>File:</b></dt>");
			information.append("<dd>");
			information.append(fileName);
			information.append("</dd>");
		}
		information.append("</dl>");
	}

	public static String getFileName(ICSSStyleRule rule, IDOMNode node) {
		String fileName = rule.getOwnerDocument().getModel().getBaseLocation();
		if (IModelManager.UNMANAGED_MODEL.equals(fileName)) {
			if (node != null) {
				return node.getModel().getBaseLocation();
			}
			return null;
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

	public static CSSClassNameOrIdRegion getCSSRegion(
			WebResourcesTextRegion cssRegion,
			IStructuredDocumentRegion documentRegion, IDocument document,
			int offset) {
		ITextRegion textRegion = cssRegion.getRegion();
		int startOffset = documentRegion.getStartOffset(textRegion);
		String attrValue = DOMHelper.getAttrValue(documentRegion
				.getText(textRegion));
		switch (cssRegion.getType()) {
		case CSS_CLASS_NAME:
			int endOffset = documentRegion.getEndOffset(textRegion);
			return CSSClassNameFinder.findClassName(document, offset,
					startOffset, endOffset);
		case CSS_ID:
			return new CSSClassNameOrIdRegion(startOffset + 1,
					attrValue.length(), attrValue, cssRegion.getType());
		default:
			return null;
		}
	}

}
