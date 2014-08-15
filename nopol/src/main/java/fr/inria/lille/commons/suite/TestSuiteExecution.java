package fr.inria.lille.commons.suite;

import static fr.inria.lille.commons.utils.library.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.utils.library.LoggerLibrary.newLoggerFor;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.utils.CustomContextClassLoaderThreadFactory;
import fr.inria.lille.commons.utils.Singleton;

public class TestSuiteExecution {
	
	public static Result runCasesIn(String[] testClasses, ClassLoader classLoaderForTestThread) {
		return runCasesIn(testClasses, classLoaderForTestThread, nullRunListener());
	}
	
	public static Result runCasesIn(String[] testClasses, ClassLoader classLoaderForTestThread, RunListener listener) {
		return executionResult(new JUnitRunner(testClasses, listener), classLoaderForTestThread);
	}
	
	public static Result runTestCase(TestCase testCase, ClassLoader classLoaderForTestThread) {
		return runTestCase(testCase, classLoaderForTestThread, nullRunListener());
	}
	
	public static Result runTestCase(TestCase testCase, ClassLoader classLoaderForTestThread, RunListener listener) {
		return executionResult(new JUnitSingleTestRunner(testCase, listener), classLoaderForTestThread);
	}

	private static Result executionResult(Callable<Result> callable, ClassLoader classLoaderForTestThread) {
		ExecutorService executor = Executors.newSingleThreadExecutor(new CustomContextClassLoaderThreadFactory(classLoaderForTestThread));
		Result result = null;
		try {
			result = executor.submit(callable).get(secondsForTimeout(), TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			logDebug(logger, String.format("Timeout after %d seconds. Infinite loop?", secondsForTimeout()));
		}
		executor.shutdownNow();
		return result;
	}
	
	public static List<Description> collectDescription(List<Failure> failures) {
		List<Description> descriptions = ListLibrary.newLinkedList();
		for (Failure failure : failures) {
			descriptions.add(failure.getDescription());
		}
		return descriptions;
	}
	
	protected static long secondsForTimeout() {
		return secondsForTimeout;
	}
	
	private static RunListener nullRunListener() {
		return Singleton.of(NullRunListener.class);
	}
	
	private static long secondsForTimeout = MINUTES.toSeconds(60L);
	private static Logger logger = newLoggerFor(TestSuiteExecution.class);
}