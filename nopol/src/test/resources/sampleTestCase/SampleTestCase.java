package xxl.java.junit;

import junit.framework.TestCase;
import junit.framework.TestResult;

/*
 * $ mkdir classes
 * $ javac -getClasspath /Users/virtual/.m2/repository/junit/junit/3.8.1/junit-3.8.1.jar -d classes SampleTestCase.java
 * $ jar cf sampleTestCase.jar -C classes xxl
 */

public class SampleTestCase extends TestCase {

	@Override
	public void runBare() throws Throwable {
		super.runBare();
	}
	
	@Override
	public void run(TestResult result) {
		super.run(result);
	}
	
	@Override
	public TestResult run() {
		return super.run();
	}
	
	@Override
	public String getName() {
		return super.getName();
	}
	
	@Override
	public int countTestCases() {
		return super.countTestCases();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
	}
	
	@Override
	protected void runTest() throws Throwable {
		super.runTest();
	}
	
	@Override
	protected TestResult createResult() {
		return super.createResult();
	}
	
	public void testOne() {
		int a = 0;
		assertTrue(0 == a);
	}
	
	public void testTwo() {
		int a = 0;
		assertFalse(1 == a);
	}
	
	public void testThree() {
		String a = "";
		assertEquals("", a);
	}
}
