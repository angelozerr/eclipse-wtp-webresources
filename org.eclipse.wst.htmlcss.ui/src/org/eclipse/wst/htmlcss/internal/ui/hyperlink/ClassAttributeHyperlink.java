package org.eclipse.wst.htmlcss.internal.ui.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.htmlcss.internal.ui.DOMHelper;
import org.eclipse.wst.htmlcss.internal.ui.EditorUtils;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;

public class ClassAttributeHyperlink implements IHyperlink {

	private final IRegion classNameRegion;
	private final ICSSStyleRule rule;

	public ClassAttributeHyperlink(IRegion classNameRegion, ICSSStyleRule rule) {
		this.classNameRegion = classNameRegion;
		this.rule = rule;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return classNameRegion;
	}

	@Override
	public String getHyperlinkText() {
		return rule.getSelectorText();
	}

	@Override
	public String getTypeLabel() {
		return rule.getSelectorText();
	}

	@Override
	public void open() {
		IFile file = DOMHelper.getFile(rule);
		if (file.exists()) {
			int start = ((IndexedRegion) rule).getStartOffset();
			int length = ((IndexedRegion) rule).getEndOffset() - start;
			EditorUtils.openInEditor(file, start, length, true);
		}
	}
}
