package org.eclipse.wst.html.webresources.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class InformationHelper {

	private static final WorkbenchLabelProvider LABEL_PROVIDER = new WorkbenchLabelProvider();

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
		String fileName = DOMHelper.getFileName(rule, node);
		if (fileName != null) {
			information.append("<dt><b>File:</b></dt>");
			information.append("<dd>");
			information.append(fileName);
			information.append("</dd>");
		}
		information.append("</dl>");
	}

	public static String getInformation(IResource file) {
		StringBuilder information = new StringBuilder();
		addInformation(file, information);
		return information.toString();
	}

	public static void addInformation(IResource file, StringBuilder information) {
		information.append("<dl>");
		String fileName = file.getName();
		if (fileName != null) {
			information.append("<dt><b>File:</b></dt>");
			information.append("<dd>");
			information.append(fileName);
			information.append("</dd>");
		}
		information.append("</dl>");
	}

	public static Image getImage(IResource resource) {
		return LABEL_PROVIDER.getImage(resource);
	}

}
