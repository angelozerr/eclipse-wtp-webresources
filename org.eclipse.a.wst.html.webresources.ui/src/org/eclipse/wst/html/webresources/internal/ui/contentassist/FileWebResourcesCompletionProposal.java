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
package org.eclipse.wst.html.webresources.internal.ui.contentassist;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.internal.ui.utils.HTMLWebResourcesPrinter;

/**
 * File Web Resources completion proposal.
 *
 */
public class FileWebResourcesCompletionProposal extends
		WebResourcesCompletionProposal {

	private final IResource resource;
	private final WebResourceType resourceType;

	public FileWebResourcesCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation, IResource resource,
			WebResourceType resourceType) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation, null);
		this.resource = resource;
		this.resourceType = resourceType;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return HTMLWebResourcesPrinter.getAdditionalProposalInfo(resource,
				resourceType);

	}

}
