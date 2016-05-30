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
package org.eclipse.wst.html.webresources.core.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.html.webresources.core.WebResourceType;

/**
 * Helper for {@link IResource}.
 */
public class ResourceHelper {

	private final static Collection<String> IMG_EXTENSIONS;

	static {
		IMG_EXTENSIONS = new ArrayList<String>();
		IMG_EXTENSIONS.add("bmp");
		IMG_EXTENSIONS.add("gif");
		IMG_EXTENSIONS.add("jif");
		IMG_EXTENSIONS.add("jfif");
		IMG_EXTENSIONS.add("jpg");
		IMG_EXTENSIONS.add("jpeg");
		IMG_EXTENSIONS.add("png");
		IMG_EXTENSIONS.add("tif");
		IMG_EXTENSIONS.add("tiff");
		IMG_EXTENSIONS.add("wbmp");
	}

	private final static Collection<String> CSS_EXTENSIONS;

	static {
		CSS_EXTENSIONS = new ArrayList<String>();
		CSS_EXTENSIONS.add("css");
		CSS_EXTENSIONS.add("scss");
	}

	/**
	 * Returns the web resource type of the given resource and null otherwise.
	 * 
	 * @param resource
	 * @return the web resource type of the given resource and null otherwise.
	 */
	public static WebResourceType getWebResourceType(IResource resource) {
		WebResourceType resourceType = null;
		WebResourceType[] resourceTypes = WebResourceType.values();
		for (int i = 0; i < resourceTypes.length; i++) {
			resourceType = resourceTypes[i];
			if (isMatchingWebResourceType(resource.getName(), resourceType)) {
				return resourceType;
			}
		}
		return null;
	}

	/**
	 * Returns true if the given filename is a web resource and false otherwise.
	 * 
	 * @param filename
	 * @return true if the given filename is a web resource and false otherwise.
	 */
	public static boolean isWebResource(String filename) {
		WebResourceType resourceType = null;
		WebResourceType[] resourceTypes = WebResourceType.values();
		for (int i = 0; i < resourceTypes.length; i++) {
			resourceType = resourceTypes[i];
			if (isMatchingWebResourceType(filename, resourceType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given file system matches the web resource type and
	 * false otherwise.
	 * 
	 * @param file
	 *            the file system.
	 * @param resourceType
	 *            the web resource type.
	 * @return true if the given file system matches the web resource type and
	 *         false otherwise.
	 */
	public static boolean isMatchingWebResourceType(File file,
			WebResourceType resourceType) {
		String filename = file.getName();
		return isMatchingWebResourceType(filename, resourceType);
	}

	/**
	 * Returns true if the given file name matches the web resource type and
	 * false otherwise.
	 * 
	 * @param filename
	 *            the file name.
	 * @param resourceType
	 *            the web resource type.
	 * @return true if the given file name matches the web resource type and
	 *         false otherwise.
	 */
	public static boolean isMatchingWebResourceType(String filename,
			WebResourceType resourceType) {
		int index = filename.lastIndexOf('.');
		if (index == -1) {
			return false;
		}
		String extension = filename.substring(index + 1, filename.length());
		return isMatching(extension, resourceType);
	}

	private static boolean isMatching(String extension,
			WebResourceType resourceType) {
		if (extension == null) {
			return false;
		}
		switch (resourceType) {
		case css:
			return CSS_EXTENSIONS.contains(extension.toLowerCase());
		case js:
			return resourceType.name().equalsIgnoreCase(extension);
		case img:
			return IMG_EXTENSIONS.contains(extension.toLowerCase());
		default:
			break;
		}
		return false;
	}

}
