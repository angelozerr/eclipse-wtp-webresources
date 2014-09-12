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

import org.eclipse.jface.text.Region;

/**
 * {@link Region} implementation for CSS clas name or ID.
 *
 */
public class WebResourceRegion extends Region {

	private final String value;
	private final WebResourcesFinderType type;

	public WebResourceRegion(int offset, int length, String value,
			WebResourcesFinderType type) {
		super(offset, length);
		this.value = value;
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public WebResourcesFinderType getType() {
		return type;
	}

}
