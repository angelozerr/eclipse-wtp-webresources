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
package org.eclipse.wst.html.webresources.core;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.html.webresources.internal.core.Trace;
import org.eclipse.wst.html.webresources.internal.core.WebResourcesCoreMessages;
import org.eclipse.wst.html.webresources.internal.core.WebResourcesFinderTypeProviderManager;
import org.eclipse.wst.html.webresources.internal.core.search.WebResourcesIndexManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class WebResourcesCorePlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.a.wst.html.webresources.core"; //$NON-NLS-1$

	// The shared instance
	private static WebResourcesCorePlugin plugin;

	private PluginInitializerJob fPluginInitializerJob;

	/**
	 * The constructor
	 */
	public WebResourcesCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.fPluginInitializerJob = new PluginInitializerJob();
		// schedule delayed initialization
		this.fPluginInitializerJob.schedule(2000);
		//WebResourcesFileManager.getInstance().initialize();
		WebResourcesFinderTypeProviderManager.getManager().initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		//WebResourcesFileManager.getInstance().dispose();
		WebResourcesFinderTypeProviderManager.getManager().destroy();
		// stop any indexing
		WebResourcesIndexManager.getDefault().stop();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static WebResourcesCorePlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Logs the given <code>Throwable</code>.
	 * 
	 * @param t the <code>Throwable</code>
	 */
	public static void log(Throwable t) {
		plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	/**
	 * <p>
	 * A {@link Job} used to perform delayed initialization for the plugin
	 * </p>
	 */
	private static class PluginInitializerJob extends Job {
		/**
		 * <p>
		 * Default constructor to set up this {@link Job} as a long running
		 * system {@link Job}
		 * </p>
		 */
		protected PluginInitializerJob() {
			super(
					WebResourcesCoreMessages.WebResourcesCorePlugin_Initializing_WebResources_Tools);

			this.setUser(false);
			this.setSystem(true);
			this.setPriority(Job.LONG);
		}

		/**
		 * <p>
		 * Perform delayed initialization for the plugin
		 * </p>
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			IStatus status = Status.OK_STATUS;
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			try {
				/*
				 * Restore save state and process any events that happened
				 * before plug-in loaded. Don't do it immediately since adding
				 * the save participant requires a lock on the workspace to
				 * compute the accumulated deltas, and if the tree is not
				 * already locked it becomes a blocking call.
				 */
				workspace.run(new IWorkspaceRunnable() {
					public void run(final IProgressMonitor worspaceMonitor)
							throws CoreException {
						ISavedState savedState = null;

						try {
							// add the save participant for this bundle
							savedState = ResourcesPlugin.getWorkspace()
									.addSaveParticipant(
											WebResourcesCorePlugin.plugin
													.getBundle()
													.getSymbolicName(),
											new SaveParticipant());
						} catch (CoreException e) {
							Trace.trace(
									Trace.SEVERE,
									"Web Resources Core Plugin failed at loading previously saved state." + //$NON-NLS-1$
											" All components dependent on this state will start as if first workspace load.",
									e); //$NON-NLS-1$
						}

						// if there is a saved state start up using that, else
						// start up cold
						//if (savedState != null) {
						if (false) {
							try {
								Thread.currentThread().setPriority(
										Thread.MIN_PRIORITY);
							} finally {
								savedState
										.processResourceChangeEvents(new IResourceChangeListener() {
											@Override
											public void resourceChanged(
													IResourceChangeEvent event) {
												WebResourcesIndexManager
														.getDefault()
														.start(event.getDelta(),
																worspaceMonitor);
											}
										});
							}
						} else {
							WebResourcesIndexManager.getDefault().start(null,
									worspaceMonitor);
						}
					}
				}, monitor);
			} catch (CoreException e) {
				status = e.getStatus();
			}
			return status;
		}

	}

	/**
	 * Used so that all of the IResourceChangeEvents that occurred before this
	 * plugin loaded can be processed.
	 */
	private static class SaveParticipant implements ISaveParticipant {
		/**
		 * <p>
		 * Default constructor
		 * </p>
		 */
		protected SaveParticipant() {
		}

		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context) {
			// ignore
		}

		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException {
			// ignore
		}

		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context) {
			// ignore
		}

		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException {
			context.needDelta();
		}
	}
}
