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
package org.eclipse.wst.html.webresources.internal.ui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.core.utils.DOMHelper;
import org.eclipse.wst.html.webresources.core.utils.ResourceHelper;
import org.eclipse.wst.html.webresources.internal.ui.ImageResource;
import org.eclipse.wst.html.webresources.internal.ui.Trace;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIPlugin;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.osgi.framework.Bundle;

/**
 * Provides a set of convenience methods for creating HTML info for web
 * resources content assist and hover.
 *
 */
public class HTMLWebResourcesPrinter {

	/**
	 * Style sheets content.
	 */
	private static String fgStyleSheet;

	// ---------------------------- HTML info for Image

	/**
	 * Returns the information of the given data:image/png;base64.
	 * 
	 * @param data
	 * @return the information of the given data:image/png;base64.
	 */
	public static String getAdditionalProposalInfo(String data) {
		StringBuffer buffer = new StringBuffer();
		ImageDescriptor descriptor = null;
		startPage(buffer, "", descriptor);
		buffer.append("<hr />");
		// resource is an image, display it.
		buffer.append("<img src=\"");
		buffer.append(data);
		buffer.append("\" />");
		long length = 1;
		for (int i = 0; i < length; i++) {
			buffer.append("<p>&nbsp;</p>");
		}
		endPage(buffer);
		return buffer.toString();
	}

	// ---------------------------- HTML info for IResource

	/**
	 * Returns the information of the given file.
	 * 
	 * @param resource
	 * @param type
	 * @return
	 */
	public static String getAdditionalProposalInfo(IResource resource,
			WebResourceType type) {
		StringBuffer buffer = new StringBuffer();
		ImageDescriptor descriptor = ResourceUIHelper
				.getFileTypeImageDescriptor(resource);
		startPage(buffer, getTitle(resource), descriptor);
		buffer.append("<hr />");
		if (type == WebResourceType.img) {
			// resource is an image, display it.
			buffer.append("<img src=\"file:/");
			buffer.append(resource.getLocation().toString());
			buffer.append("\" />");
			// Hack to generate an well height for the browser input control.
			Integer imageHeight = ResourceUIHelper.getImageHeight(resource);
			long length = Math
					.round((double) (imageHeight != null ? imageHeight : 16) / 16);
			for (int i = 0; i < length; i++) {
				buffer.append("<p>&nbsp;</p>");
			}
		}
		endPage(buffer);
		return buffer.toString();
	}

	/**
	 * Returns the HTML title for the given resource.
	 * 
	 * @param resource
	 * @return the HTML title for the given resource.
	 */
	private static String getTitle(IResource resource) {
		StringBuilder title = new StringBuilder("<b>").append(
				resource.getName()).append("</b>");
		String containerName = resource.getParent().getProjectRelativePath()
				.toString();
		if (containerName != null && containerName.length() > 0) {
			title.append(" in ");
			title.append("<b>");
			title.append(containerName);
			title.append("</b>");
		}
		return title.toString();
	}

	// ---------------------------- HTML info for ICSSStyleRule

	public static String getAdditionalProposalInfo(ICSSStyleRule rule,
			WebResourcesFinderType type, IDOMNode node) {
		StringBuffer buffer = new StringBuffer();
		ImageDescriptor descriptor = getImageDescriptor(type);
		startPage(buffer, getTitle(rule, node), descriptor);
		buffer.append("<hr />");
		buffer.append("<pre>");
		buffer.append(rule.getCssText());
		buffer.append("</pre>");
		buffer.append("<p>&nbsp;</p>");
		endPage(buffer);
		return buffer.toString();
	}

	private static String getTitle(ICSSStyleRule rule, IDOMNode node) {
		String fileName = DOMHelper.getFileName(rule, node);
		StringBuilder title = new StringBuilder("<b>").append(
				rule.getSelectorText()).append("</b>");
		if (fileName != null) {
			title.append(" in ");
			title.append("<b>");
			title.append(fileName);
			title.append("</b>");
		}
		return title.toString();
	}

	public static Image getImage(WebResourcesFinderType type) {
		switch (type) {
		case CSS_ID:
			return ImageResource.getImage(ImageResource.IMG_CSS_ID);
		case CSS_CLASS_NAME:
			return ImageResource.getImage(ImageResource.IMG_CSS_CLASSNAME);
		default:
			return null;
		}
	}

	public static ImageDescriptor getImageDescriptor(WebResourcesFinderType type) {
		switch (type) {
		case CSS_ID:
			return ImageResource.getImageDescriptor(ImageResource.IMG_CSS_ID);
		case CSS_CLASS_NAME:
			return ImageResource
					.getImageDescriptor(ImageResource.IMG_CSS_CLASSNAME);
		default:
			return null;
		}
	}

	// ---------------------------- HTML printer utilities

