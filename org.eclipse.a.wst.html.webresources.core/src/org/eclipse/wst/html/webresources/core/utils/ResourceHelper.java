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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.internal.core.Trace;

/**
 * Helper for {@link IResource}.
 */
public class ResourceHelper {

	private static final ResourceLabelProvider LABEL_PROVIDER = new ResourceLabelProvider();

	static class ResourceLabelProvider extends WorkbenchLabelProvider {

		public ImageDescriptor getImageDescriptor(IResource resource) {
			IWorkbenchAdapter adapter = super.getAdapter(resource);
			if (adapter == null) {
				return null;
			}
			return adapter.getImageDescriptor(resource);

		}
	};

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
	 * Returns the image file type of the given file.
	 * 
	 * @param resource
	 * @return
	 */
	public static Image getFileTypeImage(IResource resource) {
		return LABEL_PROVIDER.getImage(resource);
	}

	/**
	 * Returns the image descriptor file type of the given file.
	 * 
	 * @param resource
	 * @return
	 */
	public static ImageDescriptor getFileTypeImageDescriptor(IResource resource) {
		return LABEL_PROVIDER.getImageDescriptor(resource);
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

	/**
	 * Returns the image height of the given image resource and null if the
	 * height cannot be retrieved.
	 * 
	 * @param resource
	 * @return the image height of the given image resource and null if the
	 *         height cannot be retrieved.
	 */
	public static Integer getImageHeight(IResource resource) {
		String filename = null;
		try {
			filename = resource.getLocation().toOSString();
			ImageData data = new ImageData(filename);
			return data.height;
		} catch (Throwable e) {
			Trace.trace(Trace.INFO, "Error while getting image height of "
					+ filename, e);
		}
		return null;
	}
}