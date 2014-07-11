package fr.inria.lille.commons.suite;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;
import static fr.inria.lille.commons.string.StringLibrary.javaNewline;
import static java.lang.String.format;

import java.util.Collection;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;

import fr.inria.lille.commons.collections.SetLibrary;


public class TestCasesListener extends RunListener {

	public TestCasesListener() {
		testCases = SetLibrary.newHashSet();
		failedTests = SetLibrary.newHashSet();
	}

	@Override
    public void testRunStarted(Description description) throws Exception {
		processBeforeRun();
	}
	
	@Override
    public void testStarted(Description description) throws Exception {
		TestCase testCase = addTestCaseTo(allTests(), description);
		logDebug(logger, format("[#%d. %s started...]", numberOfTests(), testCase.toString()));
	}
	
    @Override
    public void testFailure(Failure failure) throws Exception {
        Description description = failure.getDescription();
        addTestCaseTo(failedTests(), description);
    }
	
    @Override
    public void testFinished(Description description) throws Exception {
    	TestCase testCase = testCaseOf(description);
		if (failedTests().contains(testCase)) {
			logDebug(logger, format("[#%d. FAILED]", numberOfTests()));
			processFailedRun(testCase);
		} else {
			logDebug(logger, format("[#%d. SUCCESS]", numberOfTests()));
			processSuccessfulRun(testCase);
		}
    }
    
    @Override
    public void testRunFinished(Result result) throws Exception {
		logTestRunFinished(result);
		processAfterRun();
    }

    protected void processBeforeRun() {
    	/* subclassResponsibility */
    }
    
	protected void processSuccessfulRun(TestCase testCase) {
		/* subclassResponsibility */
	}
	
    protected void processFailedRun(TestCase testCase) {
    	/* subclassResponsibility */
	}
    
    protected void processAfterRun() {
    	/* subclassResponsibility */
    }
    
    public int numberOfTests() {
    	return allTests().size();
    }

    public int numberOfFailedTests() {
    	return failedTests().size();
    }
    
    public Collection<TestCase> successfulTests() {
    	Collection<TestCase> successfulTests = SetLibrary.newHashSet(allTests());
    	successfulTests.removeAll(failedTests());
    	return successfulTests;
    }
    
    public Collection<TestCase> allTests() {
    	return testCases;
    }
    
    public Collection<TestCase> failedTests() {
    	return failedTests;
    }
    
	protected TestCase testCaseOf(Description description) {
    	return new TestCase(description.getClassName(), description.getMethodName());
    }
	
	private TestCase addTestCaseTo(Collection<TestCase> collection, Description description) {
		TestCase testCase = testCaseOf(description);
		collection.add(testCase);
		return testCase;
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
		logDebug(logger, builder.toString());
	}

    private Collection<TestCase> testCases;
    private Collection<TestCase> failedTests;
    private static Logger logger = newLoggerFor(TestCasesListener.class);
}