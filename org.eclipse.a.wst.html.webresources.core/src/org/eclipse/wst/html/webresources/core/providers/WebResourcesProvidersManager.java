package org.eclipse.wst.html.webresources.core.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.html.webresources.core.DOMHelper;
import org.eclipse.wst.html.webresources.core.WebResourcesType;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.html.webresources.internal.core.WebResourcesCorePlugin;
import org.eclipse.wst.html.webresources.internal.core.providers.WebResourcesProviderType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class WebResourcesProvidersManager {

	private static final String PLUGIN_ID = WebResourcesCorePlugin.PLUGIN_ID;
	private static final String EXTENSION_POINT_ID = "webResourcesProviders";
	private static final Object TAG_NAME = "provider";

	private static Map<WebResourcesType, Collection<WebResourcesProviderType>> providerTypes;

	public static void collect(IDOMNode node, WebResourcesType resourcesType,
			IWebResourcesCollector collector) {
		Collection<WebResourcesProviderType> providerTypes = WebResourcesProvidersManager
				.getProviderTypes(resourcesType);
		IProject project = DOMHelper.getFile(node).getProject();
		for (WebResourcesProviderType providerType : providerTypes) {
			providerType.collect(node, project, collector);
		}
	}

	private static Collection<WebResourcesProviderType> getProviderTypes(
			WebResourcesType resourcesType) {
		Collection<WebResourcesProviderType> providerTypes = getProvidersMap()
				.get(resourcesType);
		if (providerTypes != null) {
			return providerTypes;
		}
		return Collections.emptyList();
	}

	private static Map<WebResourcesType, Collection<WebResourcesProviderType>> getProvidersMap() {
		if (providerTypes == null) {
			providerTypes = loadProvidersMap();
		}
		return providerTypes;
	}

	private static synchronized Map<WebResourcesType, Collection<WebResourcesProviderType>> loadProvidersMap() {
		if (providerTypes != null) {
			return providerTypes;
		}
		Map<WebResourcesType, Collection<WebResourcesProviderType>> map = new HashMap<WebResourcesType, Collection<WebResourcesProviderType>>();
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

	private static void readElement(IConfigurationElement element,
			Map<WebResourcesType, Collection<WebResourcesProviderType>> map) {
		String className = null;
		try {
			className = element.getAttribute("class");
			IWebResourcesProvider provider = (IWebResourcesProvider) element
					.createExecutableExtension("class");
			String[] types = element.getAttribute("types").split(",");
			for (int i = 0; i < types.length; i++) {
				WebResourcesType resourcesType = WebResourcesType.get(types[i]
						.trim());
				WebResourcesProviderType providerType = new WebResourcesProviderType(
						provider, resourcesType);
				Collection<WebResourcesProviderType> providerTypes = map
						.get(providerType);
				if (providerTypes == null) {
					providerTypes = new ArrayList<WebResourcesProviderType>();
					map.put(resourcesType, providerTypes);
				}
				providerTypes.add(providerType);
			}
		} catch (Throwable t) {
			Trace.trace(
					Trace.SEVERE,
					"  Could not load web resources providers: " + className != null ? className
							: "", t);
		}
	}
}
