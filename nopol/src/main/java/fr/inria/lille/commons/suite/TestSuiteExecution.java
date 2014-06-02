package fr.inria.lille.commons.suite;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.lille.commons.classes.CacheBasedClassLoader;
import fr.inria.lille.commons.classes.ProvidedClassLoaderThreadFactory;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.MapLibrary;

public class TestSuiteExecution {

	public static Result runCasesIn(Collection<String> testClasses, Collection<URL> classpath) {
		return runCasesIn(testClasses, classpath, (Map) MapLibrary.newHashMap());
	}
	
	public static Result runCasesIn(Collection<String> testClasses, Collection<URL> classpath, Map<String, Class<?>> classcache) {
		String[] testClassesArray = CollectionLibrary.toArray(String.class, testClasses);
		URL[] classpathArray = CollectionLibrary.toArray(URL.class, classpath);
		return runCasesIn(testClassesArray, classpathArray, classcache, nullListener());
	}
	
	public static Result runCasesIn(String[] testClasses, URL[] classpath) {
		return runCasesIn(testClasses, classpath, (Map) MapLibrary.newHashMap(), nullListener());
	}
	
	public static Result runCasesIn(String[] testClasses, URL[] classpath, Map<String, Class<?>> classcache) {
		return runCasesIn(testClasses, classpath, classcache, nullListener());
	}
	
	public static Result runCasesIn(String[] testClasses, URL[] classpath, Map<String, Class<?>> classcache, RunListener listener) {
		ClassLoader classloader = new CacheBasedClassLoader(classpath, classcache);
		ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(classloader));
		Result result = null;
		try {
			result = executor.submit(new JUnitRunner(testClasses, listener)).get(secondsForTimeout(), TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			log(String.format("Timeout after %d seconds. Infinite loop?", secondsForTimeout()));
		}
		executor.shutdown();
		return result;
	}

	protected static RunListener nullListener() {
		return nullListener;
	}
	
	protected static long secondsForTimeout() {
		return secondsForTimeout;
	}
	
	protected static void log(String message) {
		logger.warn(message);
	}
	
	private static RunListener nullListener = new NullRunListener();
	private static long secondsForTimeout = MINUTES.toSeconds(5L);
	private static Logger logger = LoggerFactory.getLogger(TestSuiteExecution.class);
}