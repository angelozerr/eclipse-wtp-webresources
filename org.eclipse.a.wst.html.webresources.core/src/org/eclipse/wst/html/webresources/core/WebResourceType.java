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

public enum WebResourceType {

	css, js, img;

	public static WebResourceType get(String value) {
		WebResourceType[] types = WebResourceType.values();
		WebResourceType type;
		for (int i = 0; i < types.length; i++) {
			type = types[i];
			if (type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		return null;
	}
	 
}
