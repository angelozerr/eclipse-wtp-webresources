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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.webresources.core.providers.DefaultURIResolver;
import org.eclipse.wst.html.webresources.internal.ui.wizard.INewFileWizard;
import org.eclipse.wst.html.webresources.internal.ui.wizard.css.NewCSSFileWizard;
import org.eclipse.wst.html.webresources.internal.ui.wizard.js.NewJSFileWizard;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

public class OpenNewWizardFileCompletionProposal implements ICompletionProposal {

	private final IFile file;
	private final IDOMAttr attr;

	public OpenNewWizardFileCompletionProposal(IFile file, IDOMAttr attr) {
		this.file = file;
		this.attr = attr;
	}

	@Override
	public void apply(IDocument document) {
		INewFileWizard wizard = createWizard(attr);
		wizard.init(PlatformUI.getWorkbench(),
				new StructuredSelection(file.getParent()));
		WizardDialog wd = new WizardDialog(getShell(), wizard);
		wd.setTitle(wizard.getWindowTitle());
		wd.open();

		IFile newFile = wizard.getFile();
		if (newFile != null) {
			IPath path = DefaultURIResolver.INSTANCE.resolve(newFile, file);
			attr.setValue(path.toString());
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
		return "TODO";
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public Point getSelection(IDocument arg0) {
		return null;
	}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	private INewFileWizard createWizard(IDOMAttr attr) {
		if ("href".equals(attr.getName())) {
			// link/href
			return new NewCSSFileWizard();
		}
		// script/@src
		return new NewJSFileWizard();
	}

	private Shell getShell() {
		IWorkbench workBench = HTMLUIPlugin.getDefault().getWorkbench();
		IWorkbenchWindow workBenchWindow = workBench == null ? null : workBench
				.getActiveWorkbenchWindow();
		return workBenchWindow == null ? null : workBenchWindow.getShell();
	}
}
