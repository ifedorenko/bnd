package aQute.bnd.runtime;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import aQute.bnd.service.Registry;

public class OSGiServiceRegistry implements Registry {
	
	private final BundleContext context;
	private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
	private final Map<Reference<?>, ServiceReference> referenceMap = new IdentityHashMap<Reference<?>, ServiceReference>();
	
	private final Thread cleanupThread = new Thread("OSGiServiceRegistry Reference Clean-up Thread") {
		@Override
		public void run() {
			System.out.println("STARTING: OSGiServiceRegistry Reference Clean-up Thread");
			try {
				while (!Thread.interrupted()) {
					Reference<? extends Object> ref = referenceQueue.remove();
					ServiceReference svcRef = referenceMap.remove(ref);
					if (svcRef != null) {
						System.out.println("Cleaned up reference to: " + svcRef.toString());
						context.ungetService(svcRef);
					}
				}
			} catch (InterruptedException e) {
			} finally {
				System.out.println("STOPPED: OSGiServiceRegistry Reference Clean-up Thread");
			}
		}
	};
	
	public OSGiServiceRegistry(BundleContext context) {
		this.context = context;
	}

	public <T> List<T> getPlugins(Class<T> clazz) {
		try {
			List<T> result = Collections.emptyList();
			ServiceReference[] svcRefs = context.getServiceReferences(clazz.getName(), null);
			if (svcRefs != null) {
				result = new ArrayList<T>(svcRefs.length);
				for (ServiceReference svcRef : svcRefs) {
					@SuppressWarnings("unchecked") T service = (T) context.getService(svcRef);
					enqueueReference(service, svcRef);
					result.add(service);
				}
			}
			return result;
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e); // shouldn't happen
		}
	}

	public <T> T getPlugin(Class<T> clazz) {
		ServiceReference ref = context.getServiceReference(clazz.getName());
		if (ref == null)
			return null;
		
		@SuppressWarnings("unchecked") T service = (T) context.getService(ref);
		enqueueReference(service, ref);
		
		return service;
	}
	
	public void start() {
		cleanupThread.start();
	}
	
	public void stop() {
		cleanupThread.interrupt();
	}

	void enqueueReference(Object service, ServiceReference svcRef) {
		System.out.println("Adding reference to: " + svcRef.toString());
		WeakReference<Object> reference = new WeakReference<Object>(service, referenceQueue);
		synchronized (referenceMap) {
			referenceMap.put(reference, svcRef);
		}
	}

}
