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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.html.webresources.internal.core.Trace;

public class WebResourcesIgnoreValidatorsManager {

	private static final String PLUGIN_ID = WebResourcesCorePlugin.PLUGIN_ID;
	private static final String EXTENSION_POINT_ID = "webResourcesIgnoreValidators";

	private Map<WebResourcesFinderType, Collection<IWebResourcesIgnoreValidator>> ignoreValidatorTypes;

	private static final WebResourcesIgnoreValidatorsManager INSTANCE = new WebResourcesIgnoreValidatorsManager();

	public static WebResourcesIgnoreValidatorsManager getInstance() {
		return INSTANCE;
	}

	public Collection<IWebResourcesIgnoreValidator> getIgnoreValidators(WebResourcesFinderType resourcesType) {
		Collection<IWebResourcesIgnoreValidator> validators = getIgnoreValidatorsMap().get( resourcesType );
		if( validators != null ) {
			return validators;
		}
		return Collections.emptyList();
	}

	private Map<WebResourcesFinderType, Collection<IWebResourcesIgnoreValidator>> getIgnoreValidatorsMap() {
		if (ignoreValidatorTypes == null) {
			ignoreValidatorTypes = loadIgnoreValidatorsMap();
		}
		return ignoreValidatorTypes;
	}

	private synchronized Map<WebResourcesFinderType, Collection<IWebResourcesIgnoreValidator>> loadIgnoreValidatorsMap() {
		if (ignoreValidatorTypes != null) {
			return ignoreValidatorTypes;
		}
		Map<WebResourcesFinderType, Collection<IWebResourcesIgnoreValidator>> map =
						new HashMap<WebResourcesFinderType, Collection<IWebResourcesIgnoreValidator>>();
		IExtensionPoint point = Platform.getExtensionRegistry()
				.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				readElement(elements[i], map);
			}
		}
		return map;
	}

	private void readElement(IConfigurationElement element,
			Map<WebResourcesFinderType, Collection<IWebResourcesIgnoreValidator>> map) {
		String className = null;
		try {
			className = element.getAttribute("class");
			Object provider = element.createExecutableExtension("class");
			IWebResourcesIgnoreValidator resourcesValidator = null;
			if (provider instanceof IWebResourcesIgnoreValidator) {
				resourcesValidator = (IWebResourcesIgnoreValidator) provider;
				String[] types = element.getAttribute("types").split(",");
				for (int i = 0; i < types.length; i++) {
					WebResourcesFinderType resourcesType = WebResourcesFinderType
							.get(types[i].trim());
					Collection<IWebResourcesIgnoreValidator> providerTypes = map
							.get(resourcesType);
					if (providerTypes == null) {
						providerTypes = new ArrayList<IWebResourcesIgnoreValidator>();
						map.put(resourcesType, providerTypes);
					}
					providerTypes.add(resourcesValidator);
				}
			}
		} catch (Throwable t) {
			Trace.trace(
					Trace.SEVERE,
					"  Could not load web resources validators: " + className != null ? className
							: "", t);
		}
	}

}
