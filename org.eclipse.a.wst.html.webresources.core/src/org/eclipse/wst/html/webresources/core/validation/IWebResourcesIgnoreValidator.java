/**
 *  Copyright (c) 2014 Liferay, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      Gregory Amerson <gregory.amerson@liferay.com> - initial API and implementation
 */
package org.eclipse.wst.html.webresources.core.validation;

import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;


public interface IWebResourcesIgnoreValidator {

    boolean shouldIgnore(Object resource, WebResourceKind resourceKind, IWebResourcesContext context);

}
