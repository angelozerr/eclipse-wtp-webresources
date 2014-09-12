package org.eclipse.wst.html.webresources.internal.core.providers;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.webresources.core.WebResourcesType;
import org.eclipse.wst.html.webresources.core.providers.IURIResolver;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesCollector;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesProvider;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class WebResourcesProviderType {

	private final IWebResourcesProvider provider;
	private final WebResourcesType resourcesType;

	public WebResourcesProviderType(IWebResourcesProvider provider,
			WebResourcesType resourcesType) {
		this.provider = provider;
		this.resourcesType = resourcesType;
	}

	public void collect(IDOMNode htmlNode, IFile htmlFile,
			IWebResourcesCollector collector) {
		IContainer[] containers = provider.getContainers(htmlNode, htmlFile);
		if (containers != null) {
			IURIResolver resolver = provider.getResolver(htmlNode, htmlFile);
			IResourceVisitor visitor = new WebResourceVisitor(collector,
					resourcesType, htmlNode, htmlFile, provider);
			for (int i = 0; i < containers.length; i++) {
				try {
					containers[i].accept(visitor);
				} catch (CoreException e) {
					Trace.trace(Trace.SEVERE,
							"Error while collecting resource for container "
									+ containers[i].getName(), e);
				}
			}
		}
	}

}
