package fr.inria.lille.commons.compiler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.string.StringLibrary;

public class BytecodeClassLoader extends URLClassLoader {

	public BytecodeClassLoader(Map<String, byte[]> bytecodes) {
		super(new URL[]{});
		initialize(bytecodes);
	}
	
	public BytecodeClassLoader(Map<String, byte[]> bytecodes, ClassLoader parentClassLoader) {
		super(new URL[]{}, parentClassLoader);
		initialize(bytecodes);
	}
	
	public BytecodeClassLoader(Map<String, byte[]> bytecodes, URL[] classpath) {
		super(classpath);
		initialize(bytecodes);
	}
	
	public BytecodeClassLoader(Map<String, byte[]> bytecodes, URL[] classpath, ClassLoader parentClassLoader) {
		super(classpath, parentClassLoader);
		initialize(bytecodes);
	}
	
	private void initialize(Map<String, byte[]> bytecodes) {
		this.bytecodes = MapLibrary.newHashMap(bytecodes);
	}
	
	@Override
	public Class<?> findClass(String qualifiedName) throws ClassNotFoundException {
		if (containsBytecodesFor(qualifiedName)) {
			byte[] bytes = bytecodesFor(qualifiedName);
			return defineClass(qualifiedName, bytes, 0, bytes.length);
		}
		return super.findClass(qualifiedName);
	}
	
	@Override
	public InputStream getResourceAsStream(String resourceName) {
	   if (resourceName.endsWith(".class")) {
         String qualifiedName = StringLibrary.stripEnd(resourceName, ".class").replace('/', '.');
         if (containsBytecodesFor(qualifiedName)) {
        	 return new ByteArrayInputStream(bytecodesFor(qualifiedName));
         }
      }
      	return super.getResourceAsStream(resourceName);
   	}

	private boolean containsBytecodesFor(String qualifiedName) {
		return bytecodes().containsKey(qualifiedName);
	}
	
	private byte[] bytecodesFor(String qualifiedName) {
		return bytecodes().get(qualifiedName);
	}
	
	private Map<String, byte[]> bytecodes() {
		return bytecodes;
	}
	
	private Map<String, byte[]> bytecodes;
}
