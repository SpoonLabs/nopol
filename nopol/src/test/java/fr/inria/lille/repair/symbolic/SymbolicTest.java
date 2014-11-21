package fr.inria.lille.repair.symbolic;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import xxl.java.container.classic.MetaSet;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;
import xxl.java.library.FileLibrary;
import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.Main;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.symbolic.patch.Patch;
import fr.inria.lille.repair.symbolic.synth.StatementType;

public class SymbolicTest {

	@Ignore
	@Test
	public void math309() { 
		String rootFolder = "/Users/virtual/Desktop/data/projects/dataset-nopol/309/";
		String srcFolder = rootFolder + "src/main/java";
		String classpath = rootFolder + "target/test-classes/" + ":" + rootFolder + "target/classes/";
		String solver = "cvc4";
		String solverPath = "/Users/virtual/Desktop/data/projects/nopol/nopol/lib/cvc4-1.4.2/cvc4_for_mac";
		Main.main(new String[] {"symbolic", srcFolder, classpath, solver, solverPath });
		/* PATCH: CONDITIONAL (mean)<=(0) */
	}

	@Ignore
	@Test
	public void lang_6ed8e576c4e13ac3ea05a3c5422236ea3affb799() {
		String rootFolder = "/Users/virtual/Desktop/data/projects/dataset-nopol/lang-6ed8e576c4e13ac3ea05a3c5422236ea3affb799/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";
		String dependency = rootFolder + "lib/junit-3.8.jar";
		String classpath = binFolder + ":" + dependency;
		String solver = "z3";
		String solverPath = "/Users/virtual/Desktop/data/projects/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		String testClass = "org.apache.commons.lang.StringUtilsSubstringTest";
		Main.main(new String[] {"symbolic", srcFolder, classpath, solver, solverPath, testClass });
		/* PATCH: CONDITIONAL (pos > (str.length()))||((len)<=(0)) */
	}
	
	@Ignore
	@Test
	public void percentile() { 
		String rootFolder = "/Users/virtual/Desktop/data/projects/dataset-nopol/Percentile/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";
		String dependencyA = rootFolder + "lib/commons-beanutils-1.7.0.jar";
		String dependencyB = rootFolder + "lib/commons-collections-2.0.jar";
		String dependencyC = rootFolder + "lib/commons-discovery-0.4.jar";
		String dependencyD = rootFolder + "lib/commons-lang-2.1.jar";
		String dependencyE = rootFolder + "lib/commons-logging-1.1.1.jar";
		String dependencyF = rootFolder + "lib/junit-3.8.jar";
		String classpath = binFolder + ":" + dependencyA + ":" + dependencyB + ":" + dependencyC + ":" + dependencyD + ":" + dependencyE + ":" + dependencyF;
		String solver = "z3";
		String solverPath = "/Users/virtual/Desktop/data/projects/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		Main.main(new String[] {"symbolic", srcFolder, classpath, solver, solverPath });
		/* PATCH: CONDITIONAL (intPos)==(sorted.length), (fpos)==(n) */
	}
	
	@Test
	public void exampleNopolMain() {
		SolverFactory solverFactory = SolverFactory.instance();
		String solverName = solverFactory.solverName();
		String solverPath = solverFactory.solverPath();
		
		//NoPolLauncher.main(new String[] { solverName, solverPath });
	}
	
