package fr.inria.lille.commons.suite;

import static fr.inria.lille.commons.string.StringLibrary.javaNewline;
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
import org.slf4j.LoggerFactory;

import fr.inria.lille.commons.classes.CustomContextClassLoaderThreadFactory;
import fr.inria.lille.commons.collections.ListLibrary;

public class TestSuiteExecution {
	
	public static Result runCasesIn(String[] testClasses, ClassLoader classLoaderForTestThread) {
		return runCasesIn(testClasses, classLoaderForTestThread, NullRunListener.instance());
	}
	
	public static Result runCasesIn(String[] testClasses, ClassLoader classLoaderForTestThread, RunListener listener) {
		return executionResult(new JUnitRunner(testClasses, listener), classLoaderForTestThread);
	}
	
	public static Result runTestCase(TestCase testCase, ClassLoader classLoaderForTestThread) {
		return runTestCase(testCase, classLoaderForTestThread, NullRunListener.instance());
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
			log(String.format("Timeout after %d seconds. Infinite loop?", secondsForTimeout()));
		}
		logTestRunFinished(result);
		executor.shutdown();
		return result;
	}
	
	public static List<Description> collectDescription(List<Failure> failures) {
		List<Description> descriptions = ListLibrary.newLinkedList();
		for (Failure failure : failures) {
			descriptions.add(failure.getDescription());
		}
		return descriptions;
	}
	
	private static void logTestRunFinished(Result result) {
		StringBuilder builder = new StringBuilder();
		String endl = javaNewline();
		builder.append("Tests run finished" + endl);
		builder.append("~ Total tests run: " + result.getRunCount() + endl);
		builder.append("~ Ignored tests: " + result.getIgnoreCount() + endl);
		builder.append("~ Failed tests: " + result.getFailureCount() + endl);
		for (Failure failure : result.getFailures()) {
			builder.append("~ " + failure.getTestHeader() + endl);
			builder.append("[" + failure.getMessage() + "]" + endl);
			Throwable exception = failure.getException();
			builder.append(exception.toString() + endl);
			for (int i = 0; i <= 5; i += 1) {
				StackTraceElement element = exception.getStackTrace()[i];
				builder.append("    at " + element.toString() + endl);
			}
		}
		log(builder.toString());
	}
	
	protected static long secondsForTimeout() {
		return secondsForTimeout;
	}
	
	protected static void log(String message) {
		logger.warn(message);
	}
	
	private static long secondsForTimeout = MINUTES.toSeconds(60L);
	private static Logger logger = LoggerFactory.getLogger(TestSuiteExecution.class);
}