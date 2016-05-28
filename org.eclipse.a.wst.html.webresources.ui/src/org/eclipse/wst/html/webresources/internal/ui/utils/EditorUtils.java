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

import java.io.File;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIPlugin;

/**
 * Editor utilities.
 *
 */
public class EditorUtils {

	public static IEditorPart openInEditor(IFile file, int start, int length,
			boolean activate) {
		IEditorPart editor = null;
		IWorkbenchPage page = WebResourcesUIPlugin.getActivePage();
		try {
			if (start >= 0) {
				editor = IDE.openEditor(page, file, activate);
				ITextEditor textEditor = null;
				if (editor instanceof ITextEditor)
					textEditor = (ITextEditor) editor;
				else if (editor instanceof IAdaptable)
					textEditor = (ITextEditor) editor
							.getAdapter(ITextEditor.class);
				if (textEditor != null) {
					IDocument document = textEditor.getDocumentProvider()
							.getDocument(editor.getEditorInput());
					textEditor.selectAndReveal(start, length);
					page.activate(editor);
				} else {
					IMarker marker = file
							.createMarker("org.eclipse.core.resources.textmarker");
					marker.setAttribute("lineNumber", start);
					editor = IDE.openEditor(page, marker, activate);
					marker.delete();
				}
			} else {
				editor = IDE.openEditor(page, file, activate);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return editor;
	}

	public static IEditorPart openInEditor(File file, int start, int length) {
		IEditorPart editor = null;
		IWorkbenchPage page = WebResourcesUIPlugin.getActivePage();
		try {
			if (start >= 0) {
				IFileStore fileStore = EFS.getStore(file.toURI());
				editor = IDE.openEditorOnFileStore(page, fileStore);
				ITextEditor textEditor = null;
				if (editor instanceof ITextEditor)
					textEditor = (ITextEditor) editor;
				else if (editor instanceof IAdaptable)
					textEditor = (ITextEditor) editor
							.getAdapter(ITextEditor.class);
				if (textEditor != null) {
					IDocument document = textEditor.getDocumentProvider()
							.getDocument(editor.getEditorInput());
					textEditor.selectAndReveal(start, length);
					page.activate(editor);
				}
			} else {
				IFileStore fileStore = EFS.getStore(file.toURI());
				editor = IDE.openEditorOnFileStore(page, fileStore);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return editor;
	}

	public static IFile getFile(IDocument document) {
		ITextFileBufferManager bufferManager = FileBuffers
				.getTextFileBufferManager(); // get the buffer manager
		IPath location = bufferManager.getTextFileBuffer(document)
				.getLocation();
		return ResourcesPlugin.getWorkspace().getRoot().getFile(location);
	}
}
