package xxl.java.junit;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class NullRunListener extends RunListener {

    public static NullRunListener firstInstance() {
        /* Refer to: Singleton#createSingleton() */
        return new NullRunListener();
    }

    protected NullRunListener() {
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
    }

    @Override
    public void testIgnored(Description description) throws Exception {
    }

    @Override
    public void testStarted(Description description) throws Exception {
    }

    @Override
    public void testFinished(Description description) throws Exception {
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
    }
}
