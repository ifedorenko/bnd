package aQute.bnd.runtime;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import aQute.bnd.service.Registry;
import aQute.bnd.service.RegistryPlugin;

public class RegistryPluginTracker extends ServiceTracker {
	
	private final Registry registry;

	public RegistryPluginTracker(BundleContext context, Registry registry) {
		super(context, RegistryPlugin.class.getName(), null);
		this.registry = registry;
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		RegistryPlugin plugin = (RegistryPlugin) context.getService(reference);
		plugin.setRegistry(registry);
		return plugin;
	}
	
}
