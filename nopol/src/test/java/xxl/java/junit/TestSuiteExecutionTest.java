package xxl.java.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.Result;

public class TestSuiteExecutionTest {

	@Test
	public void runSuite() {
		Result result = TestSuiteExecution.runCasesIn(new String[]{ SampleTestClass.class.getName() }, getClass().getClassLoader());
		assertTrue(result.wasSuccessful());
		assertEquals(2, result.getRunCount());
		assertEquals(0, result.getFailureCount());
	}
	
	@Test
	public void runSingleTest() {
		TestCase testCase = TestCase.from(SampleTestClass.class.getName(), "joinTrue");
		Result result = TestSuiteExecution.runTestCase(testCase, getClass().getClassLoader());
		assertTrue(result.wasSuccessful());
		assertEquals(1, result.getRunCount());
		assertEquals(0, result.getFailureCount());
	}
	
	@Test
	public void runSuiteWithTestListener() {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(new String[]{ SampleTestClass.class.getName() }, getClass().getClassLoader(), listener);
		assertEquals(2, listener.allTests().size());
		assertEquals(2, listener.successfulTests().size());
		assertEquals(0, listener.failedTests().size());
	}
}
