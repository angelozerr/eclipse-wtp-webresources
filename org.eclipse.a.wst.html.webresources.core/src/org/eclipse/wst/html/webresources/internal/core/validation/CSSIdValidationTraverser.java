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
package org.eclipse.wst.html.webresources.internal.core.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.html.webresources.core.AbstractCSSClassNameOrIdTraverser;
import org.eclipse.wst.html.webresources.core.WebResourceRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

/**
 * CSS traverser implementation for CSS class name and CSS id validation.
 */
public class CSSIdValidationTraverser extends AbstractCSSClassNameOrIdTraverser {

	private final IFile file;
	private final WebResourceRegion cssRegion;
	private final MessageFactory factory;
	private int nbFiles;

	public CSSIdValidationTraverser(IDOMAttr node, IFile file,
			WebResourceRegion cssRegion, MessageFactory factory) {
		super(node, cssRegion.getType());
		this.cssRegion = cssRegion;
		this.factory = factory;
		this.nbFiles = 0;
		this.file = file;
	}

	@Override
	protected boolean collect(String className, ICSSStyleRule rule) {
		if (cssRegion.getValue().equals(className)) {
			nbFiles++;
			return true;
		}
		return false;
	}

	@Override
	public void process() {
		super.process();
		if (nbFiles == 0) {
			factory.addMessage((IDOMAttr) getNode(), cssRegion.getType(), file);
		}
	}
}
