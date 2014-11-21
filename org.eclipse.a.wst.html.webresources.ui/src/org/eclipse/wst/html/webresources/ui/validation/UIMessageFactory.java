package org.eclipse.wst.html.webresources.ui.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.core.validation.MessageFactory;
import org.eclipse.wst.html.webresources.internal.ui.text.correction.WebResourceQuickFixProcessor;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.AnnotationInfo;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.IncrementalReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

public class UIMessageFactory extends MessageFactory {

	public UIMessageFactory(IProject project, IValidator validator,
			IReporter reporter) {
		super(project, validator, reporter);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addMessage(IMessage message, WebResourcesFinderType type) {

		IReporter reporter = getReporter();
		IQuickAssistProcessor processor = getQuickAssistProcessor(type);
		if (reporter instanceof IncrementalReporter && processor != null) {
			AnnotationInfo info = new AnnotationInfo(message,
					AnnotationInfo.NO_PROBLEM_ID, processor);
			((IncrementalReporter) reporter).addAnnotationInfo(getValidator(),
					info);

		} else {
			super.addMessage(message, type);
		}
	}

	private IQuickAssistProcessor getQuickAssistProcessor(
			WebResourcesFinderType type) {
		switch (type) {
		case LINK_HREF:
		case SCRIPT_SRC:
			return new WebResourceQuickFixProcessor();
		default:
			return null;
		}
	}

}
