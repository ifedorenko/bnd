package aQute.bnd.runtime;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;

public class Activator implements BundleActivator {

	private OSGiServiceRegistry compatRegistry;
	private RegistryPluginTracker pluginTracker;

	public void start(BundleContext context) throws Exception {
		ClassLoader loader = Activator.class.getClassLoader();
		if (loader instanceof BundleReference)
			System.out.println("bnd seems to be running in real OSGi");
		else
			System.out.println("bnd seems to be running in PojoSR");
		
		compatRegistry = new OSGiServiceRegistry(context);
		compatRegistry.start();
		
		pluginTracker = new RegistryPluginTracker(context, compatRegistry);
		pluginTracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		pluginTracker.close();
		compatRegistry.stop();
	}

}
