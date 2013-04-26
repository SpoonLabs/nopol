package fr.inria.lille.jsemfix.test.junit;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import fr.inria.lille.jsemfix.gzoltar.ObjectsTest;
import fr.inria.lille.jsemfix.test.TestRunner;

public class JUnitTestRunnerTest {

	@Test
	public void testRun() {

		// GIVEN
		TestRunner runner = new JUnitTestRunner(new Class<?>[] { ObjectsTest.class });

		// WHEN
		Set<fr.inria.lille.jsemfix.test.Test> failing = runner.run();

		// THEN
		assertEquals(1, failing.size());

		fr.inria.lille.jsemfix.test.Test test = failing.iterator().next();
		assertEquals(ObjectsTest.class.getName(), test.getClassName());
		assertEquals("testEqual", test.getMethodName());
	}
}
