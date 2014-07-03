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
	
	private void initialize() {
		classByteCodes = MapLibrary.newHashMap();
		loadedClasses = MapLibrary.newHashMap();
	}
	
	@Override
	protected synchronized Class<?> loadClass(String qualifiedName, boolean resolve) throws ClassNotFoundException {
		return super.loadClass(qualifiedName, resolve);
	}
   
	@Override
	protected Class<?> findClass(String qualifiedName) throws ClassNotFoundException {
		if (alreadyLoaded(qualifiedName)) {
			return loadedClass(qualifiedName);
		}
		if (containsCompiledClassFor(qualifiedName)) {
			byte[] bytes = compiledClass(qualifiedName).byteCodes();
			return definedClass(qualifiedName, bytes);
		}
		try {
			return Class.forName(qualifiedName);
		}
		catch (ClassNotFoundException nf) {}
		return super.findClass(qualifiedName);
	}
   
	private Class<?> definedClass(String qualifiedName, byte[] bytes) {
		Class<?> definedClass = defineClass(qualifiedName, bytes, 0, bytes.length);
		loadedClasses().put(qualifiedName, definedClass);
		return definedClass;
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
	
	public void addClassFileObject(String qualifiedName, BufferedClassFileObject classFile) {
		classByteCodes().put(qualifiedName, classFile);
	}

	public boolean containsCompiledClassFor(String qualifiedName) {
		return classByteCodes().containsKey(qualifiedName);
	}
	
	public boolean alreadyLoaded(String qualifiedName) {
		return loadedClasses().containsKey(qualifiedName);
	}
	
	public BufferedClassFileObject compiledClass(String qualifiedName) {
		return classByteCodes().get(qualifiedName);
	}
	
	public Class<?> loadedClass(String qualifiedName) {
		return loadedClasses().get(qualifiedName);
	}
	
	public Collection<BufferedClassFileObject> compiledClasses() {
		return classByteCodes().values();
	}
	
	public Map<String, Class<?>> loadedClasses() {
		return loadedClasses;
	}
	
	private Map<String, BufferedClassFileObject> classByteCodes() {
		return classByteCodes;
	}
	
	private Map<String, BufferedClassFileObject> classByteCodes;
	private Map<String, Class<?>> loadedClasses;
}
