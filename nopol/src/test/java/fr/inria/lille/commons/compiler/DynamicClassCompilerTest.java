package fr.inria.lille.commons.compiler;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.tools.JavaFileObject.Kind;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import fr.inria.lille.commons.collections.MapLibrary;

public class DynamicClassCompilerTest {

	@Test
	public void helloWorldCompilation() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String qualifiedName = "test.dynamic.compiler.HelloWorld";
		String code = 
				"package test.dynamic.compiler;" +
				"public class HelloWorld {" +
				"	@Override" +
				"	public String toString() {" +
				"		return \"Hello World!\";" +
				"	}" + 
				"}";
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(qualifiedName, code);
		Class<?> newClass = loader.loadClass(qualifiedName);
		Object newInstance = newClass.newInstance();
		assertEquals("Hello World!", newInstance.toString());
	}
	
	@Test
	public void onceLoadedReturnTheSameObject() throws ClassNotFoundException {
		String qualifiedName = "test.dynamic.compiler.HelloWorld";
		String code = 
				"package test.dynamic.compiler;" +
				"public class HelloWorld {" +
				"	@Override" +
				"	public String toString() {" +
				"		return \"Hello World!\";" +
				"	}" + 
				"}";
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(qualifiedName, code);
		Class<?> newClass = loader.loadClass(qualifiedName);
		Class<?> sameClass = loader.loadClass(qualifiedName);
		assertTrue(newClass == sameClass);
	}
	
	@Test
	public void classWithDependencyCompilation() throws Exception {
		String qualifiedAbstractName = "test.dynamic.compiler.MyNumber";
		String qualifiedSubclassName = "test.dynamic.compiler.NumberTwelve";
		String abstractCode =
				"package test.dynamic.compiler;" +
				"public abstract class MyNumber {" +
				"	public abstract int id();" +
				"}";
		String subclassCode =
				"package test.dynamic.compiler;" +
				"public class NumberTwelve extends MyNumber {" +
				"	@Override" +
				"	public int id() {" +
				"		return 12;" +
				"	}" +
				"}";
		Map<String, String> sources = MapLibrary.newHashMap(asList(qualifiedAbstractName, qualifiedSubclassName), asList(abstractCode, subclassCode));
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(sources);
		Class<?> subclass = loader.loadClass(qualifiedSubclassName);
		Object newInstance = subclass.newInstance();
		assertEquals(12, subclass.getMethod("id").invoke(newInstance));
	}
	
	@Test
	public void innerClassCompilation() throws Exception {
		String qualifiedOuterName = "test.dynamic.compiler.Outer";
		String code =
				"package test.dynamic.compiler;" +
				"public class Outer {" +
				"	public class Inner {" +
				"		@Override" +
				"		public String toString() {" +
				"			return \"Hello from Inside!\";" +
				"		}" +
				"	}" +
				"}";
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(qualifiedOuterName, code);
		Class<?> outerClass = loader.loadClass(qualifiedOuterName);
		Class<?>[] classes = outerClass.getClasses();
		assertEquals(1, classes.length);
		Class<?> innerClass = classes[0];
		Constructor<?> constructor = innerClass.getDeclaredConstructor(outerClass);
		Object outerClassInstance = outerClass.newInstance();
		Object newInstance = constructor.newInstance(outerClassInstance);
		assertEquals("Hello from Inside!", newInstance.toString());
	}
	
	@Test
	public void classWithImportCompilation() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String qualifiedName = "test.dynamic.compiler.HelloWorld";
		String code =
				"package test.dynamic.compiler;" +
				"import java.awt.PageAttributes.MediaType;" +
				"public class HelloWorld {" +
				"	@Override" +
				"	public String toString() {" +
				"		return \"Hello World!\";" +
				"	}" +
				"}";
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(qualifiedName, code);
		Class<?> newClass = loader.loadClass(qualifiedName);
		Object newInstance = newClass.newInstance();
		assertEquals("Hello World!", newInstance.toString());
	}
	
	@Test
	public void accesPublicMethodFromDifferentClassloader() throws ClassNotFoundException {
		String qualifiedName = "test.dynamic.compiler.HelloWorld";
		String qualifiedTestName = "test.dynamic.compiler.HelloWorldTest";
		String code = 
				"package test.dynamic.compiler;" +
				"public class HelloWorld {" +
				"	@Override" +
				"	public String toString() {" +
				"		return \"Hello World!\";" +
				"	}" + 
				"}";
		String testCode = 
				"package test.dynamic.compiler;" +
				"import org.junit.Test;" +
				"import static org.junit.Assert.assertEquals;" +
				"public class HelloWorldTest {" +
				"	@Test" +
				"	public void toStringTest() {" +
				"		assertEquals(\"Hello World!\", new HelloWorld().toString());" +
				"	}" + 
				"}";
		ClassLoader parentLoader = BytecodeClassLoaderBuilder.loaderFor(qualifiedName, code);
		Map<String, String> sources = MapLibrary.newHashMap(asList(qualifiedName, qualifiedTestName), asList(code, testCode));
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(sources, parentLoader);
		Class<?> testClass = loader.loadClass(qualifiedTestName);
		Class<?> theClass = loader.loadClass(qualifiedName);
		assertFalse(parentLoader == loader);
		assertTrue(parentLoader == theClass.getClassLoader());
		assertTrue(loader == testClass.getClassLoader());
		JUnitCore junit = new JUnitCore();
		Request request = Request.method(testClass, "toStringTest");
		Result result = junit.run(request);
		assertTrue(result.wasSuccessful());
	}
	
	@Test
	public void accesProtectedMethodFromSameClassloaderAndPackage() throws ClassNotFoundException {
		String qualifiedName = "test.dynamic.compiler.HelloWorld";
		String qualifiedTestName = "test.dynamic.compiler.HelloWorldTest";
		String code = 
				"package test.dynamic.compiler;" +
				"public class HelloWorld {" +
				"	protected String message() {" +
				"		return \"Hello World!\";" +
				"	}" + 
				"}";
		String testCode = 
				"package test.dynamic.compiler;" +
				"import org.junit.Test;" +
				"import static org.junit.Assert.assertEquals;" +
				"public class HelloWorldTest {" +
				"	@Test" +
				"	public void protectedMethodTest() {" +
				"		assertEquals(\"Hello World!\", new HelloWorld().message());" +
				"	}" + 
				"}";
		Map<String, String> sources = MapLibrary.newHashMap(asList(qualifiedName, qualifiedTestName), asList(code, testCode));
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(sources);
		Class<?> testClass = loader.loadClass(qualifiedTestName);
		Class<?> theClass = loader.loadClass(qualifiedName);
		assertTrue(loader == theClass.getClassLoader());
		assertTrue(loader == testClass.getClassLoader());
		JUnitCore junit = new JUnitCore();
		Request request = Request.method(testClass, "protectedMethodTest");
		Result result = junit.run(request);
		assertTrue(result.wasSuccessful());
	}
	
	@Test
	public void accesProtectedMethodFromDifferentClassloaderButSamePackageName() throws ClassNotFoundException {
		String qualifiedName = "test.dynamic.compiler.HelloWorld";
		String qualifiedTestName = "test.dynamic.compiler.HelloWorldTest";
		String code = 
				"package test.dynamic.compiler;" +
				"public class HelloWorld {" +
				"	protected String message() {" +
				"		return \"Hello World!\";" +
				"	}" + 
				"}";
		String testCode = 
				"package test.dynamic.compiler;" +
				"import org.junit.Test;" +
				"import static org.junit.Assert.assertEquals;" +
				"public class HelloWorldTest {" +
				"	@Test" +
				"	public void protectedMethodTest() {" +
				"		assertEquals(\"Hello World!\", new HelloWorld().message());" +
				"	}" + 
				"}";
		Map<String, String> sources = MapLibrary.newHashMap(asList(qualifiedName, qualifiedTestName), asList(code, testCode));
		ClassLoader parentLoader = BytecodeClassLoaderBuilder.loaderFor(qualifiedName, code);
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(sources, parentLoader);
		Class<?> testClass = loader.loadClass(qualifiedTestName);
		Class<?> theClass = loader.loadClass(qualifiedName);
		assertFalse(parentLoader == loader);
		assertTrue(parentLoader == theClass.getClassLoader());
		assertTrue(loader == testClass.getClassLoader());
		JUnitCore junit = new JUnitCore();
		Request request = Request.method(testClass, "protectedMethodTest");
		Result result = junit.run(request);
		assertFalse(result.wasSuccessful());
		assertEquals(1, result.getFailureCount());
		Failure failure = result.getFailures().get(0);
		assertEquals("java.lang.IllegalAccessError",failure.getException().getClass().getName());
	}
	
	@Test
	public void customClassLoaderInThread() throws ClassNotFoundException, InterruptedException, ExecutionException, TimeoutException {
		final String qualifiedName = "test.dynamic.compiler.ListFactory";
		String code =
				"package test.dynamic.compiler;" +
				"import java.util.List;" +
				"import java.util.LinkedList;" +
				"public class ListFactory {" +
				"	public List<String> getList() {" +
				"		return new LinkedList<String>();" +
				"	}" +
				"}";
		final ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(qualifiedName, code);

		ThreadFactory normalFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r);
			}
		};
		
		ThreadFactory factoryWithClassLoader = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread newThread = new Thread(r);
				newThread.setContextClassLoader(loader);
				return newThread;
			}
		};
		
		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() {
				try {
					Thread currentThread = Thread.currentThread();
					Class<?> dynamicClass = currentThread.getContextClassLoader().loadClass(qualifiedName);
					assertFalse(loader == currentThread.getClass().getClassLoader());
					assertTrue(loader == dynamicClass.getClassLoader());
					Object object = dynamicClass.newInstance();
					assertEquals(qualifiedName, object.getClass().getName());
					Object invocation = dynamicClass.getMethod("getList").invoke(object);
					assertEquals("LinkedList", invocation.getClass().getSimpleName());
				} catch (ClassNotFoundException cnfe) {
					return "ClassNotFoundException";
				} catch (InstantiationException e) {
					return "InstantiationException";
				} catch (IllegalAccessException e) {
					return "IllegalAccessException";
				} catch (IllegalArgumentException e) {
					return "IllegalArgumentException";
				} catch (InvocationTargetException e) {
					return "InvocationTargetException";
				} catch (NoSuchMethodException e) {
					return "NoSuchMethodException";
				} catch (SecurityException e) {
					return "SecurityException";
				}
				return "NoException";
			}
		};
		ExecutorService executorThrowsException = Executors.newSingleThreadExecutor(normalFactory);
		String result = executorThrowsException.submit(callable).get(10L, TimeUnit.MINUTES);
		assertEquals("ClassNotFoundException", result);
		ExecutorService executorSuceeds = Executors.newSingleThreadExecutor(factoryWithClassLoader);
		result = executorSuceeds.submit(callable).get(10L, TimeUnit.MINUTES);
		assertEquals("NoException", result);
	}
	
	@Test
	public void virtualDependencyAddedToFileManagerBeforeCompilation() throws Exception {
		DynamicClassCompiler dependencyCompiler = new DynamicClassCompiler();
		String dependencyQualifiedName ="test.dynamic.compiler.dependency.Echo";
		String dependencyCode =
				"package test.dynamic.compiler.dependency;" +
				"public class Echo {" +
				"	public static String echo(String message) {" +
				"		return \"ECHO \" + message;" +
				"	}" +
				"}";
		
		DynamicClassCompiler clientCompiler = new DynamicClassCompiler();
		String clientQualifiedName = "test.dynamic.compiler.client.Client";
		String clientCode =
				"package test.dynamic.compiler.client;" +
				"import static test.dynamic.compiler.dependency.Echo.*;" +
				"public class Client {" +
				"	@Override" + 
				"	public String toString() {" +
				"		return echo(\"response\");" +
				"	}" +
				"}";
		
		/** Compile Dependency; Create Class File and write bytes; Add it to the Client File Manager */	
		byte[] dependencyCompilation = dependencyCompiler.javaBytecodeFor(dependencyQualifiedName, dependencyCode);
		VirtualClassFileObject dependencyClassFile = new VirtualClassFileObject(dependencyQualifiedName, Kind.CLASS);
		dependencyClassFile.openOutputStream().write(dependencyCompilation);
		clientCompiler.fileManager().classFiles().put(dependencyQualifiedName, dependencyClassFile);
		
		/** Compilation of Client using Dependency works, because the Client File Manager finds the Dependency */
		Map<String, String> sourceToCompile = MapLibrary.newHashMap(clientQualifiedName, clientCode);
		Map<String, byte[]> clientCompilation = clientCompiler.javaBytecodeFor(sourceToCompile);
		BytecodeClassLoader loader = BytecodeClassLoaderBuilder.loaderWith(clientCompilation);
		Class<?> clientClass = loader.loadClass(clientQualifiedName);
		Object clientInstance = clientClass.newInstance();
		assertEquals("ECHO response", clientInstance.toString());
	}
	
}
