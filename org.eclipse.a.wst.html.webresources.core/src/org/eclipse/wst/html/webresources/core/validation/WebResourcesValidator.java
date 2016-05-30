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
package org.eclipse.wst.html.webresources.core.validation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.html.webresources.core.WebResourceRegion;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.core.WebResourcesTextRegion;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.providers.WebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.WebResourcesProvidersManager;
import org.eclipse.wst.html.webresources.core.utils.DOMHelper;
import org.eclipse.wst.html.webresources.core.utils.URIHelper;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.html.webresources.internal.core.validation.CSSClassNameValidationTraverser;
import org.eclipse.wst.html.webresources.internal.core.validation.CSSIdValidationTraverser;
import org.eclipse.wst.html.webresources.internal.core.validation.LocalizedMessage;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * Web resources validator.
 *
 */
public class WebResourcesValidator extends AbstractValidator implements
		IValidatorJob {

	private static final String ORG_ECLIPSE_WST_HTML_CORE_HTMLSOURCE = "org.eclipse.wst.html.core.htmlsource"; //$NON-NLS-1$

	private IContentType[] fOtherSupportedContentTypes = null;
	private String[] fAdditionalContentTypesIDs = null;
	private IContentType fHTMLContentType;

	public WebResourcesValidator() {
		fHTMLContentType = Platform.getContentTypeManager().getContentType(
				ORG_ECLIPSE_WST_HTML_CORE_HTMLSOURCE);
		// FIXME : create extension point to avoid hard coded that.
		fAdditionalContentTypesIDs = new String[] {
				"org.eclipse.jst.jsp.core.jspsource",
				"org.eclipse.php.core.phpsource" };
	}

	private void validate(IReporter reporter, IFile file, IDOMModel model) {
		if (file == null || model == null)
			return; // error
		IDOMDocument document = model.getDocument();
		if (document == null)
			return; // error

		IStructuredDocumentRegion[] regions = model.getStructuredDocument()
				.getStructuredDocumentRegions();
		validateRegions(reporter, model, file, regions);
	}

	@Override
	public IStatus validateInJob(IValidationContext helper, IReporter reporter)
			throws ValidationException {
		// Exception catching was removed, see
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=123600
		IStatus status = Status.OK_STATUS;
		validate(helper, reporter);
		return status;
	}

	@Override
	public void validate(IValidationContext helper, IReporter reporter) {
		if (helper == null)
			return;
		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}
		String[] deltaArray = helper.getURIs();
		if (deltaArray != null && deltaArray.length > 0) {
			validateDelta(helper, reporter);
		} else {
			validateFull(helper, reporter);
		}
	}

	private void validateFull(IValidationContext helper, IReporter reporter) {
		IProject project = null;
		String[] fileDelta = helper.getURIs();
		if (helper instanceof IWorkbenchContext) {
			IWorkbenchContext wbHelper = (IWorkbenchContext) helper;
			project = wbHelper.getProject();
		} else if (fileDelta.length > 0) {
			// won't work for project validation (b/c nothing in file delta)
			project = getResource(fileDelta[0]).getProject();
		}
		if (project == null)
			return;
		validateContainer(helper, reporter, project);
	}

	private void validateDelta(IValidationContext helper, IReporter reporter) {
		String[] deltaArray = helper.getURIs();
		for (int i = 0; i < deltaArray.length; i++) {
			String delta = deltaArray[i];
			if (delta == null)
				continue;

			if (reporter != null) {
				IMessage message = new LocalizedMessage(IMessage.LOW_SEVERITY,
						delta.substring(1));
				reporter.displaySubtask(this, message);
			}

			IResource resource = getResource(delta);
			if (resource == null || !(resource instanceof IFile))
				continue;
			validateFile(helper, reporter, (IFile) resource, null);
		}
	}

	private void validateContainer(IValidationContext helper,
			IReporter reporter, IContainer container) {
		try {
			IResource[] resourceArray = container.members(false);
			for (int i = 0; i < resourceArray.length; i++) {
				IResource resource = resourceArray[i];
				if (resource == null || reporter.isCancelled())
					continue;
				if (resource instanceof IFile) {
					IMessage message = new LocalizedMessage(
							IMessage.LOW_SEVERITY, resource.getFullPath()
									.toString().substring(1));
					reporter.displaySubtask(this, message);
					validateFile(helper, reporter, (IFile) resource, null);
				} else if (resource instanceof IContainer) {
					validateContainer(helper, reporter, (IContainer) resource);
				}
			}
		} catch (CoreException ex) {
		}
	}

	static boolean shouldValidate(IFile file) {
		IResource resource = file;
		do {
			if (resource.isDerived()
					|| resource.isTeamPrivateMember()
					|| !resource.isAccessible()
					|| (resource.getName().charAt(0) == '.' && resource
							.getType() == IResource.FOLDER)) {
				return false;
			}
			resource = resource.getParent();
		} while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}

	protected void validateFile(IValidationContext helper, IReporter reporter,
			IFile file, ValidationResult result) {
		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}
		if (!shouldValidate(file)) {
			return;
		}
		IDOMModel model = getModel(file.getProject(), file);
		if (model == null)
			return;

		try {
			Collection dependencies = null;
			NodeImpl document = null;
			if (model.getDocument() instanceof NodeImpl) {
				document = (NodeImpl) model.getDocument();
			}
			/*
			 * if (result != null && document != null) { dependencies = new
			 * HashSet();
			 * document.setUserData(HTMLValidationAdapterFactory.DEPENDENCIES,
			 * dependencies, null); }
			 */
			validate(reporter, file, model);
			/*
			 * if (result != null && document != null) {
			 * document.setUserData(HTMLValidationAdapterFactory.DEPENDENCIES,
			 * null, null); result.setDependsOn((IResource[]) dependencies
			 * .toArray(new IResource[dependencies.size()])); }
			 */
		} finally {
			releaseModel(model);
		}
	}

	protected IDOMModel getModel(IProject project, IFile file) {
		if (project == null || file == null)
			return null;
		if (!file.exists())
			return null;
		if (!canHandle(file))
			return null;

		IModelManager manager = StructuredModelManager.getModelManager();
		if (manager == null)
			return null;

		IStructuredModel model = null;
		try {
			file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE, "Error while refreshing file", e);
		}
		try {
			try {
				model = manager.getModelForRead(file);
			} catch (UnsupportedEncodingException ex) {
				// retry ignoring META charset for invalid META charset
				// specification
				// recreate input stream, because it is already partially read
				model = manager.getModelForRead(file, new String(), null);
			}
		} catch (UnsupportedEncodingException ex) {
		} catch (IOException ex) {
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE, "Error while getting DOM model", e);
		}

		if (model == null)
			return null;
		if (!(model instanceof IDOMModel)) {
			releaseModel(model);
			return null;
		}
		return (IDOMModel) model;
	}

	protected void releaseModel(IStructuredModel model) {
		if (model != null)
			model.releaseFromRead();
	}

	/**
	 * Check file extension to validate
	 */
	private boolean canHandle(IFile file) {
		boolean result = false;
		if (file != null) {
			try {
				IContentDescription contentDescription = file
						.getContentDescription();
				if (contentDescription != null) {
					IContentType fileContentType = contentDescription
							.getContentType();
					if (fileContentType.isKindOf(fHTMLContentType)) {
						result = true;
					} else {
						IContentType[] otherTypes = getOtherSupportedContentTypes();
						for (int i = 0; i < otherTypes.length; i++) {
							result = result
									|| fileContentType.isKindOf(otherTypes[i]);
						}
					}
				} else if (fHTMLContentType != null) {
					result = fHTMLContentType.isAssociatedWith(file.getName());
				}
			} catch (CoreException e) {
				// should be rare, but will ignore to avoid logging "encoding
				// exceptions" and the like here.
				// Logger.logException(e);
			}
		}
		return result;
	}

	private IResource getResource(String delta) {
		Path path = new Path(delta);
		if (path.segmentCount() > 1)
			return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (path.segmentCount() == 1)
			return ResourcesPlugin.getWorkspace().getRoot().getProject(delta);
		return null;
	}

	@Override
	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	@Override
	public void cleanup(IReporter reporter) {
		// nothing to do
	}

	/**
	 * Gets list of content types this validator is interested in
	 * 
	 * @return All HTML-related content types
	 */
	private IContentType[] getOtherSupportedContentTypes() {
		if (fOtherSupportedContentTypes == null) {
			List contentTypes = new ArrayList(3);
			if (fAdditionalContentTypesIDs != null) {
				for (int i = 0; i < fAdditionalContentTypesIDs.length; i++) {
					IContentType type = Platform.getContentTypeManager()
							.getContentType(fAdditionalContentTypesIDs[i]);
					if (type != null) {
						contentTypes.add(type);
					}
				}
			}
			fOtherSupportedContentTypes = (IContentType[]) contentTypes
					.toArray(new IContentType[contentTypes.size()]);
		}
		return fOtherSupportedContentTypes;
	}

	protected void validateRegions(IReporter reporter, IStructuredModel model,
			IFile file, IStructuredDocumentRegion[] regions) {
		MessageFactory factory = createMessageFactory(reporter, file);
		for (int i = 0; i < regions.length; i++) {
			validate(regions[i], reporter, model, file, factory);
		}
	}

	protected MessageFactory createMessageFactory(IReporter reporter, IFile file) {
		return new MessageFactory(file.getProject(), this, reporter);
	}

	private void validate(IStructuredDocumentRegion documentRegion,
			IReporter reporter, IStructuredModel model, IFile file,
			MessageFactory factory) {

		if (documentRegion == null) {
			return;
		}

		WebResourcesTextRegion attrValueRegion = DOMHelper.getTextRegion(
				documentRegion, documentRegion.getStartOffset(), true);
		if (attrValueRegion != null) {
			WebResourcesFinderType finderType = attrValueRegion.getType();
			if (factory.getSeverity(finderType) != ValidationMessage.IGNORE) {
				switch (finderType) {
				case CSS_CLASS_NAME:
				case CSS_ID:
					IProgressMonitor monitor = null;
					validateCSS(documentRegion, reporter, model, file, factory,
							attrValueRegion, monitor);
					break;
				case SCRIPT_SRC:
				case LINK_HREF:
					validateFile(documentRegion, reporter, model, file,
							factory, attrValueRegion, finderType);
					break;
				case IMG_SRC:
					validateImage(documentRegion, reporter, model, file,
							factory, attrValueRegion, finderType);
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * Validate CSS class name and ids.
	 * 
	 * @param documentRegion
	 * @param reporter
	 * @param model
	 * @param file
	 * @param factory
	 * @param attrValueRegion
	 * @param monitor
	 */
	private void validateCSS(IStructuredDocumentRegion documentRegion,
			IReporter reporter, IStructuredModel model, IFile file,
			MessageFactory factory, WebResourcesTextRegion attrValueRegion,
			IProgressMonitor monitor) {
		int startOffset = documentRegion.getStartOffset()
				+ attrValueRegion.getRegion().getStart();
		WebResourceRegion hoverRegion = DOMHelper
				.getCSSRegion(attrValueRegion, documentRegion,
						documentRegion.getParentDocument(), startOffset);
		IDOMAttr attr = DOMHelper.getAttrByOffset(model, startOffset);
		if (attr != null) {
			WebResourcesFinderType finderType = attrValueRegion.getType();
			WebResourcesContext context = new WebResourcesContext(attr,
					hoverRegion.getType());
			if (!shouldIgnoreValidation(context, finderType)) {
				switch (hoverRegion.getType()) {
				case CSS_ID:
					// Validate CSS/@id
					CSSIdValidationTraverser cssIdTraverser = new CSSIdValidationTraverser(
							attr, file, hoverRegion, factory);
					cssIdTraverser.process(monitor);
					break;
				case CSS_CLASS_NAME:
					// Validate CSS/@class

					CSSClassNameValidationTraverser cssClassNameTraverser = new CSSClassNameValidationTraverser(
							attr, file, hoverRegion, factory);
					cssClassNameTraverser.process(monitor);

					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * Validate img/@src
	 * 
	 * @param documentRegion
	 * @param reporter
	 * @param model
	 * @param file
	 * @param factory
	 * @param attrValueRegion
	 * @param resourceType
	 */
	private void validateImage(IStructuredDocumentRegion documentRegion,
			IReporter reporter, IStructuredModel model, IFile file,
			MessageFactory factory, WebResourcesTextRegion attrValueRegion,
			WebResourcesFinderType resourceType) {
		IDOMAttr attr = DOMHelper.getAttrByOffset(model,
				documentRegion.getStartOffset()
						+ attrValueRegion.getRegion().getStart());
		if (attr != null) {
			String attrValue = DOMHelper.getAttrValue(documentRegion
					.getText(attrValueRegion.getRegion()));
			if (URIHelper.isDataURIScheme(attrValue)) {
				// see https://en.wikipedia.org/wiki/Data_URI_scheme
				// ex : <img
				// src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=="
				// alt="Red dot" />
				// TODO : validate format of data uri scheme.
			} else {
				// ex : <img src="path/to/image.png" />
				validateFile(documentRegion, reporter, model, file, factory,
						attrValueRegion, resourceType);
			}
		}
	}

	/**
	 * Validate existing of the file.
	 * 
	 * @param documentRegion
	 * @param reporter
	 * @param model
	 * @param file
	 * @param factory
	 * @param attrValueRegion
	 * @param resourceType
	 */
	private void validateFile(IStructuredDocumentRegion documentRegion,
			IReporter reporter, IStructuredModel model, IFile file,
			MessageFactory factory, WebResourcesTextRegion attrValueRegion,
			WebResourcesFinderType resourceType) {
		IDOMAttr attr = DOMHelper.getAttrByOffset(model,
				documentRegion.getStartOffset()
						+ attrValueRegion.getRegion().getStart());
		if (attr != null) {
			String attrValue = DOMHelper.getAttrValue(documentRegion
					.getText(attrValueRegion.getRegion()));
			WebResourcesFinderType finderType = attrValueRegion.getType();
			WebResourcesContext context = new WebResourcesContext(attr,
					resourceType);
			if (!shouldIgnoreValidation(context, finderType)) {
				if (URIHelper.isExternalURL(attrValue)) {
					// attribute value starts with http or //, validate the URL
					// if need
					if (factory.isValidateExternalURL()
							&& !URIHelper.validateExternalURL(attrValue)) {
						factory.addMessage(attr, finderType, file, true);
					}
				} else if (!WebResourcesProvidersManager.getInstance().exists(
						attrValue, context)) {
					// validate file
					factory.addMessage(attr, finderType, file);
				}
			}

		}
	}

	/**
	 * Return true if validation of web resources files should be ignored and
	 * false otherwise.
	 * 
	 * @param context
	 * @param finderType
	 * @return
	 */
	private boolean shouldIgnoreValidation(IWebResourcesContext context,
			WebResourcesFinderType finderType) {
		Collection<IWebResourcesIgnoreValidator> ignoreValidators = WebResourcesIgnoreValidatorsManager
				.getInstance().getIgnoreValidators(finderType);
		for (IWebResourcesIgnoreValidator ignoreValidator : ignoreValidators) {
			if (ignoreValidator.shouldIgnore(null,
					WebResourceKind.ECLIPSE_RESOURCE, context)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ValidationResult validate(IResource resource, int kind,
			ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE)
			return null;
		ValidationResult result = new ValidationResult();
		IReporter reporter = result.getReporter(monitor);
		validateFile(null, reporter, (IFile) resource, result);
		return result;
	}

}