/**
 *  Copyright (c) 2014, 2015 Liferay, Inc. and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      Gregory Amerson <gregory.amerson@liferay.com> - initial API and implementation
 *      Kaloyan Raev <kaloyan.r@zend.com> - adapted for PHP scripts
 */
package org.eclipse.wst.html.webresources.internal.core.validation;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.validation.IWebResourcesIgnoreValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;


@SuppressWarnings( "restriction" )
public class PHPScriptIgnoreValidator implements IWebResourcesIgnoreValidator {

	private final IContentType htmlContentType;
	private final IContentType phpContentType;

	public PHPScriptIgnoreValidator() {
		htmlContentType = Platform.getContentTypeManager().getContentType("org.eclipse.wst.html.core.htmlsource");
		phpContentType = Platform.getContentTypeManager().getContentType("org.eclipse.php.core.phpsource");
	}

	@Override
	public boolean shouldIgnore(Object resource, WebResourceKind resourceKind, IWebResourcesContext context) {
		if (context != null) {
			IContentType fileContentType = null;

			try {
				fileContentType = context.getHtmlFile().getContentDescription().getContentType();
			}
			catch( Exception e ) { }

			if (fileContentType != null && 
					(fileContentType.isKindOf(phpContentType) || fileContentType.isKindOf(htmlContentType))) {
				final IDOMNode node = context.getHtmlNode();

				if (node instanceof IDOMAttr) {
					final IDOMAttr attr = (IDOMAttr) node;
					final String attrName = attr.getName();

					if (attrName != null &&
						(attrName.equalsIgnoreCase("href") || attrName.equalsIgnoreCase("src"))) {
						final String val = attr.getValue();

						if (val != null && (val.contains("<?php") || val.contains("?>"))) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

}
