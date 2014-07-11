package fr.inria.lille.commons.suite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.Result;

public class TestSuiteExecutionTest {

	@Test
	public void runSuite() {
		Result result = TestSuiteExecution.runCasesIn(new String[]{"fr.inria.lille.commons.suite.SampleTestClass"}, getClass().getClassLoader());
		assertTrue(result.wasSuccessful());
		assertEquals(2, result.getRunCount());
		assertEquals(0, result.getFailureCount());
	}
	
	@Test
	public void runSingleTest() {
		TestCase testCase = new TestCase("fr.inria.lille.commons.suite.SampleTestClass", "joinTrue");
		Result result = TestSuiteExecution.runTestCase(testCase, getClass().getClassLoader());
		assertTrue(result.wasSuccessful());
		assertEquals(1, result.getRunCount());
		assertEquals(0, result.getFailureCount());
	}
	
}
