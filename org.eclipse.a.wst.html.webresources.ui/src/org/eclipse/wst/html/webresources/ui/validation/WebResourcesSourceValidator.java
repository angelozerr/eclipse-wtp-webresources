package org.eclipse.wst.html.webresources.ui.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.html.webresources.core.utils.DOMHelper;
import org.eclipse.wst.html.webresources.core.validation.WebResourcesValidator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class WebResourcesSourceValidator extends WebResourcesValidator
		implements ISourceValidator {

	private IDocument fDocument;

	@Override
	public void connect(IDocument document) {
		fDocument = document;
	}

	@Override
	public void disconnect(IDocument document) {
		fDocument = null;
	}

	/**
	 * This validate call is for the ISourceValidator partial document
	 * validation approach
	 * 
	 * @param dirtyRegion
	 * @param helper
	 * @param reporter
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator
	 */
	@Override
	public void validate(IRegion dirtyRegion, IValidationContext helper,
			IReporter reporter) {

		if (helper == null || fDocument == null)
			return;

		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}

		IStructuredModel model = StructuredModelManager.getModelManager()
				.getExistingModelForRead(fDocument);
		if (model == null)
			return; // error

		try {

			IDOMDocument document = null;
			if (model instanceof IDOMModel) {
				document = ((IDOMModel) model).getDocument();
			}

			if (document == null /* || !hasHTMLFeature(document) */) {
				// handled in finally clause
				// model.releaseFromRead();
				return; // ignore
			}

			if (fDocument instanceof IStructuredDocument) {
				IFile file = DOMHelper.getFile(model);
				IStructuredDocumentRegion[] regions = ((IStructuredDocument) fDocument)
						.getStructuredDocumentRegions(dirtyRegion.getOffset(),
								dirtyRegion.getLength());
				validateRegions(reporter, model, file, regions);
			}

		} finally {
			releaseModel(model);
		}
	}


}
