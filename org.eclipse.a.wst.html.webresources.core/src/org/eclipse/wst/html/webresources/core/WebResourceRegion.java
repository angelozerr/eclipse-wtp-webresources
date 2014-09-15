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
	private String additionalInfo;

	public WebResourceRegion(int offset, int length, String value,
			WebResourcesFinderType type) {
		super(offset, length);
		this.value = value;
		this.type = type;
	}

	/**
	 * Returns the value of the hovered region.
	 * 
	 * @return the value of the hovered region.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the web resource type finder.
	 * 
	 * @return the web resource type finder.
	 */
	public WebResourcesFinderType getType() {
		return type;
	}

	/**
	 * Returns the additional info of this web resources region and null
	 * otherwise.
	 * 
	 * @return the additional info of this web resources region and null
	 *         otherwise.
	 */
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	/**
	 * Set the additional info of this web resources region.
	 * 
	 * @param additionalInfo
	 */
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

}
