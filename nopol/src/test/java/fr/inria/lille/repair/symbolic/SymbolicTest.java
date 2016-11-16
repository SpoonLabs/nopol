package fr.inria.lille.repair.symbolic;

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

@Ignore
public class SymbolicTest {

	public final static String executionType = "symbolic";

	@Test
	public void example1Fix() {
		Collection<String> expectedFailedTests = asList("test5", "test6");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		StatementType expectedStatementType = StatementType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 1, config, listener);

		TestUtility.assertPatches(12, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "index <= 0", "index < 1", "index <= -1");
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
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "a <= b", "a < b", "1 <= (b - a)",
				"0 <= (b - a)", "1 < (b - a)", "-1< (b - a)", "0 < (b - a)");
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
	public void example9Fix() {
		Collection<String> expectedFailedTests = asList("test_f");
		Config config = new Config();
		config.setType(StatementType.INTEGER_LITERAL);
		StatementType expectedStatementType = StatementType.INTEGER_LITERAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 9, config, listener);

		TestUtility.assertPatches(11, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "x + 2", "(x) - (-1 + -1)");
	}

	@Test
	public void example10Fix() {
		Collection<String> expectedFailedTests = asList("test_g", "test_g_4");
		Config config = new Config();
		config.setType(StatementType.INTEGER_LITERAL);
		StatementType expectedStatementType = StatementType.INTEGER_LITERAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 110, config, listener);

		TestUtility.assertPatches(9, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "2 * x", "x + x", "resg - (x *(2 * -1))");
	}

	@Test
	public void example11Fix() {
		Collection<String> expectedFailedTests = asList("test3_h");
		Config config = new Config();
		config.setType(StatementType.INTEGER_LITERAL);
		StatementType expectedStatementType = StatementType.INTEGER_LITERAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 10, config, listener);

		TestUtility.assertPatches(10, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "x % 5", "(x < ((-1) - (1)) * ((-1) - (1) + (-1) - (1)))?(1):(0)");
	}

	@Test
	public void example12Fix() {
		Collection<String> expectedFailedTests = asList("test3_i");
		Config config = new Config();
		config.setType(StatementType.INTEGER_LITERAL);
		StatementType expectedStatementType = StatementType.INTEGER_LITERAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 12, config, listener);

		TestUtility.assertPatches(28, expectedFailedTests, expectedStatementType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "(x + 1) - (-1)");
	}
}
