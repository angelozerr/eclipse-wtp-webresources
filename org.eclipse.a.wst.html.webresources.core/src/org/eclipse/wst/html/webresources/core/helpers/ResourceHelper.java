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
package org.eclipse.wst.html.webresources.core.helpers;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.html.webresources.core.WebResourceType;

/**
 * Helper for {@link IResource}.
 */
public class ResourceHelper {

	private static final WorkbenchLabelProvider LABEL_PROVIDER = new WorkbenchLabelProvider();

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

	/**
	 * Returns the information of the given file.
	 * 
	 * @param file
	 * @return the information of the given file.
	 */
	public static String getInformation(IResource file) {
		StringBuilder information = new StringBuilder();
		addInformation(file, information);
		return information.toString();
	}

	/**
	 * Add information of the given file in the given buffer.
	 * 
	 * @param file
	 * @param information
	 */
	public static void addInformation(IResource file, StringBuilder information) {
		information.append("<dl>");
		String fileName = file.getName();
		if (fileName != null) {
			information.append("<dt><b>File:</b></dt>");
			information.append("<dd>");
			information.append(fileName);
			information.append("</dd>");
		}
		information.append("</dl>");
	}

	/**
	 * Returns the image file type of the given file.
	 * 
	 * @param resource
	 * @return
	 */
	public static Image getFileTypeImage(IResource resource) {
		return LABEL_PROVIDER.getImage(resource);
	}

	/**
	 * Returns true if the given resources matches the web resource type and
	 * false otherwise.
	 * 
	 * @param resource
	 *            the file.
	 * @param type
	 *            teh web resource type.
	 * @return true if the given resources matches the web resource type and
	 *         false otherwise.
	 */
	public static boolean isMatchingWebResourceType(IResource resource,
			WebResourceType type) {
		String extension = resource.getFileExtension();
		if (extension == null) {
			return false;
		}
		switch (type) {
		case css:
		case js:
			return type.name().equalsIgnoreCase(extension);
		case img:
			return IMG_EXTENSIONS.contains(extension.toLowerCase());
		}
		return false;
	}
}
