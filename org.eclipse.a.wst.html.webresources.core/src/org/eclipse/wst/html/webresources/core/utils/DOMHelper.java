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
package org.eclipse.wst.html.webresources.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.html.webresources.core.CSSClassNameFinder;
import org.eclipse.wst.html.webresources.core.WebResourceRegion;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.core.WebResourcesTextRegion;
import org.eclipse.wst.html.webresources.internal.core.WebResourcesFinderTypeProviderManager;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

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
			IStructuredDocumentRegion documentRegion, int documentPosition,
			boolean ignoreOffset) {
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
				WebResourcesFinderType type = getWebResourcesFinderType(
						elementName, attrName, documentRegion, documentPosition);
				if (type != null) { //$NON-NLS-1$
					// the next region should be "="
					if (regions.hasNext()) {
						regions.next(); // skip the "="
						// next region should be attr value region
						if (regions.hasNext()) {
							ITextRegion attrValueRegion = (ITextRegion) regions
									.next();
							if (ignoreOffset
									|| (startOffset >= attrValueRegion
											.getStart() && startOffset <= attrValueRegion
											.getEnd())) {
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

	public static WebResourcesTextRegion getTextRegion(
			IStructuredDocumentRegion documentRegion, int documentPosition) {
		return getTextRegion(documentRegion, documentPosition, false);
	}

	private static WebResourcesFinderType getWebResourcesFinderType(
			String elementName, String attrName,
			IStructuredDocumentRegion documentRegion, int documentPosition) {
		return WebResourcesFinderTypeProviderManager.getManager()
				.getWebResourcesFinderType(elementName, attrName,
						documentRegion, documentPosition);
	}

	public static String getFileName(ICSSNode cssNode, IDOMNode node) {
		String fileName = cssNode.getOwnerDocument().getModel()
				.getBaseLocation();
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
	public static final IFile getFile(IDOMNode node) {
		return getFile(node.getModel());
	}

	/**
	 * Returns the owner file of the SSE CSS Node {@link ICSSNode}.
	 * 
	 * @param node
	 *            the SSE CSS Node.
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

	/**
	 * Returns the owner file of the SSE CSS Node {@link ICSSNode}.
	 * 
	 * @param node
	 *            the SSE CSS Node.
	 * @return
	 */
	public static final File getFileSystem(ICSSNode node) {
		return getFileSystem(node.getOwnerDocument().getModel());
	}

	/**
	 * Returns the owner file of the SSE model {@link IStructuredModel}.
	 * 
	 * @param node
	 *            the SSE model.
	 * @return
	 */
	public static final File getFileSystem(IStructuredModel model) {
		String baselocation = model.getBaseLocation();
		if (baselocation != null) {
			return new File(baselocation);
		}
		return null;
	}

	public static WebResourceRegion getCSSRegion(
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
			return getAttrValueRegion(cssRegion, documentRegion, document,
					offset);
		default:
			return null;
		}
	}

	public static WebResourceRegion getAttrValueRegion(
			WebResourcesTextRegion cssRegion,
			IStructuredDocumentRegion documentRegion, IDocument document,
			int offset) {
		ITextRegion textRegion = cssRegion.getRegion();
		int startOffset = documentRegion.getStartOffset(textRegion);
		String attrValue = DOMHelper.getAttrValue(documentRegion
				.getText(textRegion));
		return new WebResourceRegion(startOffset + 1, attrValue.length(),
				attrValue, cssRegion.getType());
	}

	public static ICSSModel getModel(IFile file) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(file);
			if (model == null) {
				model = StructuredModelManager.getModelManager()
						.getModelForRead(file);
			}
			return (ICSSModel) model;
		} catch (Exception e) {

		}
		if (model != null) {
			model.releaseFromRead();
		}
		return null;
	}

	public static ICSSModel getModel(File file) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(file);
			if (model == null) {
				model = StructuredModelManager.getModelManager()
						.getModelForRead(file.getPath(),
								new FileInputStream(file), null);
			}
			return (ICSSModel) model;
		} catch (Exception e) {

		}
		if (model != null) {
			model.releaseFromRead();
		}
		return null;
	}

	public static final IDOMAttr getAttrByOffset(IStructuredModel model,
			int offset) {
		if (model != null) {
			IndexedRegion node = model.getIndexedRegion(offset);
			if (node instanceof IDOMNode) {
				return getAttrByOffset(node, offset);
			}
		}
		return null;
	}

	private static IDOMAttr getAttrByOffset(IndexedRegion node, int offset) {
		NamedNodeMap attrs = ((IDOMNode) node).getAttributes();
		if (attrs == null) {
			// see https://github.com/angelozerr/eclipse-wtp-webresources/issues/34
			return null;
		}
		for (int i = 0; i < attrs.getLength(); i++) {
			IndexedRegion attRegion = (IndexedRegion) attrs.item(i);
			if (attRegion.contains(offset))
				return (IDOMAttr) attrs.item(i);
		}
		return null;
	}

	public static final IDOMAttr getAttrByOffset(IDocument document, int offset) {
		IDOMNode node = getNodeByOffset(document, offset);
		if (node instanceof Element) {
			return getAttrByOffset(node, offset);
		}
		return null;
	}

	/**
	 * Returns the SSE DOM Node {@link IDOMNode} by offset from the
	 * {@link IDocument} document and null if not found.
	 * 
	 * @param document
	 *            the document.
	 * @param offset
	 *            the offset.
	 * @return
	 */
	public static final IDOMNode getNodeByOffset(IDocument document, int offset) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			return getNodeByOffset(model, offset);
		} finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Returns the SSE DOM Node {@link IDOMNode} by offset from the
	 * {@link IStructuredModel} SSE mode and null if not found.
	 * 
	 * @param model
	 *            the SSE model.
	 * @param offset
	 *            the offset.
	 * @return
	 */
	public static final IDOMNode getNodeByOffset(IStructuredModel model,
			int offset) {
		IndexedRegion node = null;
		if (model != null) {
			node = model.getIndexedRegion(offset);
			if (node instanceof IDOMNode) {
				return (IDOMNode) node;
			}

			if (model != null) {
				int lastOffset = offset;
				node = model.getIndexedRegion(offset);
				while (node == null && lastOffset >= 0) {
					lastOffset--;
					node = model.getIndexedRegion(lastOffset);
				}
			}
		}
		return (IDOMNode) node;
	}

}
