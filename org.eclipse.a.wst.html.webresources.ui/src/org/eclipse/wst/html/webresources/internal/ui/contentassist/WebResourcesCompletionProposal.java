package org.eclipse.wst.html.webresources.internal.ui.contentassist;

import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIPlugin;
import org.eclipse.wst.html.webresources.internal.ui.hover.WebResourcesHoverControlCreator;
import org.eclipse.wst.html.webresources.internal.ui.hover.PresenterControlCreator;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;

/**
 * Web Resources completion proposal which supports HTML
 * {@link BrowserInformationControl}.
 *
 */
public class WebResourcesCompletionProposal extends CustomCompletionProposal
		implements ICompletionProposalExtension3 {

	private IInformationControlCreator webResourcesControlCreator;

	public WebResourcesCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation,
			String additionalProposalInfo) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, 0);
	}

	@Override
	public IInformationControlCreator getInformationControlCreator() {
		Shell shell = WebResourcesUIPlugin.getActiveWorkbenchShell();
		if (shell == null || !BrowserInformationControl.isAvailable(shell))
			return null;

		if (webResourcesControlCreator == null) {
			PresenterControlCreator presenterControlCreator = new PresenterControlCreator();
			webResourcesControlCreator = new WebResourcesHoverControlCreator(
					presenterControlCreator, true);
		}
		return webResourcesControlCreator;
	}

	@Override
	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return 1;// getReplacementOffset();
	}

	@Override
	public CharSequence getPrefixCompletionText(IDocument document,
			int completionOffset) {
		return null;
	}

}
