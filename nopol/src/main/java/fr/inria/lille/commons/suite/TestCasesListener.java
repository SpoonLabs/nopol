package fr.inria.lille.commons.suite;

import static java.lang.String.format;

import java.util.Collection;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.lille.commons.collections.SetLibrary;


public class TestCasesListener extends RunListener {

	public TestCasesListener() {
		testCases = SetLibrary.newHashSet();
		failedTests = SetLibrary.newHashSet();
	}
	
	@Override
    public void testStarted(Description description) throws Exception {
		TestCase testCase = addTestCaseTo(allTests(), description);
		log(format("[#%d. %s started...]", numberOfTests(), testCase.toString()));
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
			log(format("[#%d. FAILED]", numberOfTests()));
			processFailedRun(testCase);
		} else {
			log(format("[#%d. SUCCESS]", numberOfTests()));
			processSuccessfulRun(testCase);
		}
    }
    
	protected void processSuccessfulRun(TestCase testCase) {
		/* subclassResponsibility */
	}
	
    protected void processFailedRun(TestCase testCase) {
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
    
    private static void log(String message) {
    	logger.debug(message);
    }
    
    private Collection<TestCase> testCases;
    private Collection<TestCase> failedTests;
    private static Logger logger = LoggerFactory.getLogger(TestCasesListener.class);
}