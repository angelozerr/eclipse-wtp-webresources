package org.eclipse.wst.htmlcss.internal.ui.hover;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.htmlcss.internal.ui.AbstractCSSClassTraverser;
import org.eclipse.wst.htmlcss.internal.ui.ClassNameRegion;
import org.eclipse.wst.htmlcss.internal.ui.DOMHelper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class HoverCSSClassTraverser extends AbstractCSSClassTraverser {

	private final ClassNameRegion classNameRegion;
	private final StringBuilder info;

	public HoverCSSClassTraverser(IDOMNode node, ClassNameRegion classNameRegion) {
		super(node);
		this.classNameRegion = classNameRegion;
		this.info = new StringBuilder();
	}

	@Override
	protected void addClassName(String className, ICSSStyleRule rule) {
		if (classNameRegion.getName().equals(className)) {
			DOMHelper.addInformation(rule, info);
		}
	}

	public String getInfo() {
		return info.toString();
	}
}
