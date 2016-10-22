package xxl.java.junit;

import fr.inria.lille.localization.TestResult;
import fr.inria.lille.repair.common.config.Config;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import xxl.java.container.classic.MetaList;
import xxl.java.support.Singleton;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static xxl.java.library.LoggerLibrary.logDebug;
import static xxl.java.library.LoggerLibrary.loggerFor;

public class TestSuiteExecution {

    public static Result runCasesIn(String[] testClasses, ClassLoader classLoaderForTestThread, Config config) {
        return runCasesIn(testClasses, classLoaderForTestThread, nullRunListener(), config);
    }

    public static Result runCasesIn(String[] testClasses, ClassLoader classLoaderForTestThread, RunListener listener, Config config) {
        return executionResult(new JUnitRunner(testClasses, listener), classLoaderForTestThread, config);
    }

    public static Result runTestCase(TestCase testCase, ClassLoader classLoaderForTestThread, Config config) {
        return runTestCase(testCase, classLoaderForTestThread, nullRunListener(), config);
    }

    public static Result runTestCase(TestCase testCase, ClassLoader classLoaderForTestThread, RunListener listener, Config config) {
        return executionResult(new JUnitSingleTestRunner(testCase, listener), classLoaderForTestThread, config);
    }

    public static Result runTest(String test, ClassLoader classLoaderForTestThread, RunListener listener, Config config) {
        return executionResult(new JUnitSingleTestResultRunner(test, listener), classLoaderForTestThread, config);
    }

    public static Result runTestCase(TestResult testCase, ClassLoader classLoaderForTestThread, RunListener listener, Config config) {
        return executionResult(new JUnitSingleTestResultRunner(testCase.getTestCase().toString(), listener), classLoaderForTestThread, config);
    }

    public static CompoundResult runTestCases(Collection<TestCase> testCases, ClassLoader classLoaderForTestThread, Config config) {
        return runTestCases(testCases, classLoaderForTestThread, nullRunListener(), config);
    }

    public static CompoundResult runTestResult(Collection<TestResult> testCases, ClassLoader classLoaderForTestThread, Config config) {
        return runTestResult(testCases, classLoaderForTestThread, nullRunListener(), config);
    }

    public static Result runTest(String[] testClasses, ClassLoader loader, Config config) {
        return runTest(testClasses, loader, nullRunListener(), config);
    }

    public static Result runTest(String[] testClasses, ClassLoader classLoaderForTestThread, RunListener listener, Config config) {
        List<Result> results = MetaList.newArrayList(testClasses.length);
        for (String testCase : testClasses) {
            results.add(runTest(testCase, classLoaderForTestThread, listener, config));
        }
        return new CompoundResult(results);
    }

    public static CompoundResult runTestResult(Collection<TestResult> testCases, ClassLoader classLoaderForTestThread, RunListener listener, Config config) {
        List<Result> results = MetaList.newArrayList(testCases.size());
        for (TestResult testCase : testCases) {
            if (testCase.getTestCase().className().startsWith("junit.")) {
                continue;
            }
            results.add(runTestCase(testCase, classLoaderForTestThread, listener, config));
        }
        return new CompoundResult(results);
    }

    public static CompoundResult runTestCases(Collection<TestCase> testCases, ClassLoader classLoaderForTestThread, RunListener listener, Config config) {
        List<Result> results = MetaList.newArrayList(testCases.size());
        for (TestCase testCase : testCases) {
            results.add(runTestCase(testCase, classLoaderForTestThread, listener, config));
        }
        return new CompoundResult(results);
    }

    private static Result executionResult(Callable<Result> callable, ClassLoader classLoaderForTestThread, Config config) {
        ExecutorService executor = Executors.newSingleThreadExecutor(new CustomClassLoaderThreadFactory(classLoaderForTestThread));
        Result result = null;
        try {
            result = executor.submit(callable).get(config.getTimeoutTestExecution(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            logDebug(logger(), String.format("Timeout after %d seconds. Infinite loop?", config.getTimeoutTestExecution()));
            throw new RuntimeException(e);
        } finally {
            executor.shutdownNow();
        }
        return result;
    }

    public static List<Description> collectDescription(List<Failure> failures) {
        List<Description> descriptions = MetaList.newLinkedList();
        for (Failure failure : failures) {
            descriptions.add(failure.getDescription());
        }
        return descriptions;
    }

    private static RunListener nullRunListener() {
        return Singleton.of(NullRunListener.class);
    }

    private static Logger logger() {
        return loggerFor(TestSuiteExecution.class);
    }

}