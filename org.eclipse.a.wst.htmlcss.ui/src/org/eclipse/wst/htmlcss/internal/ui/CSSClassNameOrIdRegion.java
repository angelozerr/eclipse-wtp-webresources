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
package org.eclipse.wst.htmlcss.internal.ui;

import org.eclipse.jface.text.Region;

/**
 * {@link Region} implementation for CSS clas name or ID.
 *
 */
public class CSSClassNameOrIdRegion extends Region {

	private final String nameOrId;
	private final WebResourcesType type;

	public CSSClassNameOrIdRegion(int offset, int length, String nameOrId,
			WebResourcesType type) {
		super(offset, length);
		this.nameOrId = nameOrId;
		this.type = type;
	}

	public String getNameOrId() {
		return nameOrId;
	}

	public WebResourcesType getType() {
		return type;
	}

}
