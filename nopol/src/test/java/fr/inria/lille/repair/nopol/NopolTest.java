package fr.inria.lille.repair.nopol;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.RepairType;
import org.junit.Ignore;
import org.junit.Test;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static fr.inria.lille.repair.nopol.Defects4jUtils.nopolConfigFor;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NopolTest {

	public final static String executionType = "nopol";

	@Test
	public void example1Fix() {
		Collection<String> expectedFailedTests = asList("test5", "test6");
		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 1, listener, RepairType.CONDITIONAL);

		TestUtility.assertPatches(12, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "index <= 0", "index < 1", "index <= -1", "index <= 0", "index < 0");
	}

	@Test
	public void example2Fix() {
		Collection<String> expectedFailedTests = asList("test1", "test2", "test4", "test5", "test6", "test7", "test9");
		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 2, listener, RepairType.CONDITIONAL);
		TestUtility.assertPatches(11, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "a <= b", "a < b", "1 <= (b - a)", "0 <= (b - a)", "1 < (b - a)", "0 < (b - a)", "a < b", "2 <= (b - a)", "-1 < (b - a)");
	}

	@Test
	public void example3Fix() {
		Collection<String> expectedFailedTests = asList("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9");

		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 3, listener, RepairType.CONDITIONAL);

		TestUtility.assertPatches(11, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "(tmp)==(0)", "(0)==(tmp)", "0 == tmp", "tmp == 0");
	}

	@Ignore
	@Test
	public void example4Fix() {
		Collection<String> expectedFailedTests = asList("test5");

		RepairType expectedRepairType = RepairType.PRECONDITION;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 4, listener, RepairType.PRECONDITION);

		TestUtility.assertPatches(23, expectedFailedTests, expectedRepairType, listener, patches);
