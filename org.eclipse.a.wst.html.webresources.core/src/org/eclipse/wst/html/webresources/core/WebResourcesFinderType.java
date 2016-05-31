/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Gregory Amerson <gregory.amerson@liferay.com> - https://github.com/angelozerr/eclipse-wtp-webresources/issues/14
 */
package org.eclipse.wst.html.webresources.core;

/**
 * Web resources finder type.
 *
 */
public enum WebResourcesFinderType {

	CSS_ID(WebResourceType.css), CSS_CLASS_NAME(WebResourceType.css), SCRIPT_SRC(
			WebResourceType.js), LINK_HREF(WebResourceType.css), IMG_SRC(
			WebResourceType.img);

	private final WebResourceType type;

	private WebResourcesFinderType(WebResourceType type) {
		this.type = type;
	}

	public WebResourceType getType() {
		return type;
	}

	public static WebResourcesFinderType get(String value) {
		WebResourcesFinderType[] types = WebResourcesFinderType.values();
		WebResourcesFinderType type;
		for (int i = 0; i < types.length; i++) {
			type = types[i];
			if (type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		return null;
	}

}
