/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.htmlcss.internal.ui;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class ClassNameFinder {

	public static ClassNameRegion findName(IDocument document, int offset,
			int startOffset, int endOffset) {

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
				return new ClassNameRegion(offset, 0, className.toString());
			else if (start == offset)
				return new ClassNameRegion(start, end - start,
						className.toString());
			else
				return new ClassNameRegion(start + 1, end - start - 1,
						className.toString());
		}

		return null;
	}

	private static boolean isSeparator(char c) {
		return c == ' ' || c == '"' || c == '\'';
	}
}
