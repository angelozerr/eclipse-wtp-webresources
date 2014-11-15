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
package org.eclipse.wst.html.webresources.core.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.html.webresources.core.WebResourceType;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.html.webresources.internal.core.providers.WebResourcesProviderType;

public class WebResourcesProvidersManager implements IURIResolver {

	private static final String PLUGIN_ID = WebResourcesCorePlugin.PLUGIN_ID;
	private static final String EXTENSION_POINT_ID = "webResourcesProviders";

	private Map<WebResourceType, Collection<WebResourcesProviderType>> providerTypes;

	private static final WebResourcesProvidersManager INSTANCE = new WebResourcesProvidersManager();

	public static WebResourcesProvidersManager getInstance() {
		return INSTANCE;
	}

	public boolean exists(String uri, IWebResourcesContext context) {
		WebResourceType resourceType = context.getResourceType();
		Collection<WebResourcesProviderType> providerTypes = getProviderTypes(resourceType);
		for (WebResourcesProviderType providerType : providerTypes) {
			if (providerType.exists(uri, context)) {
				return true;
			}
		}
		return false;
	}

	public void collect(IWebResourcesContext context,
			IWebResourcesCollector collector, IProgressMonitor monitor) {
		WebResourceType resourceType = context.getResourceType();
		Collection<WebResourcesProviderType> providerTypes = getProviderTypes(resourceType);
		for (WebResourcesProviderType providerType : providerTypes) {
			providerType.collect(context, collector, monitor);
		}
	}

	private Collection<WebResourcesProviderType> getProviderTypes(
			WebResourceType resourcesType) {
		Collection<WebResourcesProviderType> providerTypes = getProvidersMap()
				.get(resourcesType);
		if (providerTypes != null) {
			return providerTypes;
		}
		return Collections.emptyList();
	}

	private Map<WebResourceType, Collection<WebResourcesProviderType>> getProvidersMap() {
		if (providerTypes == null) {
			providerTypes = loadProvidersMap();
		}
		return providerTypes;
	}

	private synchronized Map<WebResourceType, Collection<WebResourcesProviderType>> loadProvidersMap() {
		if (providerTypes != null) {
			return providerTypes;
		}
		Map<WebResourceType, Collection<WebResourcesProviderType>> map = new HashMap<WebResourceType, Collection<WebResourcesProviderType>>();
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
			Map<WebResourceType, Collection<WebResourcesProviderType>> map) {
		String className = null;
		try {
			className = element.getAttribute("class");
			Object provider = element.createExecutableExtension("class");
			IWebResourcesProvider resourcesProvider = null;
			IWebResourcesFileSystemProvider fileSystemProvider = null;
			if (provider instanceof IWebResourcesProvider) {
				resourcesProvider = (IWebResourcesProvider) provider;
			}
			if (provider instanceof IWebResourcesFileSystemProvider) {
				fileSystemProvider = (IWebResourcesFileSystemProvider) provider;
			}
			if (resourcesProvider != null || fileSystemProvider != null) {
				String[] types = element.getAttribute("types").split(",");
				for (int i = 0; i < types.length; i++) {
					WebResourceType resourcesType = WebResourceType
							.get(types[i].trim());
					WebResourcesProviderType providerType = new WebResourcesProviderType(
							resourcesProvider, fileSystemProvider,
							resourcesType);
					Collection<WebResourcesProviderType> providerTypes = map
							.get(resourcesType);
					if (providerTypes == null) {
						providerTypes = new ArrayList<WebResourcesProviderType>();
						map.put(resourcesType, providerTypes);
					}
					providerTypes.add(providerType);
				}
			}
		} catch (Throwable t) {
			Trace.trace(
					Trace.SEVERE,
					"  Could not load web resources providers: " + className != null ? className
							: "", t);
		}
	}

	@Override
	public IPath resolve(IResource resource, IFile root) {
		// TODO : manage resolver with extension point
		return DefaultURIResolver.INSTANCE.resolve(resource, root);
	}

	@Override
	public boolean exists(String uri, IFile root) {
		// TODO : manage resolver with extension point
		return DefaultURIResolver.INSTANCE.exists(uri, root);
	}
}
