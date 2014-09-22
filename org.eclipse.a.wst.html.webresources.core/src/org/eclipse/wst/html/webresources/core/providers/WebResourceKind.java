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
package org.eclipse.wst.html.webresources.core.providers;

import java.io.File;

import org.eclipse.core.resources.IResource;

/**
 * Web resource kind :
 * 
 * <ul>
 * <li>{@link #WebResourceKind#ECLIPSE_RESOURCE} : eclipse {@link IResource}. *
 * <li>{@link #WebResourceKind#FILESYSTEM} : file system {@link File}.
 * </ul>
 *
 */
public enum WebResourceKind {

	ECLIPSE_RESOURCE, FILESYSTEM;
}
