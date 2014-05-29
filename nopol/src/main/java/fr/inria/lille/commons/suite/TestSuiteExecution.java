package fr.inria.lille.commons.suite;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.runner.Result;

import fr.inria.lille.commons.classes.CacheBasedClassLoader;
import fr.inria.lille.commons.classes.ProvidedClassLoaderThreadFactory;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.MapLibrary;

public class TestSuiteExecution {

	public static boolean runCasesIn(Collection<String> testClasses, Collection<URL> classpath) {
		return runCasesIn(testClasses, classpath, (Map) MapLibrary.newHashMap());
	}
	
	public static boolean runCasesIn(String[] testClasses, URL[] classpath) {
		return runCasesIn(testClasses, classpath, (Map) MapLibrary.newHashMap());
	}
	
	public static boolean runCasesIn(Collection<String> testClasses, Collection<URL> classpath, Map<String, Class<?>> classcache) {
		String[] testClassesArray = CollectionLibrary.toArray(String.class, testClasses);
		URL[] classpathArray = CollectionLibrary.toArray(URL.class, classpath);
		return runCasesIn(testClassesArray, classpathArray, classcache);
	}
	
	public static boolean runCasesIn(String[] testClasses, URL[] classpath, Map<String, Class<?>> classcache) {
		ClassLoader classloader = new CacheBasedClassLoader(classpath, classcache);
		ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(classloader));
		Result result;
		try {
			result = executor.submit(new JUnitRunner(testClasses)).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		executor.shutdown();
		return result.wasSuccessful();
	}

}