package fr.inria.lille.commons.compiler;

import java.net.URL;
import java.util.Map;

import fr.inria.lille.commons.collections.MapLibrary;

public class BytecodeClassLoaderBuilder {

	public static BytecodeClassLoader loaderFor(String qualifiedName, String sourceContent) {
		Map<String, byte[]> bytecodes = bytecodes(qualifiedName, sourceContent);
		return new BytecodeClassLoader(bytecodes);
	}
	
	public static BytecodeClassLoader loaderFor(String qualifiedName, String sourceContent, URL[] classpath) {
		Map<String, byte[]> bytecodes = bytecodes(qualifiedName, sourceContent);
		return new BytecodeClassLoader(bytecodes, classpath);
	}
	
	public static BytecodeClassLoader loaderFor(String qualifiedName, String sourceContent, ClassLoader parentClassLoader) {
		Map<String, byte[]> bytecodes = bytecodes(qualifiedName, sourceContent);
		return new BytecodeClassLoader(bytecodes, parentClassLoader);
	}
	
	public static BytecodeClassLoader loaderFor(Map<String, String> qualifiedNameAndContent) {
		Map<String, byte[]> bytecodes = bytecodes(qualifiedNameAndContent);
		return new BytecodeClassLoader(bytecodes);
	}
	
	public static BytecodeClassLoader loaderFor(Map<String, String> qualifiedNameAndContent, URL[] classpath) {
		Map<String, byte[]> bytecodes = bytecodes(qualifiedNameAndContent);
		return new BytecodeClassLoader(bytecodes, classpath);
	}
	
	public static BytecodeClassLoader loaderFor(Map<String, String> qualifiedNameAndContent, ClassLoader parentClassLoader) {
		Map<String, byte[]> bytecodes = bytecodes(qualifiedNameAndContent);
		return new BytecodeClassLoader(bytecodes, parentClassLoader);
	}
	
	private static Map<String, byte[]> bytecodes(String qualifiedName, String sourceContent) {
		Map<String, String> sources = MapLibrary.newHashMap(qualifiedName, sourceContent);
		return bytecodes(sources);
	}
	
	private static Map<String, byte[]> bytecodes(Map<String, String> qualifiedNameAndContent) {
		DynamicClassCompiler compiler = new DynamicClassCompiler();
		return compiler.javaBytecodeFor(qualifiedNameAndContent);
	}
}
