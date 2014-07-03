package fr.inria.lille.commons.compiler;

import static fr.inria.lille.commons.string.StringLibrary.javaNewline;
import static fr.inria.lille.commons.string.StringLibrary.join;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.inria.lille.commons.collections.MapLibrary;

public class DynamicClassCompilerTest {

	@Test
	public void helloWorldCompilation() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String packageName = "test.dynamic.compiler";
		String simpleClassName = "HelloWorld";
		String message = "Hello world!";
		String qualifiedName = packageName + "." + simpleClassName;
		String sourceCode = simpleClassTemplate().replace("%%package%%", packageName).replace("%%class%%", simpleClassName).replace("%%message%%", message);
		DynamicClassCompiler compiler = new DynamicClassCompiler();
		
		checkThrownClassNotFoundExcpetion(true, ClassLoader.getSystemClassLoader(), qualifiedName);
		checkThrownClassNotFoundExcpetion(true, compiler.dynamicClassLoader(), qualifiedName);
		Class<?> newClass = compiler.compileSource(qualifiedName, sourceCode);
		checkThrownClassNotFoundExcpetion(true, ClassLoader.getSystemClassLoader(), qualifiedName);
		checkThrownClassNotFoundExcpetion(false, compiler.dynamicClassLoader(), qualifiedName);
		
		Object newInstance = newClass.newInstance();
		assertEquals(message, newInstance.toString());
	}
	
	@Test
	public void classWithDependencyCompilation() throws Exception {
		String packageName = "test.dynamic.compiler";
		String abstractClassName = "MyNumber";
		String simpleClassName = "NumberTwelve";
		String methodName = "id";
		String qualifiedAbstractName = packageName + "." + abstractClassName;
		String qualifiedSubclassName = packageName + "." + simpleClassName;
		Integer value = 12;
		String abstractCode = abstractClassTemplate().replace("%%package%%", packageName).replace("%%class%%", abstractClassName).replace("%%method%%", methodName);
		String subclassCode = subclassTemplate().replace("%%package%%", packageName).replace("%%class%%", simpleClassName).
								replace("%%superclass%%", abstractClassName).replace("%%method%%", methodName).replace("%%value%%", value.toString());
		DynamicClassCompiler compiler = new DynamicClassCompiler();
		
		checkThrownClassNotFoundExcpetion(true, ClassLoader.getSystemClassLoader(), qualifiedAbstractName);
		checkThrownClassNotFoundExcpetion(true, ClassLoader.getSystemClassLoader(), qualifiedSubclassName);
		checkThrownClassNotFoundExcpetion(true, compiler.dynamicClassLoader(), qualifiedAbstractName);
		checkThrownClassNotFoundExcpetion(true, compiler.dynamicClassLoader(), qualifiedSubclassName);
		
		Map<String, String> toBeCompiled = MapLibrary.newHashMap(asList(qualifiedAbstractName, qualifiedSubclassName), asList(abstractCode, subclassCode));
		Map<String, Class<?>> compiled = compiler.compileSources(toBeCompiled);
		
		checkThrownClassNotFoundExcpetion(true, ClassLoader.getSystemClassLoader(), qualifiedAbstractName);
		checkThrownClassNotFoundExcpetion(true, ClassLoader.getSystemClassLoader(), qualifiedSubclassName);
		checkThrownClassNotFoundExcpetion(false, compiler.dynamicClassLoader(), qualifiedAbstractName);
		checkThrownClassNotFoundExcpetion(false, compiler.dynamicClassLoader(), qualifiedSubclassName);

		Class<?> subclass = compiled.get(qualifiedSubclassName);
		Object newInstance = subclass.newInstance();
		assertEquals(value, subclass.getMethod(methodName).invoke(newInstance));
	}
	
	private void checkThrownClassNotFoundExcpetion(boolean shouldBeThrown, ClassLoader classLoader, String classQualifiedName) {
		boolean classNotFoundExceptionThrown = false;
		try {
			classLoader.loadClass(classQualifiedName);
		}
		catch (ClassNotFoundException cnfe) {
			classNotFoundExceptionThrown = true;
		}
		assertEquals(shouldBeThrown, classNotFoundExceptionThrown);
	}
	
	private String simpleClassTemplate() {
		List<String> lines = asList("package %%package%%;", "public class %%class%% {", "@Override", "public String toString() {", "return \"%%message%%\";", "}", "}");
		return join(lines, javaNewline());
	}
	
	private String abstractClassTemplate() {
		List<String> lines = asList("package %%package%%;", "public abstract class %%class%% {", "public abstract int %%method%%();", "}");
		return join(lines, javaNewline());
	}
	
	private String subclassTemplate() {
		List<String> lines = asList("package %%package%%;", "public class %%class%% extends %%superclass%% {", "@Override", "public int %%method%%() {", "return %%value%%;", "}", "}");
		return join(lines, javaNewline());
	}
}
