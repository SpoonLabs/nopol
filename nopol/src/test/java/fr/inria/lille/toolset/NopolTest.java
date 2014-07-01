package fr.inria.lille.toolset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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

public class NopolTest {

	public static ProjectReference example(int projectNumber) {
		if (projects == null) {
			setUp();
		}
		if (1 <= projectNumber && projectNumber <= numberOfExamples) {
			return projects[projectNumber - 1];
		}
		return null;
	}
	
	@BeforeClass
	public static void setUp() {
		numberOfExamples = 6;
		exampleNames = new String[numberOfExamples];
		projects = new ProjectReference[numberOfExamples];
		for (int index = 0; index < numberOfExamples; index += 1) {
			String exampleName = "example_" + (index + 1);
			exampleNames[index] = exampleName;
			projects[index] = projectForExample(exampleName);
		}
		removeSpoonFiles();
	}
	
	@AfterClass
	public static void removeSpoonFiles() {
		for (String exampleName : exampleNames) {
			removeSMTFiles(exampleName);
			removeSpoonedFolder(exampleName);
		}
	}

	private static ProjectReference projectForExample(String exampleName) {
		String sourceFile = sourceFolderPathForExample(exampleName) + "NopolExample.java";
		String classpath = "../test-projects/target/test-classes:../test-projects/target/classes";
		String[] testClasses = new String[] { packageNameFor(exampleName) + ".NopolExampleTest" };
		return new ProjectReference(sourceFile, classpath, testClasses);
	}
	
	private static String sourceFolderPathForExample(String exampleName) {
		return String.format("../test-projects/src/main/java/nopol_examples/nopol_%s/", exampleName);
	}
	
	private static String packageNameFor(String exampleName) {
		return String.format("nopol_examples.nopol_%s", exampleName);
	}
	
	private static void removeSMTFiles(String exampleName) {
		Collection<File> smtFiles = FileHandler.filesMatchingNameIn(sourceFolderPathForExample(exampleName), "NopolExample:[0-9]+.smt_[0-9]+");
		FileHandler.deleteFiles(smtFiles);
	}
	
	private static void removeSpoonedFolder(String exampleName) {
		String path = sourceFolderPathForExample(exampleName) + "spooned";
		if (FileHandler.isValidPath(path)) {
			FileHandler.deleteDirectory(path);
		}
	}
	
	@Test
	public void example1Fix() {
		Collection<String> failedTests = SetLibrary.newHashSet("test5", "test6");
		Patch patch = test(1, 12, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "(index)<=(0)", "(index)<(1)");
	}
	
	@Test
	public void example2Fix() {
		Collection<String> failedTests = SetLibrary.newHashSet("test1", "test2", "test4", "test5", "test6", "test7");
		Patch patch = test(2, 9, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "(a)<=(b)");
	}
	
	@Test
	public void example3Fix() {
		Collection<String> failedTests = SetLibrary.newHashSet("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9");
		Patch patch = test(3, 11, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "(tmp)==(0)", "(0)==(tmp)");
	}
	
	@Test
	public void example4Fix() {
		Collection<String> failedTests = SetLibrary.newHashSet("test5");
		test(4, 23, BugKind.PRECONDITION, failedTests);
	}
	
	@Test
	public void example5Fix() {
		Collection<String> failedTests = SetLibrary.newHashSet("test4", "test5");
		Patch patch = test(5, 13, BugKind.PRECONDITION, failedTests);
		fixComparison(patch, "(-1)<=(a)", "(1)<=(a)", "(r)<=(a)", "(-1)<(a)");
	}
	
	@Test
	public void example6Fix() {
		Collection<String> failedTests = SetLibrary.newHashSet("test1", "test2", "test3", "test4", "test6");
		Patch patch = test(6, 7, BugKind.CONDITIONAL, failedTests);
		fixComparison(patch, "(a)<(b)", "(a)<=(b)");
	}
	
	private Patch test(int projectNumber, int linePosition, BugKind type, Collection<String> expectedFailedTests) {
		ProjectReference project = example(projectNumber);
		TestCasesListener listener = new TestCasesListener();
		URLClassLoader classLoader = new URLClassLoader(project.classpath());
		TestSuiteExecution.runCasesIn(project.testClasses(), classLoader, listener);
		Collection<String> failedTests = TestCase.testNames(listener.failedTests());
		assertEquals(expectedFailedTests, failedTests);
		Patch patch = patchFor(project);
		assertEquals(patch.getType(), type);
		assertEquals(linePosition, patch.getLineNumber());
		System.out.println(String.format("Patch for nopol example %d: %s", projectNumber, patch.asString()));
		return patch;
	}
	
	private void fixComparison(Patch foundPatch, String... expectedFixes) {
		Collection<String> possibleFixes = SetLibrary.newHashSet(expectedFixes);
		assertTrue(possibleFixes.contains(foundPatch.asString()));
	}

	private Patch patchFor(ProjectReference project) {
		NoPol nopol = new NoPol(project.sourceFile(), project.classpath());
		return nopol.build(project.testClasses());
	}

	private static int numberOfExamples;
	private static String[] exampleNames;
	private static ProjectReference[] projects;
}
