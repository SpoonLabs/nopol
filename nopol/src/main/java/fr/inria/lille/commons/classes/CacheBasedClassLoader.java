package fr.inria.lille.commons.classes;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.TreeMap;

public class CacheBasedClassLoader extends URLClassLoader {

	private Map<String, Class<?>> classcache = new TreeMap<String, Class<?>>();

	public CacheBasedClassLoader(URL[] urls, Map<String, Class<?>> classcache){
		super(urls);
		this.classcache = classcache;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if ( classcache.containsKey(name)){
			return classcache.get(name);
		} else {
			Class<?> c = super.loadClass(name);
			classcache.put(name, c);
			return c;
		}
	}
}
