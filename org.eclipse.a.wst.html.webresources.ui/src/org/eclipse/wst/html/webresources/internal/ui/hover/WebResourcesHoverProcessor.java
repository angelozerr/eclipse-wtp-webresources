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

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.html.webresources.core.WebResourceRegion;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.WebResourcesTextRegion;
import org.eclipse.wst.html.webresources.core.providers.WebResourcesProvidersManager;
import org.eclipse.wst.html.webresources.core.utils.DOMHelper;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
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
public class WebResourcesHoverProcessor extends AbstractHoverProcessor
		implements ITextHoverExtension2 {

	private IInformationControlCreator fHoverControlCreator;
	private IInformationControlCreator fPresenterControlCreator;

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		WebResourcesBrowserInformationControlInput info = (WebResourcesBrowserInformationControlInput) getHoverInfo2(
				textViewer, hoverRegion);
		return info != null ? info.getHtml() : null;
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		if (hoverRegion instanceof WebResourceRegion) {
			String html = ((WebResourceRegion) hoverRegion).getAdditionalInfo();
			return new WebResourcesBrowserInformationControlInput(null, html,
					100);
		}
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if ((textViewer == null) || (textViewer.getDocument() == null)) {
			return null;
		}
		WebResourceRegion resourceRegion = getWebResourceRegion(textViewer,
				offset);
		if (resourceRegion != null) {
			String additionalInfo = getAdditionalInfo(textViewer,
					resourceRegion);
			if (additionalInfo != null) {
				resourceRegion.setAdditionalInfo(additionalInfo);
				return resourceRegion;
			}
		}
		return null;
	}

	public WebResourceRegion getWebResourceRegion(ITextViewer textViewer,
			int offset) {
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

	private String getAdditionalInfo(ITextViewer textViewer,
			WebResourceRegion resourceRegion) {
		IDOMNode xmlnode = (IDOMNode) ContentAssistUtils.getNodeAt(textViewer,
				resourceRegion.getOffset());
		switch (resourceRegion.getType()) {
		case CSS_CLASS_NAME:
		case CSS_ID:
			return getCSSHoverInfo(resourceRegion, xmlnode);
		default:
			return getFileHoverInfo(resourceRegion, xmlnode);
		}
	}

	private String getCSSHoverInfo(WebResourceRegion hoverRegion,
			IDOMNode xmlnode) {
		CSSHoverTraverser traverser = new CSSHoverTraverser(xmlnode,
				hoverRegion);
		traverser.process();
		return traverser.getInfo();
	}

	private String getFileHoverInfo(WebResourceRegion resourceRegion,
			IDOMNode xmlnode) {
		final String fileName = resourceRegion.getValue();
		WebResourceType type = resourceRegion.getType().getType();
		WebResourcesCollectorForHover collector = new WebResourcesCollectorForHover(
				fileName, type);
		WebResourcesProvidersManager.getInstance().collect(xmlnode, type, null,
				collector);
		return collector.getInfo();
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null)
			fHoverControlCreator = new WebResourcesHoverControlCreator(
					getInformationPresenterControlCreator());
		return fHoverControlCreator;
	}

	// @Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null)
			fPresenterControlCreator = new PresenterControlCreator();
		return fPresenterControlCreator;
	}
}
