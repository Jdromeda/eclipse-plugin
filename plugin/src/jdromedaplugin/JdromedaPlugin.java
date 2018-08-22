package jdromedaplugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class JdromedaPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String ID = "jdromeda-plugin";

	// The shared instance
	private static JdromedaPlugin plugin;
	
	/**
	 * The constructor
	 */
	public JdromedaPlugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}


	public static JdromedaPlugin getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(ID, path);
	}
}