	@Test
	public void example1Fix() {
		Collection<String> failedTests = asList("test5", "test6");
		Patch patch = test(1, 12, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(index)<=(0)", "(index)<(1)", "(index)<=(-1)");
	}
	
	@Test
	public void example2Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test4", "test5", "test6", "test7");
		Patch patch = test(2, 11, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(a)<=(b)", "(a)<(b)", "(1)<=((b - a))", "(0)<=((b - a))", "(1)<((b - a))", "(0)<((b - a))");
	}
	
	@Test
	public void example3Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9");
		Patch patch = test(3, 11, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(tmp)==(0)", "(0)==(tmp)");
	}
	
	@Test
	public void example4Fix() {
		Collection<String> failedTests = asList("test5");
		test(4, 23, StatementType.PRECONDITION, failedTests);
	}
	
	@Test
	public void example5Fix() {
		Collection<String> failedTests = asList("test4", "test5");
		Patch patch = test(5, 20, StatementType.PRECONDITION, failedTests);
		fixComparison(patch, "(-1)<=(a)", "(1)<=(a)", "(r)<=(a)", "(-1)<(a)", "(0)<=(a)");
	}
	
	@Test
	public void example6Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3", "test4", "test6");
		Patch patch = test(6, 7, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(a)<(b)", "(a)<=(b)");
	}
	
	@Test
	public void example7Fix() {
		Collection<String> failedTests = asList("test1");
		Patch patch = test(7, 21, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(intermediaire == 0)&&((1)<=((-1)+((a)-(1))))", 
				"(intermediaire == 0)&&((!(((a)+(-1))<=(1)))||((((a)+(-1))-(-1))==(intermediaire)))",
				"((1)<=((1)-(a)))||((intermediaire == 0)&&((intermediaire)!=(((1)-(a))+(1))))",
				"(intermediaire == 0)&&((((1)-((a)+(0)))<(-1))||(((a)+(0))!=((a)+(0))))",
				"!((((a)+(-1))<=(1))||((0)!=(intermediaire)))",
				"(!(((1)==(intermediaire))||(((a)+(-1))<=(1))))&&(!(((1)==(intermediaire))||(((a)+(-1))<=(1))))",
				"!(((intermediaire)!=(0))||(((1)-(-1))==(a)))");
	}
	
	@Test
	public void example8Fix() {
		Collection<String> failedTests = asList("test_2");
		Patch patch = test(8, 12, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "((a * b))<=(100)");
	}
	
	@Test
	public void example9Fix() {
		Collection<String> failedTests = asList("test_g", "test3_h", "test_f", "test3_i");
		Patch patch = test(9, 16, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "x * 3");
	}
	
	private Patch test(int projectNumber, int linePosition, StatementType type, Collection<String> expectedFailedTests) {
		ProjectReference project = projectForExample(projectNumber);
		TestCasesListener listener = new TestCasesListener();
		URLClassLoader classLoader = new URLClassLoader(project.classpath());
		TestSuiteExecution.runCasesIn(project.testClasses(), classLoader, listener);
		Collection<String> failedTests = TestCase.testNames(listener.failedTests());
		assertEquals(expectedFailedTests.size(), failedTests.size());
		assertTrue(expectedFailedTests.containsAll(failedTests));
		List<Patch> patches = patchFor(project);
		assertEquals(patches.toString(), 1, patches.size());
		Patch patch = patches.get(0);
		assertEquals(patch.getType(), type);
		assertEquals(linePosition, patch.getLineNumber());
		System.out.println(String.format("Patch for nopol example %d: %s", projectNumber, patch.asString()));
		return patch;
	}
	
	private void fixComparison(Patch foundPatch, String... expectedFixes) {
		Collection<String> possibleFixes = MetaSet.newHashSet(expectedFixes);
		assertTrue(foundPatch + "is not a valid patch", possibleFixes.contains(foundPatch.asString()));
	}
	
	public static ProjectReference projectForExample(int nopolExampleNumber) {
		String sourceFile = "../test-projects/src/";
		String classpath = "../test-projects/target/test-classes:../test-projects/target/classes";
		String[] testClasses = new String[] { "symbolic_examples.symbolic_example_" + nopolExampleNumber + ".NopolExampleTest" };
		return new ProjectReference(sourceFile, classpath, testClasses);
	}
	
	private List<Patch> patchFor(ProjectReference project) {
		clean(project.sourceFile().getParent());
		List<Patch> patches = SymbolicLauncher.run(project);
		clean(project.sourceFile().getParent());
		return patches;
	}
	
	private void clean(String folderPath) {
		String path = folderPath + "/spooned";
		if (FileLibrary.isValidPath(path)) {
			FileLibrary.deleteDirectory(path);
		}
	}
}
