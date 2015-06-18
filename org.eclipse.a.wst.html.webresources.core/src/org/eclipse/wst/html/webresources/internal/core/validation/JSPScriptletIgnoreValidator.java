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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.validation.IWebResourcesIgnoreValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

@SuppressWarnings("restriction")
public class JSPScriptletIgnoreValidator implements
		IWebResourcesIgnoreValidator {

	private static final String END_SCRIPTLET = "%>";
	private static final String START_SCRIPLET = "<%";
	private final IContentType jspContentType;

	public JSPScriptletIgnoreValidator() {
		jspContentType = Platform.getContentTypeManager().getContentType(
				"org.eclipse.jst.jsp.core.jspsource");
	}

	@Override
	public boolean shouldIgnore(Object resource, WebResourceKind resourceKind,
			IWebResourcesContext context) {
		if (context != null) {
			IContentType fileContentType = null;

			try {
				fileContentType = context.getHtmlFile().getContentDescription()
						.getContentType();
			} catch (Exception e) {
			}

			if (fileContentType != null
					&& fileContentType.isKindOf(jspContentType)) {
				final IDOMNode node = context.getHtmlNode();

				if (node instanceof IDOMAttr) {
					final IDOMAttr attr = (IDOMAttr) node;
					final String attrName = attr.getName();

					if (attrName != null
							&& (HTML40Namespace.ATTR_NAME_HREF
									.equalsIgnoreCase(attrName)
									|| HTML40Namespace.ATTR_NAME_SRC
											.equalsIgnoreCase(attrName) || HTML40Namespace.ATTR_NAME_CLASS
										.equalsIgnoreCase(attrName))
							|| HTML40Namespace.ATTR_NAME_ID
									.equalsIgnoreCase(attrName)) {
						final String val = attr.getValue();

						if (val != null
								&& (val.contains(START_SCRIPLET) || val
										.contains(END_SCRIPTLET))) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
