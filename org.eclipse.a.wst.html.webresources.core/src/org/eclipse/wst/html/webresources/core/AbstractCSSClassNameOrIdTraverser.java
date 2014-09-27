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
package org.eclipse.wst.html.webresources.core;

import java.io.File;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetListAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSMediaRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSimpleSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.eclipse.wst.css.core.internal.util.AbstractCssTraverser;
import org.eclipse.wst.html.core.internal.htmlcss.HTMLDocumentAdapter;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.providers.WebResourcesProvidersManager;
import org.eclipse.wst.html.webresources.core.utils.DOMHelper;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.stylesheets.StyleSheetList;

/**
 * CSS class name or ID traverser.
 */
public abstract class AbstractCSSClassNameOrIdTraverser extends
		AbstractCssTraverser implements IWebResourcesCollector {

	private final IDOMNode node;
	private final WebResourcesFinderType webResourcesType;

	public AbstractCSSClassNameOrIdTraverser(IDOMNode node,
			WebResourcesFinderType webResourcesType) {
		this.node = node;
		this.webResourcesType = webResourcesType;
		super.setTraverseImported(true);
	}

	public void process() {
		HTMLDocumentAdapter adapter = (HTMLDocumentAdapter) ((INodeNotifier) node
				.getOwnerDocument())
				.getAdapterFor(IStyleSheetListAdapter.class);
		ICSSNode cssNode = null;
		StyleSheetList sheetList = adapter.getStyleSheets();
		int nbSheets = sheetList.getLength();
		// Loop for each CSS styles sheets :
		// - embedded styles declared with <style> element
		// - external styles declared with <link href=""
		for (int i = 0; i < nbSheets; i++) {
			org.w3c.dom.stylesheets.StyleSheet sheet = sheetList.item(i);
			if (sheet instanceof ICSSNode) {
				cssNode = (ICSSNode) sheet;
				super.apply(cssNode);
			}
		}
		// to discover styles from the project.
		WebResourcesProvidersManager.getInstance().collect(node,
					WebResourceType.css, this);
	}

	private void traverseRule(ICSSStyleRule rule) {
		ICSSSelectorList selectorList = rule.getSelectors();
		Iterator iSelector = selectorList.getIterator();
		while (iSelector.hasNext()) {
			ICSSSelector selector = (ICSSSelector) iSelector.next();
			Iterator iItem = selector.getIterator();
			while (iItem.hasNext()) {
				ICSSSelectorItem item = (ICSSSelectorItem) iItem.next();
				if (item.getItemType() == ICSSSelectorItem.SIMPLE) {
					ICSSSimpleSelector sel = (ICSSSimpleSelector) item;
					// vist CSS#class names
					if (webResourcesType == WebResourcesFinderType.CSS_CLASS_NAME) {
						int nClasses = sel.getNumOfClasses();
						for (int iClass = 0; iClass < nClasses; iClass++) {
							String className = sel.getClass(iClass);
							collect(className, rule);
						}
					}
					// visit CSS#id
					if (webResourcesType == WebResourcesFinderType.CSS_ID) {
						int nbIds = sel.getNumOfIDs();
						for (int i = 0; i < nbIds; i++) {
							String cssID = sel.getID(i);
							collect(cssID, rule);
						}
					}
				}
			}
		}
	}

	protected abstract void collect(String classNameOrId, ICSSStyleRule rule);

	/**
	 * 
	 */
	protected void begin(ICSSNode node) {

	}

	/**
	 * 
	 */
	protected void end(ICSSNode node) {
	}

	/**
	 * 
	 */
	protected short postNode(ICSSNode node) {
		return TRAV_CONT;
	}

	/**
	 * s
	 * 
	 */
	protected short preNode(ICSSNode node) {
		short ret;
		if (node instanceof ICSSStyleRule) {
			traverseRule((ICSSStyleRule) node);
			ret = TRAV_PRUNE;
		} else if (node instanceof ICSSStyleSheet
				|| node instanceof ICSSMediaRule
				|| node instanceof ICSSImportRule) {
			ret = TRAV_CONT;
		} else {
			ret = TRAV_PRUNE;
		}
		return ret;
	}

	public IDOMNode getNode() {
		return node;
	}

	public WebResourcesFinderType getWebResourcesType() {
		return webResourcesType;
	}

	@Override
	public void add(Object resource, WebResourceKind resourceKind,
			IDOMNode htmlNode, IFile htmlFile, IURIResolver resolver) {
		ICSSModel model = getModel(resource, resourceKind);
		if (model != null) {
			super.apply(model);
		}
	}

	private ICSSModel getModel(Object resource, WebResourceKind resourceKind) {
		switch (resourceKind) {
		case ECLIPSE_RESOURCE:
			return DOMHelper.getModel((IFile) resource);
		case FILESYSTEM:
			return DOMHelper.getModel((File) resource);
		}
		return null;
	}

	@Override
	public void startCollect(WebResourceType resourcesType) {

	}

	@Override
	public void endCollect(WebResourceType resourcesType) {

	}
}
