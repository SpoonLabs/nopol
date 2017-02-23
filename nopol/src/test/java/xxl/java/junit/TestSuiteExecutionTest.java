package xxl.java.junit;

import fr.inria.lille.repair.common.config.Config;
import org.junit.Test;
import org.junit.runner.Result;
import xxl.java.library.FileLibrary;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import static org.junit.Assert.*;

public class TestSuiteExecutionTest {

	@Test
	public void runSuite() {
		Config config = new Config();
		config.setTimeoutTestExecution(10);
		Result result = TestSuiteExecution.runCasesIn(new String[]{ sampleTestClass() }, classLoaderWithTestClass(), config);
		assertFalse(result.wasSuccessful());
		assertTrue(3 == result.getRunCount());
		assertTrue(1 == result.getFailureCount());
	}
	
	@Test
	public void runSingleTest() {
		Config config = new Config();
		config.setTimeoutTestExecution(10);
		TestCase testCase = TestCase.from(sampleTestClass(), "joinTrue");
		Result result = TestSuiteExecution.runTestCase(testCase, classLoaderWithTestClass(), config);
		assertTrue(result.wasSuccessful());
		assertEquals(1, result.getRunCount());
		assertEquals(0, result.getFailureCount());
	}
	
	@Test
	public void runSuiteWithTestListener() {
		Config config = new Config();
		config.setTimeoutTestExecution(10);
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(new String[]{ sampleTestClass() }, classLoaderWithTestClass(), listener, config);
		assertEquals(3, listener.allTests().size());
		assertEquals(2, listener.successfulTests().size());
		assertEquals(1, listener.failedTests().size());
	}
	
	@Test
	public void doNotUseSameTestNameTwice() {

		/* According to the name, we do not run twice the same test, i.e. test with same name */

		Config config = new Config();
		config.setTimeoutTestExecution(10);
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(new String[]{ sampleTestClass(), sampleTestClass() }, classLoaderWithTestClass(), listener, config);
		assertEquals(3, listener.allTests().size());
		assertEquals(2, listener.successfulTests().size());
		assertEquals(1, listener.failedTests().size());
	}
	
	@Test
	public void compoundResultForMultipleTestCases() {
		Config config = new Config();
		config.setTimeoutTestExecution(10);

		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(new String[]{ sampleTestClass() }, classLoaderWithTestClass(), listener, config);
		Collection<TestCase> failedTests = listener.failedTests();
		assertFalse(failedTests.isEmpty());
		Collection<TestCase> successfulTests = listener.successfulTests();
		assertFalse(successfulTests.isEmpty());
		CompoundResult compound;
		
		compound = TestSuiteExecution.runTestCases(failedTests, classLoaderWithTestClass(), config);
		assertFalse(compound.wasSuccessful());
		assertTrue(failedTests.size() == compound.getFailureCount());
		assertTrue(failedTests.size() == compound.getRunCount());
		assertTrue(0 == compound.getIgnoreCount());
		assertTrue(compound.successes().isEmpty());
		
		compound = TestSuiteExecution.runTestCases(successfulTests, classLoaderWithTestClass(), config);
		assertTrue(compound.wasSuccessful());
		assertTrue(0 == compound.getFailureCount());
		assertTrue(successfulTests.size() == compound.getRunCount());
		assertTrue(0 == compound.getIgnoreCount());
		assertTrue(compound.failures().isEmpty());
	}
	
	@Test
	public void runJUnit3Tests() {
		Config config = new Config();
		config.setTimeoutTestExecution(10);

		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(new String[]{ sampleTestCase() }, classLoaderWithTestCase(), listener, config);
		Collection<TestCase> cases = listener.allTests();
		assertEquals(3, cases.size());
	}
	
	private ClassLoader classLoaderWithTestClass() {
		URL resource = FileLibrary.resource("/sampleTestClass/TestClass.jar");
		return new URLClassLoader(new URL[] {resource});
	}
	
	private ClassLoader classLoaderWithTestCase() {
		URL resource = FileLibrary.resource("/sampleTestCase/SampleTestCase.jar");
		return new URLClassLoader(new URL[] {resource});
	}
	
	private String sampleTestClass() {
		return "xxl.java.junit.sample.TestClass";
	}
	
	private String sampleTestCase() {
		return "xxl.java.junit.SampleTestCase";
	}
}
