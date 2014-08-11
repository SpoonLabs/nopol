package fr.inria.lille.toolset;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.io.FileHandler;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.suite.TestSuiteExecution;
import fr.inria.lille.nopol.NoPol;
import fr.inria.lille.nopol.patch.Patch;
import fr.inria.lille.nopol.synth.BugKind;
import fr.inria.lille.repair.Main;

public class NopolTest {

	@Ignore
	@Test
	public void math309() { 
		String rootFolder = "/Users/virtual/Desktop/data/projects/dataset-nopol/309/";
		String srcFolder = rootFolder + "src/main/java";
		String classpath = rootFolder + "target/test-classes/" + ":" + rootFolder + "target/classes/";
		String solver = "cvc4";
		String solverPath = "/Users/virtual/Desktop/data/projects/nopol/nopol/lib/cvc4-1.4.1/cvc4_for_mac";
		Main.main(new String[] {"nopol", srcFolder, classpath, solver, solverPath });
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
		String solver = "cvc4";
		String solverPath = "/Users/virtual/Desktop/data/projects/nopol/nopol/lib/cvc4-1.4.1/cvc4_for_mac";
		String testClass = "org.apache.commons.lang.StringUtilsSubstringTest";
		Main.main(new String[] {"nopol", srcFolder, classpath, solver, solverPath, testClass });
		/* PATCH: CONDITIONAL (pos > (str.length()))||((len)<=(0)) */
	}
	
	@Test
	public void example1Fix() {
		Collection<String> failedTests = asList("test5", "test6");
		Patch patch = test(1, 12, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "(index)<=(0)", "(index)<(1)");
	}
	
	@Test
	public void example2Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test4", "test5", "test6", "test7");
		Patch patch = test(2, 11, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "(a)<=(b)", "(a)<(b)", "(1)<=((b - a))", "(0)<=((b - a))");
	}
	
	@Test
	public void example3Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9");
		Patch patch = test(3, 11, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "(tmp)==(0)", "(0)==(tmp)");
	}
	
	@Test
	public void example4Fix() {
		Collection<String> failedTests = asList("test5");
		test(4, 23, BugKind.PRECONDITION, failedTests);
	}
	
	@Test
	public void example5Fix() {
		Collection<String> failedTests = asList("test4", "test5");
		Patch patch = test(5, 20, BugKind.PRECONDITION, failedTests);
		fixComparison(patch, "(-1)<=(a)", "(1)<=(a)", "(r)<=(a)", "(-1)<(a)", "(0)<=(a)");
	}
	
	@Test
	public void example6Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3", "test4", "test6");
		Patch patch = test(6, 7, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "(a)<(b)", "(a)<=(b)");
	}
	
	@Ignore
	@Test
	public void example7Fix() {
		Collection<String> failedTests = asList("test1");
		Patch patch = test(7, 21, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "((intermediaire)==(0))&&((a)!=((1)+(1)))");
	}
	
	@Test
	public void example8Fix() {
		Collection<String> failedTests = asList("test_2");
		Patch patch = test(8, 12, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "((a * b))<=(100)");
	}

	private Patch test(int projectNumber, int linePosition, BugKind type, Collection<String> expectedFailedTests) {
		ProjectReference project = projectForExample(projectNumber);
		TestCasesListener listener = new TestCasesListener();
		URLClassLoader classLoader = new URLClassLoader(project.classpath());
		TestSuiteExecution.runCasesIn(project.testClasses(), classLoader, listener);
		Collection<String> failedTests = TestCase.testNames(listener.failedTests());
		assertEquals(expectedFailedTests.size(), failedTests.size());
		assertTrue(expectedFailedTests.containsAll(failedTests));
		List<Patch> patches = patchFor(project);
		assertEquals(1, patches.size());
		Patch patch = patches.get(0);
		assertEquals(patch.getType(), type);
		assertEquals(linePosition, patch.getLineNumber());
		System.out.println(String.format("Patch for nopol example %d: %s", projectNumber, patch.asString()));
		return patch;
	}
	
	private void fixComparison(Patch foundPatch, String... expectedFixes) {
		Collection<String> possibleFixes = SetLibrary.newHashSet(expectedFixes);
		assertTrue(possibleFixes.contains(foundPatch.asString()));
	}
	
	public static ProjectReference projectForExample(int nopolExampleNumber) {
		String sourceFile = "../test-projects/src/main/java/nopol_examples/nopol_example_" + nopolExampleNumber + "/NopolExample.java";
		String classpath = "../test-projects/target/test-classes:../test-projects/target/classes";
		String[] testClasses = new String[] { "nopol_examples.nopol_example_" + nopolExampleNumber + ".NopolExampleTest" };
		return new ProjectReference(sourceFile, classpath, testClasses);
	}
	
	private List<Patch> patchFor(ProjectReference project) {
		clean(project.sourceFile().getParent());
		boolean originalvalue = NoPol.isOneBuild();
		NoPol.setOneBuild(false);
		NoPol nopol = new NoPol(project.sourceFile(), project.classpath());
		List<Patch> patches = nopol.build(project.testClasses());
		NoPol.setOneBuild(originalvalue);
		clean(project.sourceFile().getParent());
		return patches;
	}
	
	private void clean(String folderPath) {
		Collection<File> smtFiles = FileHandler.filesMatchingNameIn(folderPath, "NopolExample:[0-9]+-[0-9]+[.]smt");
		FileHandler.deleteFiles(smtFiles);
		String path = folderPath + "/spooned";
		if (FileHandler.isValidPath(path)) {
			FileHandler.deleteDirectory(path);
		}
	}
}
