package fr.inria.lille.repair.nopol;

import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import org.junit.Ignore;
import org.junit.Test;
import xxl.java.junit.TestCasesListener;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class NopolTest {

	public final static String executionType = "nopol";

	@Test
	public void example1Fix() {
		Collection<String> expectedFailedTests = asList("test5", "test6");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		StatementType expectedStatementType = StatementType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 1, config, listener);

		TestUtility.assertPatches(12, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "index <= 0", "index < 1", "index <= -1", "index <= 0");
	}

	@Test
	public void example2Fix() {
		Collection<String> expectedFailedTests = asList("test1", "test2", "test4", "test5", "test6", "test7", "test9");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		StatementType expectedStatementType = StatementType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 2, config, listener);

		TestUtility.assertPatches(11, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "a <= b", "a < b", "1 <= (b - a)", "0 <= (b - a)", "1 < (b - a)", "0 < (b - a)", "a < b", "2 <= (b - a)");
	}

	@Test
	public void example3Fix() {
		Collection<String> expectedFailedTests = asList("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		StatementType expectedStatementType = StatementType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 3, config, listener);

		TestUtility.assertPatches(11, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "(tmp)==(0)", "(0)==(tmp)", "0 == tmp", "tmp == 0");
	}

	@Ignore
	@Test
	public void example4Fix() {
		Collection<String> expectedFailedTests = asList("test5");
		Config config = new Config();
		config.setType(StatementType.PRECONDITION);
		StatementType expectedStatementType = StatementType.PRECONDITION;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 4, config, listener);

		TestUtility.assertPatches(23, expectedFailedTests, expectedStatementType, listener, patches);
//		TestUtility.assertAgainstKnownPatches(patches.get(0), "");
	}

	@Test
	public void example5Fix() {
		Collection<String> expectedFailedTests = asList("test4", "test5");
		Config config = new Config();
		config.setType(StatementType.PRECONDITION);
		StatementType expectedStatementType = StatementType.PRECONDITION;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 5, config, listener);

		TestUtility.assertPatches(20, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "-1 <= a", "1 <= a", "(r)<=(a)", "(-1)<(a)", "(0)<=(a)", "0 <= a");
	}

	@Test
	public void example6Fix() {
		Collection<String> expectedFailedTests = asList("test1", "test2", "test3", "test4", "test6");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		StatementType expectedStatementType = StatementType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 6, config, listener);

		TestUtility.assertPatches(7, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "(a)<(b)", "(a)<=(b)", "a < b");
	}

	@Test
	public void example7Fix() {
		Collection<String> expectedFailedTests = asList("test1");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		StatementType expectedStatementType = StatementType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 7, config, listener);

		TestUtility.assertPatches(21, expectedFailedTests, expectedStatementType, listener, patches);
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
				"(1 < a + -1) && ((a + -1 <= -1) || (intermediaire == 0))");
	}

	@Test
	public void example8Fix() {
		Collection<String> expectedFailedTests = asList("test_2");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		StatementType expectedStatementType = StatementType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 8, config, listener);

		TestUtility.assertPatches(12, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "((a * b))<=(100)", "(a * b) <= 100");
	}

	@Test
	public void preconditionThenConditional() {

		/* Test the PRE_THEN_COND mode.
			For the example 1, Nopol find a patch in CONDITIONAL mode but not in PRECONDITION mode.
		*/

		Collection<String> expectedFailedTests = asList("test5", "test6");
		Config config = new Config();
		config.setType(StatementType.PRE_THEN_COND);
		StatementType expectedStatementType = StatementType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 1, config, listener);

		TestUtility.assertPatches(12, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "index <= 0", "index < 1", "index <= -1", "index <= 0");
	}

	@Test
	public void preconditionThenConditionalPrecondition() {

		/* Test the PRE_THEN_COND mode.
			For the example 5, Nopol find a patch in PRECONDITION mode but not in CONDITIONAL mode.
		 */

		Collection<String> expectedFailedTests = asList("test4", "test5");
		Config config = new Config();
		config.setType(StatementType.PRE_THEN_COND);
		StatementType expectedStatementType = StatementType.PRECONDITION;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 5, config, listener);

		TestUtility.assertPatches(20, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "-1 <= a", "1 <= a", "(r)<=(a)", "(-1)<(a)", "(0)<=(a)", "0 <= a");
	}
}
