package fr.inria.lille.repair.symbolic;

import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

import static java.util.Arrays.asList;

@Ignore
public class SymbolicTest extends TestUtility {

    public SymbolicTest() {
        super("symbolic");
    }

	@Test
	public void example1Fix() {
		Collection<String> failedTests = asList("test5", "test6");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = test(1, 12,  failedTests, config);
		fixComparison(patch, "index <= 0", "index < 1", "index <= -1");
	}

	@Test
	public void example2Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test4",
				"test5", "test6", "test7");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = test(2, 11, failedTests, config);
		fixComparison(patch, "a <= b", "a < b", "1 <= (b - a)",
				"0 <= (b - a)", "1 < (b - a)", "-1< (b - a)", "0 < (b - a)");
	}

	@Test
	public void example3Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3",
				"test4", "test5", "test6", "test7", "test8", "test9");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = test(3, 11, failedTests, config);
		fixComparison(patch, "tmp == 0", "0 == tmp");
	}

	@Test
	public void example4Fix() {
		Collection<String> failedTests = asList("test5");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = test(4, 23, failedTests, config);
		fixComparison(
				patch,
				"-1 <= a",
				"a.length() != 4",
				"((-1 - initializedVariableShouldBeCollected) < -1) && ((1 != (a.length() + (-1 - initializedVariableShouldBeCollected))) || a.length() == 0)",
                "(((-1)+(a.length()))-(1))!=(initializedVariableShouldBeCollected)",
				"(a.length()) != (initializedVariableShouldBeCollected + (initializedVariableShouldBeCollected) - (0))");
	}

	@Test
	public void example5Fix() {
		Collection<String> failedTests = asList("test4", "test5");
		Config config = new Config();
		config.setType(StatementType.PRECONDITION);
		Patch patch = test(5, 20, failedTests, config);
		fixComparison(patch, "-1 <= a", "1 <= a", "r <= a", "-1 < a", "0 <= a");
	}

	@Test
	public void example6Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3",
				"test4", "test6");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = test(6, 7, failedTests, config);
		fixComparison(patch, "a < b", "a <= b", "(4)<= b");
	}

	@Test
	public void example7Fix() {
		Collection<String> failedTests = asList("test1");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = test(7, 21, failedTests, config);
		fixComparison(
				patch,
				"(intermediaire == 0)&&((1)<=((-1)+((a)-(1))))",
				"(intermediaire == 0)&&((!(((a)+(-1))<=(1)))||((((a)+(-1))-(-1))==(intermediaire)))",
				"((1)<=((1)-(a)))||((intermediaire == 0)&&((intermediaire)!=(((1)-(a))+(1))))",
				"(intermediaire == 0)&&((((1)-((a)+(0)))<(-1))||(((a)+(0))!=((a)+(0))))",
				"!((((a)+(-1))<=(1))||((0)!=(intermediaire)))",
				"!(((intermediaire)!=(0))||((intermediaire == 0)&&((2)==(a))))",
				"(!(((1)==(intermediaire))||(((a)+(-1))<=(1))))&&(!(((1)==(intermediaire))||(((a)+(-1))<=(1))))",
				"!(((intermediaire)!=(0))||(((1)-(-1))==(a)))",
                "((a)!=((1)+(1)))&&(intermediaire == 0)",
				"(intermediaire == 0) && (!(a + -1 <= (intermediaire) - (-1)))");
	}

	@Test
	public void example8Fix() {
		Collection<String> failedTests = asList("test_2");
		Config config = new Config();
		config.setType(StatementType.CONDITIONAL);
		Patch patch = test(8, 12, failedTests, config);
		fixComparison(patch, "(a * b) <= 100");
	}

	@Test
	public void example9Fix() {
		Collection<String> failedTests = asList("test_f");
		Config config = new Config();
		config.setType(StatementType.INTEGER_LITERAL);
		Patch patch = test(9, 11, failedTests, config);
		fixComparison(patch, "x + 2", "(x) - (-1 + -1)");
	}

	@Test
	public void example10Fix() {
		Collection<String> failedTests = asList("test_g", "test_g_4");
		Config config = new Config();
		config.setType(StatementType.INTEGER_LITERAL);
		Patch patch = test(10, 9, failedTests, config);
		fixComparison(patch, "2 * x", "x + x", "resg - (x *(2 * -1))");
	}

	@Test
	public void example11Fix() {
		Collection<String> failedTests = asList("test3_h");
		Config config = new Config();
		config.setType(StatementType.INTEGER_LITERAL);
		Patch patch = test(11, 10, failedTests, config);
		fixComparison(patch, "x % 5", "(x < ((-1) - (1)) * ((-1) - (1) + (-1) - (1)))?(1):(0)");
	}

	@Test
	public void example12Fix() {
		Collection<String> failedTests = asList("test3_i");
		Config config = new Config();
		config.setType(StatementType.INTEGER_LITERAL);
		Patch patch = test(12, 28, failedTests, config);
		fixComparison(patch, "(x + 1) - (-1)");
	}
}
