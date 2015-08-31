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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * URI Helper.
 *
 */
public class URIHelper {

	private static final String DATA_URI_SCHEME = "data:";

	private static final String HTTP = "http";

	private static final String DOUBLE_SLASH = "//";

	/**
	 * Returns true if the given uri is a data URI scheme and false otherwise.
	 * 
	 * @param uri
	 * @return true if the given uri is a data URI scheme and false otherwise.
	 */
	public static boolean isDataURIScheme(String uri) {
		return uri != null && uri.startsWith(DATA_URI_SCHEME);
	}

	/**
	 * Returns true if the given uri starts with http or with //
	 * 
	 * @param url
	 * @return true if the given uri starts with http or with //
	 */
	public static boolean isExternalURL(String url) {
		return url != null
				&& (url.startsWith(HTTP) || url.startsWith(DOUBLE_SLASH));
	}

	/**
	 * Returns true of the given url can be connected an dfalse otherwise.
	 * 
	 * @param url
	 * @return true of the given url can be connected an dfalse otherwise.
	 */
	public static boolean validateExternalURL(String url) {
		try {
			if (url.startsWith(DOUBLE_SLASH)) {
				url = HTTP + ":" + url;
			}
			URLConnection conn = new URL(url).openConnection();
			conn.connect();
			if (conn instanceof HttpURLConnection) {
				int code = ((HttpURLConnection) conn).getResponseCode();
				return code != HttpURLConnection.HTTP_NOT_FOUND;
			}
		} catch (MalformedURLException e) {
			// the URL is not in a valid form
			return false;
		} catch (IOException e) {
			// the connection couldn't be established
			return false;
		}
		return true;
	}
}
