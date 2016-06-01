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
package org.eclipse.wst.html.webresources.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.html.webresources.core.IWebResourcesFinderTypeProvider;
import org.eclipse.wst.html.webresources.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

/**
 * Tern file configuration manager.
 * 
 */
public class WebResourcesFinderTypeProviderManager implements
		IRegistryChangeListener {

	private static final String CLASS = "class";

	private static final String EXTENSION_WEB_RESOURCES_FINDER_TYPE_PROVIDERS = "webResourcesFinderTypeProviders";

	private static final WebResourcesFinderTypeProviderManager INSTANCE = new WebResourcesFinderTypeProviderManager();

	private List<IWebResourcesFinderTypeProvider> webResourcesFinderTypeProviders;

	private boolean registryListenerIntialized;

	public WebResourcesFinderTypeProviderManager() {
		this.registryListenerIntialized = false;
	}

	public static WebResourcesFinderTypeProviderManager getManager() {
		return INSTANCE;
	}

	@Override
	public void registryChanged(final IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas(
				WebResourcesCorePlugin.PLUGIN_ID,
				EXTENSION_WEB_RESOURCES_FINDER_TYPE_PROVIDERS);
		if (deltas != null) {
			for (IExtensionDelta delta : deltas)
				handleDelta(delta);
		}
	}

	public IWebResourcesFinderTypeProvider[] getProviders() {
		if (webResourcesFinderTypeProviders == null)
			loadTernServerConfigurations();

		IWebResourcesFinderTypeProvider[] st = new IWebResourcesFinderTypeProvider[webResourcesFinderTypeProviders
				.size()];
		webResourcesFinderTypeProviders.toArray(st);
		return st;
	}

	/**
	 * Load the tern server types.
	 */
	private synchronized void loadTernServerConfigurations() {
		if (webResourcesFinderTypeProviders != null)
			return;

		Trace.trace(Trace.EXTENSION_POINT,
				"->- Loading .webResourcesFinderTypeProviders extension point ->-");

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				WebResourcesCorePlugin.PLUGIN_ID,
				EXTENSION_WEB_RESOURCES_FINDER_TYPE_PROVIDERS);
		List<IWebResourcesFinderTypeProvider> list = new ArrayList<IWebResourcesFinderTypeProvider>(
				cf.length);
		addTernServerConfigurations(cf, list);
		addRegistryListenerIfNeeded();
		webResourcesFinderTypeProviders = list;

		Trace.trace(Trace.EXTENSION_POINT,
				"-<- Done loading .webResourcesFinderTypeProviders extension point -<-");
	}

	/**
	 * Load the tern server types.
	 */
	private synchronized void addTernServerConfigurations(
			IConfigurationElement[] cf,
			List<IWebResourcesFinderTypeProvider> list) {
		for (IConfigurationElement ce : cf) {
			try {
				list.add((IWebResourcesFinderTypeProvider) ce
						.createExecutableExtension(CLASS));
				Trace.trace(
						Trace.EXTENSION_POINT,
						"  Loaded console connectors: "
								+ ce.getAttribute(CLASS));
			} catch (Throwable t) {
				Trace.trace(
						Trace.SEVERE,
						"  Could not load console connectors: "
								+ ce.getAttribute(CLASS), t);
			}
		}
	}

	protected void handleDelta(IExtensionDelta delta) {
		if (webResourcesFinderTypeProviders == null) // not loaded yet
			return;

		IConfigurationElement[] cf = delta.getExtension()
				.getConfigurationElements();

		List<IWebResourcesFinderTypeProvider> list = new ArrayList<IWebResourcesFinderTypeProvider>(
				webResourcesFinderTypeProviders);
		if (delta.getKind() == IExtensionDelta.ADDED) {
			addTernServerConfigurations(cf, list);
		}
		webResourcesFinderTypeProviders = list;
	}

	private void addRegistryListenerIfNeeded() {
		if (registryListenerIntialized)
			return;

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.addRegistryChangeListener(this,
				WebResourcesCorePlugin.PLUGIN_ID);
		registryListenerIntialized = true;
	}

	public void initialize() {

	}

	public void destroy() {
		if (webResourcesFinderTypeProviders == null) // not loaded yet
			return;
		webResourcesFinderTypeProviders.clear();
		webResourcesFinderTypeProviders = null;
		Platform.getExtensionRegistry().removeRegistryChangeListener(this);
	}

	public WebResourcesFinderType getWebResourcesFinderType(String elementName,
			String attrName, IStructuredDocumentRegion documentRegion,
			int documentPosition) {
		IWebResourcesFinderTypeProvider[] providers = getProviders();
		for (IWebResourcesFinderTypeProvider provider : providers) {
			WebResourcesFinderType finder = provider.getWebResourcesFinderType(
					elementName, attrName, documentRegion, documentPosition);
			if (finder != null) {
				return finder;
			}
		}
		return null;
	}
}
