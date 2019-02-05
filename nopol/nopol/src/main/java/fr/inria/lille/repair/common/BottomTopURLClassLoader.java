package fr.inria.lille.repair.common;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class BottomTopURLClassLoader extends URLClassLoader {
	private ClassLoader parent;

	public BottomTopURLClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, null);
		this.parent = parent;
	}

	public BottomTopURLClassLoader(URL[] urls) {
		this(urls, Thread.currentThread().getContextClassLoader());
	}

	public BottomTopURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, null, factory);
		this.parent = parent;
	}

	@Override
	public URL findResource(String name) {
		return super.findResource(name);
	}



	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (name.contains("junit") || name.contains("fr.inria.lille") || name.contains("_Instrumenting")) {
			try {
				return parent.loadClass(name);
			} catch (ClassNotFoundException ignore) {
				return super.loadClass(name);
			}
		}
		try {
			return super.loadClass(name);
		} catch (ClassNotFoundException ignore) {
			return parent.loadClass(name);
		}
	}
}
