package org.eclipse.wst.htmlcss.internal.ui.hyperlink;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.htmlcss.internal.ui.ClassNameRegion;
import org.eclipse.wst.htmlcss.ui.core.AbstractCSSClassTraverser;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class HyperlinkCSSClassTraverser extends AbstractCSSClassTraverser {

	private static final IHyperlink[] EMPTY_HYPERLINK = new IHyperlink[0];
	private List<IHyperlink> hyperlinks;
	private final ClassNameRegion classNameRegion;

	public HyperlinkCSSClassTraverser(IDOMNode node,
			ClassNameRegion classNameRegion) {
		super(node);
		this.classNameRegion = classNameRegion;
	}

	@Override
	protected void addClassName(String className, ICSSStyleRule rule) {
		if (classNameRegion.getName().equals(className)) {
			if (hyperlinks == null) {
				hyperlinks = new ArrayList<IHyperlink>();
			}
			hyperlinks.add(new CSSClassNameHyperlink(classNameRegion, rule));
		}

	}

	public IHyperlink[] getHyperlinks() {
		if (hyperlinks == null) {
			return null;
		}
		return hyperlinks.toArray(EMPTY_HYPERLINK);
	}

}
