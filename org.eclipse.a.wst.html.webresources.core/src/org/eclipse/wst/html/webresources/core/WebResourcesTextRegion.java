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

import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * Bean info which hosts {@link ITextRegion} and web resources type
 * {@link WebResourcesFinderType}.
 *
 */
public class WebResourcesTextRegion {

	private final ITextRegion region;
	private final WebResourcesFinderType type;

	public WebResourcesTextRegion(ITextRegion region, WebResourcesFinderType type) {
		this.region = region;
		this.type = type;
	}

	public ITextRegion getRegion() {
		return region;
	}

	public WebResourcesFinderType getType() {
		return type;
	}
}
