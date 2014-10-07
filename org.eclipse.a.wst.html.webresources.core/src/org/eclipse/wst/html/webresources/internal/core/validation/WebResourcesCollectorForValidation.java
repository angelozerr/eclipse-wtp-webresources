/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Gregory Amerson <gregory.amerson@liferay.com> - https://github.com/angelozerr/eclipse-wtp-webresources/issues/14
 */
package org.eclipse.wst.html.webresources.internal.core.validation;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.providers.WebResourcesCollectorAdapter;
import org.eclipse.wst.html.webresources.core.validation.IWebResourcesIgnoreValidator;
import org.eclipse.wst.html.webresources.core.validation.WebResourcesIgnoreValidatorsManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;

public class WebResourcesCollectorForValidation extends
		WebResourcesCollectorAdapter {

	private final IDOMAttr attr;
	private final IFile file;
	private final WebResourcesFinderType finderType;
	private final String fileName;
	private final MessageFactory factory;
	private int nbFiles;
	private final Collection<IWebResourcesIgnoreValidator> ignoreValidators;

	public WebResourcesCollectorForValidation(String fileName, IDOMAttr attr,
			IFile file, WebResourcesFinderType finderType,
			MessageFactory factory) {
		this.fileName = fileName;
		this.attr = attr;
		this.file = file;
		this.finderType = finderType;
		this.factory = factory;
		this.nbFiles = 0;
		this.ignoreValidators = WebResourcesIgnoreValidatorsManager.getInstance().getIgnoreValidators( finderType );
	}

	@Override
	public boolean add(Object resource, WebResourceKind resourceKind,
			IWebResourcesContext context, IURIResolver resolver) {
		if (resourceKind == WebResourceKind.ECLIPSE_RESOURCE) {
			for (IWebResourcesIgnoreValidator ignoreValidator : this.ignoreValidators ) {
				if (ignoreValidator.shouldIgnore(resource, resourceKind, context)) {
					this.nbFiles++;
					return true;
				}
			}
			IResource r = (IResource) resource;
			IFile htmlFile = context.getHtmlFile();
			IPath resourceFileLoc = resolver.resolve(r, htmlFile);
			if (resourceFileLoc.toString().equals(fileName)) {
				this.nbFiles++;
				return true;
			}
		}
		return false;
	}

	public int getNbFiles() {
		return nbFiles;
	}

	@Override
	public void endCollect(WebResourceType resourceType) {
		if (nbFiles == 0) {
			factory.addMessage(attr, finderType, file);
		}
	}
}
