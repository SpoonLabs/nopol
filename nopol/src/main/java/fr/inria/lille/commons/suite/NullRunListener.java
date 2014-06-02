package fr.inria.lille.commons.suite;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class NullRunListener extends RunListener {

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
    }
    
    @Override
    public void testIgnored(Description description) throws Exception {
        super.testIgnored(description);
    }
    
    @Override
    public void testStarted(Description description) throws Exception {
        super.testStarted(description);
    }
	
    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
    }
    
    @Override
    public void testAssumptionFailure(Failure failure) {
        super.testAssumptionFailure(failure);
    }
    
    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
    }
    
    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
    }
}
