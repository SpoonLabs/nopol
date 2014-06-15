package fr.inria.lille.commons.suite;

import java.util.Collection;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import fr.inria.lille.commons.collections.SetLibrary;

public class TestCasesListener extends RunListener {

	public TestCasesListener() {
		testCases = SetLibrary.newHashSet();
		failedTests = SetLibrary.newHashSet();
	}
	
    @Override
    public void testFailure(Failure failure) throws Exception {
        Description description = failure.getDescription();
        failedTests().add(testCaseOf(description));
    }
	
    @Override
    public void testFinished(Description description) throws Exception {
        TestCase testCase = testCaseOf(description);
		allTests().add(testCase);
		if (! failedTests().contains(testCase)) {
			processSuccessfulRun(testCase);
		} else {
			processFailedRun(testCase);
		}
    }
    
	protected void processSuccessfulRun(TestCase testCase) {
		/* subclassResponsibility */
	}
	
    protected void processFailedRun(TestCase testCase) {
    	/* subclassResponsibility */
	}

	protected TestCase testCaseOf(Description description) {
    	return new TestCase(description.getClassName(), description.getMethodName());
    }
    
    public Collection<TestCase> allTests() {
    	return testCases;
    }
    
    public Collection<TestCase> successfulTests() {
    	Collection<TestCase> successfulTests = SetLibrary.newHashSet(allTests());
    	successfulTests.removeAll(failedTests());
    	return successfulTests;
    }
    
    public Collection<TestCase> failedTests() {
    	return failedTests;
    }
    
    private Collection<TestCase> testCases;
    private Collection<TestCase> failedTests;
}