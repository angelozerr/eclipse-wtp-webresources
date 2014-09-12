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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * CSS class name finder.
 *
 */
public class CSSClassNameFinder {

	public static CSSClassNameOrIdRegion findClassName(IDocument document,
			int offset, int startOffset, int endOffset) {

		StringBuilder className = new StringBuilder();

		int start = -2;
		int end = -1;

		try {
			int pos = offset;
			char c;

			while (pos > startOffset) {
				c = document.getChar(pos);
				if (isSeparator(c))
					break;
				className.insert(0, c);
				--pos;
			}
			start = pos;

			pos = offset + 1;
			int length = document.getLength();
			while (pos < endOffset && pos < length) {
				c = document.getChar(pos);
				if (isSeparator(c))
					break;
				className.append(c);
				++pos;
			}
			end = pos;

		} catch (BadLocationException x) {
		}

		if (start >= -1 && end > -1) {
			if (start == offset && end == offset)
				return new CSSClassNameOrIdRegion(offset, 0,
						className.toString(), WebResourcesFinderType.CSS_CLASS_NAME);
			else if (start == offset)
				return new CSSClassNameOrIdRegion(start, end - start,
						className.toString(), WebResourcesFinderType.CSS_CLASS_NAME);
			else
				return new CSSClassNameOrIdRegion(start + 1, end - start - 1,
						className.toString(), WebResourcesFinderType.CSS_CLASS_NAME);
		}

		return null;
	}

	private static boolean isSeparator(char c) {
		return c == ' ' || c == '"' || c == '\'';
	}
}
