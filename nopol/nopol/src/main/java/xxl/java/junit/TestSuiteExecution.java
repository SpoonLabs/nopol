package xxl.java.junit;

import fr.inria.lille.localization.TestResult;
import fr.inria.lille.repair.common.config.NopolContext;
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

    public static Result runCasesIn(String[] testClasses, ClassLoader classLoaderForTestThread, NopolContext nopolContext) {
        return runCasesIn(testClasses, classLoaderForTestThread, nullRunListener(), nopolContext);
    }

    public static Result runCasesIn(String[] testClasses, ClassLoader classLoaderForTestThread, RunListener listener, NopolContext nopolContext) {
        return executionResult(new JUnitRunner(testClasses, listener), classLoaderForTestThread, nopolContext);
    }

    public static Result runTestCase(TestCase testCase, ClassLoader classLoaderForTestThread, NopolContext nopolContext) {
        return runTestCase(testCase, classLoaderForTestThread, nullRunListener(), nopolContext);
    }

    public static Result runTestCase(TestCase testCase, ClassLoader classLoaderForTestThread, RunListener listener, NopolContext nopolContext) {
        return executionResult(new JUnitSingleTestRunner(testCase, listener), classLoaderForTestThread, nopolContext);
    }

    public static Result runTest(String test, ClassLoader classLoaderForTestThread, RunListener listener, NopolContext nopolContext) {
        return executionResult(new JUnitSingleTestResultRunner(test, listener), classLoaderForTestThread, nopolContext);
    }

    public static Result runTestCase(TestResult testCase, ClassLoader classLoaderForTestThread, RunListener listener, NopolContext nopolContext) {
        return executionResult(new JUnitSingleTestResultRunner(testCase.getTestCase().toString(), listener), classLoaderForTestThread, nopolContext);
    }

    public static CompoundResult runTestCases(Collection<TestCase> testCases, ClassLoader classLoaderForTestThread, NopolContext nopolContext) {
        return runTestCases(testCases, classLoaderForTestThread, nullRunListener(), nopolContext);
    }

    public static CompoundResult runTestResult(Collection<TestResult> testCases, ClassLoader classLoaderForTestThread, NopolContext nopolContext) {
        return runTestResult(testCases, classLoaderForTestThread, nullRunListener(), nopolContext);
    }

    public static Result runTest(String[] testClasses, ClassLoader loader, NopolContext nopolContext) {
        return runTest(testClasses, loader, nullRunListener(), nopolContext);
    }

    public static Result runTest(String[] testClasses, ClassLoader classLoaderForTestThread, RunListener listener, NopolContext nopolContext) {
        List<Result> results = MetaList.newArrayList(testClasses.length);
        for (String testCase : testClasses) {
            results.add(runTest(testCase, classLoaderForTestThread, listener, nopolContext));
        }
        return new CompoundResult(results);
    }

    public static CompoundResult runTestResult(Collection<TestResult> testCases, ClassLoader classLoaderForTestThread, RunListener listener, NopolContext nopolContext) {
        List<Result> results = MetaList.newArrayList(testCases.size());
        for (TestResult testCase : testCases) {
            if (testCase.getTestCase().className().startsWith("junit.")) {
                continue;
            }
            String completeTestName = testCase.getTestCase().className()+"#"+testCase.getTestCase().testName();
            if (!nopolContext.getTestMethodsToIgnore().contains(completeTestName)) {
                results.add(runTestCase(testCase, classLoaderForTestThread, listener, nopolContext));
            }
        }
        return new CompoundResult(results);
    }

    public static CompoundResult runTestCases(Collection<TestCase> testCases, ClassLoader classLoaderForTestThread, RunListener listener, NopolContext nopolContext) {
        List<Result> results = MetaList.newArrayList(testCases.size());
        for (TestCase testCase : testCases) {
            String completeTestName = testCase.className()+"#"+testCase.testName();
            if (!nopolContext.getTestMethodsToIgnore().contains(completeTestName)) {
                results.add(runTestCase(testCase, classLoaderForTestThread, listener, nopolContext));
            }

        }
        return new CompoundResult(results);
    }

    private static Result executionResult(Callable<Result> callable, ClassLoader classLoaderForTestThread, NopolContext nopolContext) {
        ExecutorService executor = Executors.newSingleThreadExecutor(new CustomClassLoaderThreadFactory(classLoaderForTestThread));
        Result result = null;

        Future<Result> future = executor.submit(callable);
        try {
            executor.shutdown();
            result = future.get(nopolContext.getTimeoutTestExecution(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            logDebug(logger(), String.format("Timeout after %d seconds. Infinite loop?", nopolContext.getTimeoutTestExecution()));
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