//		TestUtility.assertAgainstKnownPatches(patches.get(0), "");
	}

	@Test
	public void example5Fix() {
		Collection<String> expectedFailedTests = asList("test4", "test5");

		RepairType expectedRepairType = RepairType.PRECONDITION;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 5, listener, RepairType.PRECONDITION);

		TestUtility.assertPatches(20, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "-1 <= a", "-1 < a", "1 <= a", "r <= a", "(-1)<(a)", "(0)<=(a)", "0 <= a", "0 < a");
	}

	@Test
	public void example6Fix() {
		Collection<String> expectedFailedTests = asList("test1", "test2", "test3", "test4", "test6");

		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 6, listener, RepairType.CONDITIONAL);

		TestUtility.assertPatches(7, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "(a)<(b)", "a <= b", "a < b");
	}

	@Test
	public void example7Fix() {
		Collection<String> expectedFailedTests = asList("test1");

		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 7, listener, RepairType.CONDITIONAL);

		TestUtility.assertPatches(21, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "(intermediaire == 0) && ((1)<=((-1)+((a)-(1))))",
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
				"(1 < a + -1) && ((a + -1 <= -1) || (intermediaire == 0))",
				"(2 < a) && (intermediaire == 0)",
				"(!(a == 2)) && (intermediaire == 0)",
				"(intermediaire == 0) && (2 < a)",
				"(intermediaire == 0) && ((a) != (2))",
				"((2) != (a)) && (intermediaire == 0)",
				"(intermediaire == 0) && (3 < a)"
				);
	}

	@Test
	public void example8Fix() {
		Collection<String> expectedFailedTests = asList("test_2");

		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 8, listener, RepairType.CONDITIONAL);

		TestUtility.assertPatches(12, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "((a * b))<=(100)", "(a * b) <= 100");
	}

	@Test
	public void preconditionThenConditional() {

		/* Test the PRE_THEN_COND mode.
			For the example 1, Nopol find a patch in CONDITIONAL mode but not in PRECONDITION mode.
		*/

		Collection<String> expectedFailedTests = asList("test5", "test6");

		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 1, listener, RepairType.PRE_THEN_COND);

		TestUtility.assertPatches(12, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "index <= 0", "index < 1", "index <= -1", "index <= 0", "index < 0");
	}

	@Test
	public void conditionalThenPreconditionnal() {

		/* Test the COND_THEN_PRE mode.
			For the example 1, Nopol find a patch in CONDITIONAL mode but not in PRECONDITION mode.
		*/

		Collection<String> expectedFailedTests = asList("test5", "test6");

		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 1, listener, RepairType.COND_THEN_PRE);

		TestUtility.assertPatches(12, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "index <= 0", "index < 1", "index <= -1", "index <= 0", "index < 0");
	}

	@Test
	public void conditionalThenPreconditionalUsePrecond() {

		/* Test the PRE_THEN_COND mode.
			For the example 5, Nopol find a patch in PRECONDITION mode but not in CONDITIONAL mode.
		 */

		Collection<String> expectedFailedTests = asList("test4", "test5");

		RepairType expectedRepairType = RepairType.PRECONDITION;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 5, listener, RepairType.COND_THEN_PRE);

		TestUtility.assertPatches(20, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "-1 <= a", "1 <= a", "r <= a", "(-1)<(a)", "(0)<=(a)", "0 <= a", "-1 < a", "0 < a");
	}

	@Test
	public void preconditionThenConditionalPrecondition() {

		/* Test the PRE_THEN_COND mode.
			For the example 5, Nopol find a patch in PRECONDITION mode but not in CONDITIONAL mode.
		 */

		Collection<String> expectedFailedTests = asList("test4", "test5");

		RepairType expectedRepairType = RepairType.PRECONDITION;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 5, listener, RepairType.PRE_THEN_COND);

		TestUtility.assertPatches(20, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "-1 <= a", "1 <= a", "r <= a", "(-1)<(a)", "(0)<=(a)", "0 <= a", "-1 < a", "0 < a");
	}

	@Test
	public void testSkippingRegressionStepLeadToAPatch() {
		NopolContext nopolContext = TestUtility.configForExample(executionType, 1);
		nopolContext.setType(RepairType.CONDITIONAL);
		nopolContext.setSkipRegressionStep(true);
		SolverFactory.setSolver("z3", TestUtility.solverPath);
		URLClassLoader classLoader = new URLClassLoader(nopolContext.getProjectClasspath());
		TestSuiteExecution.runCasesIn(nopolContext.getProjectTests(), classLoader, new TestCasesListener(), nopolContext);
		List<Patch> patches = TestUtility.patchFor(executionType, nopolContext);
		assertTrue(patches.size() > 0);
	}

	@Test
	public void testAPI() {
		NopolContext nopolContext = TestUtility.configForExample(executionType, 2);
		nopolContext.setType(RepairType.CONDITIONAL);
		SolverFactory.setSolver("z3", TestUtility.solverPath);

		NoPol nopol = new NoPol(nopolContext);
		NopolResult result = nopol.build();

		assertEquals(1, result.getPatches().size());
		// now we test the angelic values
		assertEquals(1, result.getNbAngelicValues());
		assertEquals("SourceLocation nopol_examples.nopol_example_2.NopolExample:11 ConditionalReplacer", result.getAngelicValues().get(0));

		TestUtility.assertAgainstKnownPatches(result.getPatches().get(0), "a < b", "-1 < (b - a)", "2 <= (b - a)", "1 < (b - a)");
	}

	@Test
	public void testIgnoreTestCouldCreateOtherPatches() {
		NopolContext nopolContext = TestUtility.configForExample(executionType, 2);
		nopolContext.setType(RepairType.CONDITIONAL);
		List<String> testsToIgnore = new ArrayList<String>();
		testsToIgnore.add("nopol_examples.nopol_example_2.NopolExampleTest#test2");
		testsToIgnore.add("nopol_examples.nopol_example_2.NopolExampleTest#test4");
		testsToIgnore.add("nopol_examples.nopol_example_2.NopolExampleTest#test5");
		testsToIgnore.add("nopol_examples.nopol_example_2.NopolExampleTest#test7");
		testsToIgnore.add("nopol_examples.nopol_example_2.NopolExampleTest#test9");

		nopolContext.setTestMethodsToIgnore(testsToIgnore);
		NoPol nopol = new NoPol(nopolContext);
		SolverFactory.setSolver("z3", TestUtility.solverPath);

		nopol = new NoPol(nopolContext);
		NopolResult result2 = nopol.build();

		assertEquals(1, result2.getPatches().size());
		TestUtility.assertAgainstKnownPatches(result2.getPatches().get(0),  "a == 2", "(b - a) == 2",  "2 == (b - a)", "1 < (b - a)");
	}
}
