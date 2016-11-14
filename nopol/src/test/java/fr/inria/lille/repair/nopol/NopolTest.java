package fr.inria.lille.repair.nopol;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import org.junit.Ignore;
import org.junit.Test;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NopolTest extends TestUtility {

	private StatementType expectedStatementType;

	public NopolTest() {
		super("nopol");
	}

	@Test
	public void example1Fix() {
		Collection<String> failedTests = asList("test5", "test6");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = super.test(1, 12, failedTests, config);
		fixComparison(patch, "index <= 0", "index < 1", "index <= -1", "index <= 0");
	}

	@Test
	public void example2Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test4", "test5", "test6", "test7", "test9");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = super.test(2, 11, failedTests, config);
		fixComparison(patch, "a <= b", "a < b", "1 <= (b - a)", "0 <= (b - a)", "1 < (b - a)", "0 < (b - a)", "a < b", "2 <= (b - a)");
	}

	@Test
	public void example3Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = super.test(3, 11, failedTests, config);
		fixComparison(patch, "(tmp)==(0)", "(0)==(tmp)", "0 == tmp", "tmp == 0");
	}

	@Ignore
	@Test
	public void example4Fix() {
		Collection<String> failedTests = asList("test5");
		Config config = new Config();
		config.setType(StatementType.PRECONDITION);
		test(4, 23, failedTests, config);
	}

	@Test
	public void example5Fix() {
		Collection<String> failedTests = asList("test4", "test5");
		Config config = new Config();
		config.setType(StatementType.PRECONDITION);
		Patch patch = super.test(5, 20, failedTests, config);
		fixComparison(patch, "-1 <= a", "1 <= a", "(r)<=(a)", "(-1)<(a)", "(0)<=(a)", "0 <= a");
	}

	@Test
	public void example6Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3", "test4", "test6");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = super.test(6, 7, failedTests, config);
		fixComparison(patch, "(a)<(b)", "(a)<=(b)", "a < b");
	}

	@Test
	public void example7Fix() {
		Collection<String> failedTests = asList("test1");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = super.test(7, 21, failedTests, config);
		fixComparison(patch, "(intermediaire == 0) && ((1)<=((-1)+((a)-(1))))",
				"(intermediaire == 0) && ((!(((a)+(-1))<=(1)))||((((a)+(-1))-(-1))==(intermediaire)))",
				"((1)<=((1)-(a)))||((intermediaire == 0)&&((intermediaire)!=(((1)-(a))+(1))))",
				"(intermediaire == 0)&&((((1)-((a)+(0)))<(-1))||(((a)+(0))!=((a)+(0))))",
				"!((((a)+(-1))<=(1))||((0)!=(intermediaire)))",
				"(!(((1)==(intermediaire))||(((a)+(-1))<=(1))))&&(!(((1)==(intermediaire))||(((a)+(-1))<=(1))))",
				"!(((intermediaire)!=(0))||(((1)-(-1))==(a)))",
				"((a)!=((1)+(1)))&&(intermediaire == 0)",
				"(intermediaire == 0) && (!(a + -1 <= (intermediaire) - (-1)))",
				"(-1 + 1 == intermediaire) && (1 < a - 1)",
				"(-1 + 1 == intermediaire) && (1 < (a) - (1))",
				"(intermediaire < 1) && ((2) != (a))",
				"(intermediaire == 0) && (3 <= a)",
				"((intermediaire == 0) && (!(a < 3))) || (intermediaire == a)",
				"((intermediaire) - (a) + 1 < -1) && (intermediaire == 0)",
				"!((-1 <= (1) - (a + intermediaire)) || ((0) != (intermediaire)))",
				"(1 < a + -1) && ((a + -1 <= -1) || (intermediaire == 0))");
	}

	@Test
	public void example8Fix() {
		Collection<String> failedTests = asList("test_2");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = super.test(8, 12, failedTests, config);
		fixComparison(patch, "((a * b))<=(100)", "(a * b) <= 100");
	}

	@Test
	public void preconditionThenConditional() {

		/* Test the PRE_THEN_COND mode.
			For the example 1, Nopol find a patch in CONDITIONAL mode but not in PRECONDITION mode.
		 */

		Collection<String> failedTests = asList("test5", "test6");
		Config config = new Config();
		config.setType(StatementType.PRE_THEN_COND);
		expectedStatementType = StatementType.CONDITIONAL;
		Patch patch = test(1, 12, failedTests, config);
		fixComparison(patch, "index <= 0", "index < 1", "index <= -1", "index <= 0");
	}

	@Test
	public void preconditionThenConditionalPrecondition() {

		/* Test the PRE_THEN_COND mode.
			For the example 5, Nopol find a patch in PRECONDITION mode but not in CONDITIONAL mode.
		 */

		Collection<String> failedTests = asList("test4", "test5");
		Config config = new Config();
		config.setType(StatementType.PRE_THEN_COND);
		expectedStatementType = StatementType.PRECONDITION;
		Patch patch = test(5, 20, failedTests, config);
		fixComparison(patch, "-1 <= a", "1 <= a", "(r)<=(a)", "(-1)<(a)", "(0)<=(a)", "0 <= a");
	}

	@Override
	protected Patch test(int projectNumber, int linePosition,
						 Collection<String> expectedFailedTests, Config config) {
		ProjectReference project = projectForExample(projectNumber);
		SolverFactory.setSolver(solver, solverPath);
		TestCasesListener listener = new TestCasesListener();
		URLClassLoader classLoader = new URLClassLoader(project.classpath());
		TestSuiteExecution.runCasesIn(project.testClasses(), classLoader,
				listener, config);
		Collection<String> failedTests = TestCase.testNames(listener
				.failedTests());
		assertEquals(expectedFailedTests.size(), failedTests.size());
		assertTrue(expectedFailedTests.containsAll(failedTests));
		List<Patch> patches = patchFor(project, config);
		assertEquals(patches.toString(), 1, patches.size());
		Patch patch = patches.get(0);
		assertEquals(patch.getType(), expectedStatementType);
		assertEquals(linePosition, patch.getLineNumber());
		System.out.println(String.format("Patch for nopol example %d: %s",
				projectNumber, patch.asString()));
		return patch;
	}

	public static String absolutePathOf(int exampleNumber) {
		return "../test-projects/src/main/java/nopol_examples/nopol_example_" + exampleNumber + "/NopolExample.java";
	}
}
