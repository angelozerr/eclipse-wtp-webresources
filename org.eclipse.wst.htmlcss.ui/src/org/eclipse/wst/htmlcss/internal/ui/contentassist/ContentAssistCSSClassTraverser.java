package org.eclipse.wst.htmlcss.internal.ui.contentassist;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.htmlcss.internal.ui.ImageResource;
import org.eclipse.wst.htmlcss.ui.core.AbstractCSSClassTraverser;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;

public class ContentAssistCSSClassTraverser extends AbstractCSSClassTraverser {

	private final ContentAssistRequest contentAssistRequest;
	private String matchingClassName;
	private int replacementOffset;
	private final String attrValue;
	private int replacementLength;

	public ContentAssistCSSClassTraverser(IDOMNode node,
			ContentAssistRequest contentAssistRequest, int pos, String attrValue) {
		super(node);
		this.contentAssistRequest = contentAssistRequest;

		matchingClassName = contentAssistRequest.getMatchString();
		if (matchingClassName.startsWith("\"")) {
			matchingClassName = matchingClassName.substring(1,
					matchingClassName.length());
		}
		int index = matchingClassName.lastIndexOf(" ");
		if (index != -1) {
			matchingClassName = matchingClassName.substring(index + 1,
					matchingClassName.length());
		}

		this.replacementOffset = pos - matchingClassName.length();
		this.attrValue = attrValue;
		this.replacementLength = attrValue.length() - 2;
		if (index != -1) {
			// replacementLength += index;
			// replacementOffset -= index;
		}

	}

	private String getMatchingClassName(
			ContentAssistRequest contentAssistRequest) {
		String matchingClassName = contentAssistRequest.getMatchString();
		if (matchingClassName.startsWith("\"")) {
			matchingClassName = matchingClassName.substring(1,
					matchingClassName.length());
		}
		int index = matchingClassName.lastIndexOf(" ");
		if (index != -1) {
			matchingClassName = matchingClassName.substring(index + 1,
					matchingClassName.length());
		}
		return matchingClassName;
	}

	@Override
	protected void addClassName(String className, ICSSStyleRule rule) {
		if (!className.startsWith(matchingClassName)) {
			return;
		}

		String fileName = rule.getOwnerDocument().getModel().getBaseLocation();
		if (IModelManager.UNMANAGED_MODEL.equals(fileName)) {
			fileName = null;
		}

		StringBuilder info = new StringBuilder();
		info.append("<pre>");
		info.append(rule.getCssText());
		info.append("</pre>");
		if (fileName != null) {
			info.append("<p>");
			info.append(fileName);
			info.append("</p>");
		}
		String displayString = fileName != null ? new StringBuilder(className)
				.append(" - ").append(fileName).toString() : className;
		contentAssistRequest.addProposal(new CompletionProposal(className,
				replacementOffset, 0, className.length(), ImageResource
						.getImage(ImageResource.IMG_CLASSNAME), displayString,
				null, info.toString()));

		// /*contentAssistRequest.addProposal(new CustomCompletionProposal(
		// className, pos,
		// /* start pos */
		// 0, /* replace length */
		// className.length(), /*
		// * /* cursor position after (relavtive to
		// * start)
		// */
		// ImageResource.getImage(ImageResource.IMG_CLASSNAME), className,
		// null, info.toString(), XMLRelevanceConstants.R_TAG_NAME));
	}

}
