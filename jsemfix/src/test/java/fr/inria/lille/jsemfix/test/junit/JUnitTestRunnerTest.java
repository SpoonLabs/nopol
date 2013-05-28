package fr.inria.lille.jsemfix.test.junit;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import fr.inria.lille.jsemfix.InClasspathJavaProgram;
import fr.inria.lille.jsemfix.gzoltar.ObjectsTest;
import fr.inria.lille.jsemfix.test.TestRunner;

public class JUnitTestRunnerTest {

	@Test
	public void testRun() {

		// GIVEN
		Class<ObjectsTest> testClass = ObjectsTest.class;
		TestRunner runner = new JUnitTestRunner(new Class<?>[] { testClass });

		// WHEN
		Set<fr.inria.lille.jsemfix.test.Test> failing = runner.run(new InClasspathJavaProgram(testClass.getPackage()));

		// THEN
		assertEquals(1, failing.size());

		fr.inria.lille.jsemfix.test.Test test = failing.iterator().next();
		assertEquals(testClass.getName(), test.getClassName());
		assertEquals("testEqual", test.getMethodName());
	}
}