	public static void startPage(StringBuffer buf, String title,
			ImageDescriptor descriptor) {
		int imageWidth = 16;
		int imageHeight = 16;
		int labelLeft = 20;
		int labelTop = 2;

		buf.append("<div style='word-wrap: break-word; position: relative; "); //$NON-NLS-1$

		String imageSrcPath = getImageURL(descriptor);
		if (imageSrcPath != null) {
			buf.append("margin-left: ").append(labelLeft).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("padding-top: ").append(labelTop).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
		}

		buf.append("'>"); //$NON-NLS-1$
		if (imageSrcPath != null) {
			/*
			 * if (element != null) { // try { // String uri = ""; // TODO //
			 * JavaElementLinks.createURI(JavaElementLinks.OPEN_LINK_SCHEME, //
			 * element); //buf.append("<a href='").append(uri).append("'>");
			 * //$NON-NLS-1$//$NON-NLS-2$ /* } catch (URISyntaxException e) {
			 * element= null; // no link }
			 */
			// }
			StringBuffer imageStyle = new StringBuffer(
					"border:none; position: absolute; "); //$NON-NLS-1$
			imageStyle.append("width: ").append(imageWidth).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("height: ").append(imageHeight).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("left: ").append(-labelLeft - 1).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$

			// hack for broken transparent PNG support in IE 6, see
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=223900 :
			buf.append("<!--[if lte IE 6]><![if gte IE 5.5]>\n"); //$NON-NLS-1$
			String tooltip = ""; // TODO element == null ? "" : "alt='" + JavaHoverMessages.JavadocHover_openDeclaration + "' "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buf.append("<span ").append(tooltip).append("style=\"").append(imageStyle). //$NON-NLS-1$ //$NON-NLS-2$
					append("filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='").append(imageSrcPath).append("')\"></span>\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("<![endif]><![endif]-->\n"); //$NON-NLS-1$

			buf.append("<!--[if !IE]>-->\n"); //$NON-NLS-1$
			buf.append("<img ").append(tooltip).append("style='").append(imageStyle).append("' src='").append(imageSrcPath).append("'/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			buf.append("<!--<![endif]-->\n"); //$NON-NLS-1$
			buf.append("<!--[if gte IE 7]>\n"); //$NON-NLS-1$
			buf.append("<img ").append(tooltip).append("style='").append(imageStyle).append("' src='").append(imageSrcPath).append("'/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			buf.append("<![endif]-->\n"); //$NON-NLS-1$
			// if (element != null) {
			//	buf.append("</a>"); //$NON-NLS-1$
			// }
		}
		buf.append(title);

		buf.append("</div>"); //$NON-NLS-1$
	}

	/**
	 * 
	 * @param buffer
	 */
	public static void endPage(StringBuffer buffer) {
		HTMLPrinter.insertPageProlog(buffer, 0,
				HTMLWebResourcesPrinter.getStyleSheet());
		HTMLPrinter.addPageEpilog(buffer);
	}

	/**
	 * Returns the Javadoc hover style sheet with the current Javadoc font from
	 * the preferences.
	 * 
	 * @return the updated style sheet
	 * @since 3.4
	 */
	private static String getStyleSheet() {
		if (fgStyleSheet == null) {
			fgStyleSheet = loadStyleSheet("/WebResourcesStyleSheet.css"); //$NON-NLS-1$
		}
		String css = fgStyleSheet;
		if (css != null) {
			FontData fontData = JFaceResources.getFontRegistry().getFontData(
					JFaceResources.DIALOG_FONT)[0];
			css = HTMLPrinter.convertTopLevelFont(css, fontData);
		}

		return css;
	}

	/**
	 * Loads and returns the style sheet associated with either Javadoc hover or
	 * the view.
	 * 
	 * @param styleSheetName
	 *            the style sheet name of either the Javadoc hover or the view
	 * @return the style sheet, or <code>null</code> if unable to load
	 * @since 3.4
	 */
	private static String loadStyleSheet(String styleSheetName) {
		Bundle bundle = Platform.getBundle(WebResourcesUIPlugin.PLUGIN_ID);
		URL styleSheetURL = bundle.getEntry(styleSheetName);
		if (styleSheetURL == null)
			return null;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					styleSheetURL.openStream()));
			StringBuffer buffer = new StringBuffer(1500);
			String line = reader.readLine();
			while (line != null) {
				buffer.append(line);
				buffer.append('\n');
				line = reader.readLine();
			}

			FontData fontData = JFaceResources.getFontRegistry().getFontData(
					JFaceResources.DIALOG_FONT)[0];
			return HTMLPrinter.convertTopLevelFont(buffer.toString(), fontData);
		} catch (IOException ex) {
			Trace.trace(Trace.SEVERE, "Error while loading style sheets", ex);
			return ""; //$NON-NLS-1$
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	private static String getImageURL(ImageDescriptor descriptor) {
		if (descriptor == null) {
			return null;
		}
		String imageName = null;

		URL imageUrl = ImageResource.getImageURL(descriptor);
		if (imageUrl != null) {
			imageName = imageUrl.toExternalForm();
		}
		return imageName;
	}

}
