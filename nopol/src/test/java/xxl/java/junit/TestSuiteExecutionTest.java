package xxl.java.junit;

import fr.inria.lille.repair.common.config.NopolContext;
import org.junit.Test;
import org.junit.runner.Result;
import xxl.java.library.FileLibrary;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import static org.junit.Assert.*;

public class TestSuiteExecutionTest {

	@Test
	public void runSuite() {
		Result result = TestSuiteExecution.runCasesIn(new String[]{ sampleTestClass() }, classLoaderWithTestClass(), new NopolContext());
		assertFalse(result.wasSuccessful());
		assertTrue(3 == result.getRunCount());
		assertTrue(1 == result.getFailureCount());
	}
	
	@Test
	public void runSingleTest() {
		TestCase testCase = TestCase.from(sampleTestClass(), "joinTrue");
		Result result = TestSuiteExecution.runTestCase(testCase, classLoaderWithTestClass(), new NopolContext());
		assertTrue(result.wasSuccessful());
		assertEquals(1, result.getRunCount());
		assertEquals(0, result.getFailureCount());
	}
	
	@Test
	public void runSuiteWithTestListener() {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(new String[]{ sampleTestClass() }, classLoaderWithTestClass(), listener, new NopolContext());
		assertEquals(3, listener.allTests().size());
		assertEquals(2, listener.successfulTests().size());
		assertEquals(1, listener.failedTests().size());
	}
	
	@Test
	public void doNotUseSameTestNameTwice() {

		/* According to the name, we do not run twice the same test, i.e. test with same name */

		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(new String[]{ sampleTestClass(), sampleTestClass() }, classLoaderWithTestClass(), listener, new NopolContext());
		assertEquals(3, listener.allTests().size());
		assertEquals(2, listener.successfulTests().size());
		assertEquals(1, listener.failedTests().size());
	}
	
	@Test
	public void compoundResultForMultipleTestCases() {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(new String[]{ sampleTestClass() }, classLoaderWithTestClass(), listener, new NopolContext());
		Collection<TestCase> failedTests = listener.failedTests();
		assertFalse(failedTests.isEmpty());
		Collection<TestCase> successfulTests = listener.successfulTests();
		assertFalse(successfulTests.isEmpty());
		CompoundResult compound;
		
		compound = TestSuiteExecution.runTestCases(failedTests, classLoaderWithTestClass(), new NopolContext());
		assertFalse(compound.wasSuccessful());
		assertTrue(failedTests.size() == compound.getFailureCount());
		assertTrue(failedTests.size() == compound.getRunCount());
		assertTrue(0 == compound.getIgnoreCount());
		assertTrue(compound.successes().isEmpty());
		
		compound = TestSuiteExecution.runTestCases(successfulTests, classLoaderWithTestClass(), new NopolContext());
		assertTrue(compound.wasSuccessful());
		assertTrue(0 == compound.getFailureCount());
		assertTrue(successfulTests.size() == compound.getRunCount());
		assertTrue(0 == compound.getIgnoreCount());
		assertTrue(compound.failures().isEmpty());
	}
	
	@Test
	public void runJUnit3Tests() {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(new String[]{ sampleTestCase() }, classLoaderWithTestCase(), listener, new NopolContext());
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
