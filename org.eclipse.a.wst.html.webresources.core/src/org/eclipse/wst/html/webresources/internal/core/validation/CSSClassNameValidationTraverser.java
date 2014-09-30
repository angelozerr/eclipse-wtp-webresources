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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.html.webresources.core.AbstractCSSClassNameOrIdTraverser;
import org.eclipse.wst.html.webresources.core.WebResourceRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

/**
 * CSS traverser implementation for CSS class name and CSS id validation.
 */
public class CSSClassNameValidationTraverser extends
		AbstractCSSClassNameOrIdTraverser {

	private class CSSClassInfo {

		private final String className;
		private final int startIndex;

		public CSSClassInfo(String className, int startIndex) {
			this.className = className;
			this.startIndex = startIndex;
		}

	}

	private final IFile file;
	private final WebResourceRegion cssRegion;
	private final MessageFactory factory;
	private final Map<String, List<CSSClassInfo>> cssClassToValidate;

	public CSSClassNameValidationTraverser(IDOMAttr node, IFile file,
			WebResourceRegion cssRegion, MessageFactory factory) {
		super(node, cssRegion.getType());
		this.cssRegion = cssRegion;
		this.factory = factory;
		this.file = file;

		// Collect the classname to validate
		cssClassToValidate = new HashMap<String, List<CSSClassInfo>>();
		int startIndex = node.getValueRegionStartOffset() + 1;
		String className = null;
		String[] classNames = node.getValue().split(" ");
		for (int i = 0; i < classNames.length; i++) {
			className = classNames[i];
			if (className.length() == 0) {
				startIndex++;
			} else {
				if (i > 0 && i < classNames.length) {
					startIndex++;
				}
				List<CSSClassInfo> infos = cssClassToValidate.get(className
						.trim());
				if (infos == null) {
					infos = new ArrayList<CSSClassNameValidationTraverser.CSSClassInfo>();
					cssClassToValidate.put(className.trim(), infos);
				}
				infos.add(new CSSClassInfo(className.trim(), startIndex));
				startIndex = startIndex + className.length();
			}
		}
	}

	@Override
	protected boolean collect(String className, ICSSStyleRule rule) {
		List<CSSClassInfo> infos = cssClassToValidate.get(className);
		if (infos != null) {
			cssClassToValidate.remove(className);
		}
		if (cssClassToValidate.size() == 0) {
			return true;
		}

		return false;
	}

	@Override
	public void process() {
		super.process();
		Collection<List<CSSClassInfo>> allInfos = cssClassToValidate.values();
		for (List<CSSClassInfo> infos : allInfos) {
			for (CSSClassInfo classInfo : infos) {
				factory.addMessage((IDOMAttr) getNode(), classInfo.startIndex,
						classInfo.className, cssRegion.getType(), file);
			}
		}

	}
}
