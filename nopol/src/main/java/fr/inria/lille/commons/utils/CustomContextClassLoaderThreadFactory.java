package fr.inria.lille.commons.utils;

import java.util.concurrent.ThreadFactory;

public final class CustomContextClassLoaderThreadFactory implements ThreadFactory {

	public CustomContextClassLoaderThreadFactory(ClassLoader customClassLoader) {
		this.customClassLoader = customClassLoader;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread newThread = new Thread(r);
		newThread.setDaemon(true);
		newThread.setContextClassLoader(customClassLoader());
		return newThread;
	}
	
	private ClassLoader customClassLoader() {
		return customClassLoader;
	}
	
	private ClassLoader customClassLoader;
}
