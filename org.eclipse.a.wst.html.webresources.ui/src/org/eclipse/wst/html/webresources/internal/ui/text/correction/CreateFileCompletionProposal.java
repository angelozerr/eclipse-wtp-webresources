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
package org.eclipse.wst.html.webresources.internal.ui.text.correction;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.internal.ui.ImageResource;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIMessages;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIPlugin;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

/**
 * Create CSS, JS file which doesn't exists.
 *
 */
public class CreateFileCompletionProposal implements ICompletionProposal {

	private final IFile file;
	private final IDOMAttr attr;

	public CreateFileCompletionProposal(IFile file, IDOMAttr attr) {
		this.file = file;
		this.attr = attr;
	}

	@Override
	public void apply(IDocument document) {
		String path = attr.getValue();
		IFile newFile = file.getParent().getFile(new Path(path));
		if (!newFile.exists()) {
			try {
				IContainer parent = newFile.getParent();
				while (parent != null && parent.getType() == IResource.FOLDER
						&& !parent.exists()) {
					((IFolder) parent).create(IResource.NONE, true, null);
					parent = parent.getParent();
				}
				newFile.create(new ByteArrayInputStream("".getBytes()), true,
						null);
			} catch (Throwable e) {
				IStatus status = new Status(
						IStatus.ERROR,
						WebResourcesUIPlugin.PLUGIN_ID,
						IStatus.ERROR,
						NLS.bind(
								WebResourcesUIMessages.CreateFileCompletionProposal_errorMessage,
								path), e);
				ErrorDialog
						.openError(
								getShell(),
								WebResourcesUIMessages.CreateFileCompletionProposal_errorTitle,
								NLS.bind(
										WebResourcesUIMessages.CreateFileCompletionProposal_errorMessage,
										path), status);
			}
		}
	}

	@Override
	public String getAdditionalProposalInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public String getDisplayString() {
		return NLS
				.bind(WebResourcesUIMessages.CreateFileCompletionProposal_displayString,
						attr.getValue());
	}

	@Override
	public Image getImage() {
		if (attr.getValue().endsWith(WebResourceType.css.name())) {
			return ImageResource.getImage(ImageResource.IMG_NEW_CSS);
		}
		return ImageResource.getImage(ImageResource.IMG_NEW_HTML);
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CreateFileCompletionProposal) {
			CreateFileCompletionProposal proposal = (CreateFileCompletionProposal) obj;
			return proposal.attr.getValue().equals(this.attr.getValue());
		}
		return false;
	}

	private Shell getShell() {
		IWorkbench workBench = HTMLUIPlugin.getDefault().getWorkbench();
		IWorkbenchWindow workBenchWindow = workBench == null ? null : workBench
				.getActiveWorkbenchWindow();
		return workBenchWindow == null ? null : workBenchWindow.getShell();
	}
}
