package fr.inria.lille.commons.compiler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.string.StringLibrary;

public class DynamicallyCompiledClassLoader extends ClassLoader {

	public DynamicallyCompiledClassLoader() {
		super();
		initialize();
	}
	
	public DynamicallyCompiledClassLoader(ClassLoader parentClassLoader) {
		super(parentClassLoader);
		initialize();
	}
	
	public DynamicallyCompiledClassLoader copy() {
		DynamicallyCompiledClassLoader copy = new DynamicallyCompiledClassLoader(getParent());
		copy.classFiles().putAll(classFiles());
		return copy;
	}
	
	private void initialize() {
		classFiles = MapLibrary.newHashMap();
	}
	
	@Override
	protected synchronized Class<?> loadClass(String qualifiedName, boolean resolve) throws ClassNotFoundException {
		return super.loadClass(qualifiedName, resolve);
	}
   
	@Override
	protected Class<?> findClass(String qualifiedName) throws ClassNotFoundException {
		if (containsCompiledClassFor(qualifiedName)) {
			byte[] bytes = compiledClass(qualifiedName).byteCodes();
			return defineClass(qualifiedName, bytes, 0, bytes.length);
		}
		try {
			return Class.forName(qualifiedName);
		}
		catch (ClassNotFoundException nf) {}
		return super.findClass(qualifiedName);
	}
   
	@Override
	public InputStream getResourceAsStream(String resourceName) {
	   if (resourceName.endsWith(".class")) {
         String qualifiedName = StringLibrary.stripEnd(resourceName, ".class").replace('/', '.');
         if (containsCompiledClassFor(qualifiedName)) {
        	 return new ByteArrayInputStream(compiledClass(qualifiedName).byteCodes());
         }
      }
      	return super.getResourceAsStream(resourceName);
   	}
	
	public void addClassFileObject(String qualifiedName, VirtualClassFileObject classFile) {
		classFiles().put(qualifiedName, classFile);
	}

	public boolean containsCompiledClassFor(String qualifiedName) {
		return classFiles().containsKey(qualifiedName);
	}
	
	public VirtualClassFileObject compiledClass(String qualifiedName) {
		return classFiles().get(qualifiedName);
	}
	
	public Collection<VirtualClassFileObject> compiledClasses() {
		return classFiles().values();
	}
	
	private Map<String, VirtualClassFileObject> classFiles() {
		return classFiles;
	}
	
	private Map<String, VirtualClassFileObject> classFiles;
}
