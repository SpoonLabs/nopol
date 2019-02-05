package fr.inria.lille.repair.symbolic;

import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.RepairType;
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

		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 1, listener, RepairType.CONDITIONAL);

		TestUtility.assertPatches(12, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "index <= 0", "index < 1", "index <= -1");
	}

	@Test
	public void example2Fix() {
		Collection<String> expectedFailedTests = asList("test1", "test2", "test4", "test5", "test6", "test7", "test9");

		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 2, listener, RepairType.CONDITIONAL);

		TestUtility.assertPatches(11, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0),  "a <= b", "a < b", "1 <= (b - a)",
				"0 <= (b - a)", "1 < (b - a)", "-1< (b - a)", "0 < (b - a)");
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
		TestUtility.assertAgainstKnownPatches(patches.get(0), "-1 <= a", "1 <= a", "(r)<=(a)", "(-1)<(a)", "(0)<=(a)", "0 <= a");
	}

	@Test
	public void example6Fix() {
		Collection<String> expectedFailedTests = asList("test1", "test2", "test3", "test4", "test6");

		RepairType expectedRepairType = RepairType.CONDITIONAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 6, listener, RepairType.CONDITIONAL);

		TestUtility.assertPatches(7, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "(a)<(b)", "(a)<=(b)", "a < b");
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
				"(1 < a + -1) && ((a + -1 <= -1) || (intermediaire == 0))");
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
	public void example9Fix() {
		Collection<String> expectedFailedTests = asList("test_f");

		RepairType expectedRepairType = RepairType.INTEGER_LITERAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 9, listener, RepairType.INTEGER_LITERAL);

		TestUtility.assertPatches(11, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "x + 2", "(x) - (-1 + -1)");
	}

	@Test
	public void example10Fix() {
		Collection<String> expectedFailedTests = asList("test_g", "test_g_4");

		RepairType expectedRepairType = RepairType.INTEGER_LITERAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 110, listener, RepairType.INTEGER_LITERAL);

		TestUtility.assertPatches(9, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "2 * x", "x + x", "resg - (x *(2 * -1))");
	}

	@Test
	public void example11Fix() {
		Collection<String> expectedFailedTests = asList("test3_h");

		RepairType expectedRepairType = RepairType.INTEGER_LITERAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 10, listener, RepairType.INTEGER_LITERAL);

		TestUtility.assertPatches(10, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "x % 5", "(x < ((-1) - (1)) * ((-1) - (1) + (-1) - (1)))?(1):(0)");
	}

	@Test
	public void example12Fix() {
		Collection<String> expectedFailedTests = asList("test3_i");

		RepairType expectedRepairType = RepairType.INTEGER_LITERAL;
		final TestCasesListener listener = new TestCasesListener();

		List<Patch> patches = TestUtility.setupAndRun(executionType, 12, listener, RepairType.INTEGER_LITERAL);

		TestUtility.assertPatches(28, expectedFailedTests, expectedRepairType, listener, patches);
		TestUtility.assertAgainstKnownPatches(patches.get(0), "(x + 1) - (-1)");
	}
}
