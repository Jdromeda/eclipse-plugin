package jdromedaplugin.launchshortcut;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import jdromedaplugin.builder.Executer;
import jdromedaplugin.builder.InternalServer;
import jdromedaplugin.builder.JdromedaBuilder;
import jdromedaplugin.builder.JdromedaNature;

public class RunAsWeb implements ILaunchShortcut {

	Display display;

	@Override
	public void launch(ISelection selection, String mode) {
		// TODO Auto-generated method stub
		MessageConsole console = findConsole("Launch Jdromeda Web App");
		MessageConsoleStream out = console.newMessageStream();

		if (!(selection instanceof ITreeSelection)) {
			return;
		}
		ITreeSelection treeSelection = (ITreeSelection) selection;

		if (treeSelection.getFirstElement() instanceof IProject) {
			IProject project = (IProject) treeSelection.getFirstElement();
			String name = "Launch Jdromeda Web App";// Resources.BUNDLE.getString("preferences.compiler.rebuild.job.name");
			Job job = new Job(name) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						if (project.isOpen() && project.hasNature(JdromedaNature.ID)) {
							project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, JdromedaBuilder.ID, null, monitor);
							InternalServer.server.startServer(project);
						}
						return Status.OK_STATUS;
					} catch (CoreException e) {
						return e.getStatus();
					} catch (Exception e) {
						return Status.CANCEL_STATUS;
					}
				}
			};
			job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
			job.schedule();
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		//
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
