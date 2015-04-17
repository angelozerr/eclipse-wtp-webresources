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
package org.eclipse.wst.html.webresources.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.html.webresources.core.WebRootFolders;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIMessages;
import org.eclipse.wst.html.webresources.internal.ui.WebResourcesUIPlugin;

/**
 * Property page for Web Root folders.
 */
public class WebResourcesRootFoldersPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {
	
	private CheckboxTreeViewer tree;

	public WebResourcesRootFoldersPropertyPage() {
		setDescription(WebResourcesUIMessages.WebRootFoldersPropertyPage_description);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		composite.setLayout(layout);
		
		Label label = new Label(composite, SWT.NONE);
		label.setText(WebResourcesUIMessages.WebRootFoldersPropertyPage_select);
		
		tree = new CheckboxTreeViewer(composite, SWT.BORDER);
		tree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.setContentProvider(new ITreeContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				return new Object[] { getProject() };
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				IContainer container = (IContainer) parentElement;
				List<IResource> children = new ArrayList<IResource>();
				
				if (container.isAccessible()) {
					try {
						for (IResource resource : container.members()) {
							if (resource instanceof IContainer) {
								children.add(resource);
							}
						}
					} catch (CoreException e) {
						WebResourcesUIPlugin.log(e);
					}
				}
				
				return children.toArray();
			}
			
			@Override
			public Object getParent(Object element) {
				return ((IResource) element).getParent();
			}
			
			@Override
			public boolean hasChildren(Object element) {
				IContainer container = (IContainer) element;
				
				if (container.isAccessible()) {
					try {
						for (IResource resource : container.members()) {
							if (resource instanceof IContainer) {
								return true;
							}
						}
					} catch (CoreException e) {
						WebResourcesUIPlugin.log(e);
					}
				}
				
				return false;
			}
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
			@Override
			public void dispose() {
			}
		});
		tree.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IResource) element).getName();
			}

			@Override
			public Image getImage(Object element) {
				String id;
				
				if (element instanceof IProject) {
					IProject project = ((IProject) element);
					if (project.isOpen()) {
						id = IDE.SharedImages.IMG_OBJ_PROJECT;
					} else {
						id = IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED;
					}
				} else {
					id = ISharedImages.IMG_OBJ_FOLDER;
				}
				
				return PlatformUI.getWorkbench().getSharedImages().getImage(id);
			}
		});
		tree.setAutoExpandLevel(2);
		tree.setInput(new Object[0]);
		
		String[] roots = WebRootFolders.getWebRootFolders(getProject());
		for (String root : roots) {
			IResource resource = getProject().findMember(root);
			tree.setChecked(resource, true);
			tree.reveal(resource);
		}
		
		return composite;
	}
	
	private IProject getProject() {
		return (IProject) getElement().getAdapter(IProject.class);
	}

	@Override
	public boolean performOk() {
		List<String> roots = new ArrayList<String>();
		
		for (Object element : tree.getCheckedElements()) {
			IPath path = ((IResource) element).getFullPath().removeFirstSegments(1);
			roots.add('/' + path.toString());
		}
		
		WebRootFolders.setWebRootFolders(getProject(), roots.toArray(new String[roots.size()]));
		
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		tree.setSubtreeChecked(getProject(), false);
		
		super.performDefaults();
	}
	
}
