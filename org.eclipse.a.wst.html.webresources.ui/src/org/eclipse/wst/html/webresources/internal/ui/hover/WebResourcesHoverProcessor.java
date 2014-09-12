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
package org.eclipse.wst.html.webresources.internal.ui.hover;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.html.webresources.core.InformationHelper;
import org.eclipse.wst.html.webresources.core.WebResourceRegion;
import org.eclipse.wst.html.webresources.core.DOMHelper;
import org.eclipse.wst.html.webresources.core.WebResourcesTextRegion;
import org.eclipse.wst.html.webresources.core.WebResourcesType;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.core.providers.WebResourcesProvidersManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.taginfo.AbstractHoverProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * Hover processor for Web resources inside HTML:
 * 
 * <ul>
 * <li>@class : hover for CSS class name inside @class attribute.</li>
 * <li>@id : hover for CSS ID inside @id attribute.</li>
 * <li>script/@src : hover for JS files inside script/@src attribute.</li>
 * <li>link/@href : hover for CSS files inside link/@href attribute.</li>
 * <li>img/@src : hover for Images files inside img/@src attribute.</li>
 * </ul>
 *
 */
public class WebResourcesHoverProcessor extends AbstractHoverProcessor {

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (hoverRegion instanceof WebResourceRegion) {
			WebResourceRegion resourceRegion = (WebResourceRegion) hoverRegion;
			IDOMNode xmlnode = (IDOMNode) ContentAssistUtils.getNodeAt(
					textViewer, hoverRegion.getOffset());
			switch (resourceRegion.getType()) {
			case CSS_CLASS_NAME:
			case CSS_ID:
				CSSHoverTraverser traverser = new CSSHoverTraverser(xmlnode,
						((WebResourceRegion) hoverRegion));
				traverser.process();
				return traverser.getInfo();
			default:
				final String fileName = resourceRegion.getValue();
				final IFile file = DOMHelper.getFile(xmlnode);
				final StringBuilder info = new StringBuilder();
				WebResourcesType type = resourceRegion.getType().getType();
				WebResourcesProvidersManager.collect(xmlnode, type,
						new IWebResourcesCollector() {

							@Override
							public void add(IResource resource) {
								IPath location = resource.getLocation()
										.makeRelativeTo(
												file.getParent().getLocation());
								if (location.toString().equals(fileName)) {
									InformationHelper.addInformation(resource,
											info);
								}
							}
						});
				return info.length() > 0 ? info.toString() : null;
			}
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
		WebResourcesTextRegion attrValueRegion = DOMHelper.getTextRegion(
				documentRegion, offset);
		if (attrValueRegion != null) {
			switch (attrValueRegion.getType()) {
			case CSS_CLASS_NAME:
			case CSS_ID:
				return DOMHelper.getCSSRegion(attrValueRegion, documentRegion,
						textViewer.getDocument(), offset);
			case SCRIPT_SRC:
			case LINK_HREF:
			case IMG_SRC:
				return DOMHelper.getAttrValueRegion(attrValueRegion,
						documentRegion, textViewer.getDocument(), offset);
			}
		}
		return null;
	}
}
