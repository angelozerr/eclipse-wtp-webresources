package org.eclipse.wst.htmlcss.internal.ui;

import org.eclipse.jface.text.Region;

public class ClassNameRegion extends Region {

	private final String name;

	public ClassNameRegion(int offset, int length, String name) {
		super(offset, length);
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
