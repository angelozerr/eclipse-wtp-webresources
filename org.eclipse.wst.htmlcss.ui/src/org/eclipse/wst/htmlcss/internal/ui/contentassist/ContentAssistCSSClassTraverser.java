package org.eclipse.wst.htmlcss.internal.ui.contentassist;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.htmlcss.internal.ui.ImageResource;
import org.eclipse.wst.htmlcss.ui.core.AbstractCSSClassTraverser;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;

public class ContentAssistCSSClassTraverser extends AbstractCSSClassTraverser {

	private final ContentAssistRequest contentAssistRequest;
	private final String matchingClassName;
	private final int pos;

	public ContentAssistCSSClassTraverser(IDOMNode node,
			ContentAssistRequest contentAssistRequest, int pos) {
		super(node);
		this.contentAssistRequest = contentAssistRequest;
		this.matchingClassName = getMatchingClassName(contentAssistRequest);
		this.pos = pos;// - matchingClassName.length();
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

		StringBuilder info = new StringBuilder();
		info.append("<pre>");
		info.append(rule.getCssText());
		info.append("</pre>");
		info.append("<p>");
		info.append(rule.getOwnerDocument().getModel().getBaseLocation());
		info.append("</p>");

		/*contentAssistRequest.addProposal(new CompletionProposal(className, pos,
				className.length(), className.length(), ImageResource
						.getImage(ImageResource.IMG_CLASSNAME), className,
				null, info.toString()));
		*/
		contentAssistRequest.addProposal(new CustomCompletionProposal(
				className, pos,
				/* start pos */
				1, /* replace length */
				className.length(), /*
										 * /* cursor position after (relavtive
										 * to start)
										 */
				ImageResource.getImage(ImageResource.IMG_CLASSNAME), className,
				null, info.toString(), XMLRelevanceConstants.R_TAG_NAME));
	}

}
