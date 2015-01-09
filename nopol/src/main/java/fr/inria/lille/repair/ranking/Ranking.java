package fr.inria.lille.repair.ranking;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import com.gzoltar.core.components.Component;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;

import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.TestClassesFinder;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.sps.SuspiciousStatement;
import fr.inria.lille.repair.nopol.sps.gzoltar.GZoltarSuspiciousProgramStatements;

public class Ranking {

	public Ranking(File sourceFile, URL[] classpath, String[] tests) {
		this(new ProjectReference(sourceFile, classpath, tests));
	}

	public Ranking(final File sourceFile, final URL[] classpath) {
		this(new ProjectReference(sourceFile, classpath));
	}

	public Ranking(ProjectReference project) {
		this.classpath = project.classpath();
		this.sourceFile = project.sourceFile();
		this.testClasses = project.testClasses();

		jpfSpoonedProject = new SpoonedProject(this.sourceFile, classpath);

		// get all test classes of the current project
		if (this.testClasses.length == 0) {
			this.testClasses = new TestClassesFinder().findIn(classpath, true);
		}

		// init gzolor
		gZoltar = GZoltarSuspiciousProgramStatements.create(this.classpath,
				jpfSpoonedProject.topPackageNames());
	}

	public String summary() {
		TestCasesListener executedTests = this.getExecutedTests();
		Collection<TestCase> sucesssTests = executedTests.successfulTests();
		Collection<TestCase> failedTests = executedTests.failedTests();
		int successfulTests = 0;
		int totalTest = 0;
		Collection<SuspiciousStatement> suspicousStatements = this
				.getSuspisiousStatements();
		List<TestResult> gzoloarTestResults = this.gZoltar.getGzoltar()
				.getSpectra().getTestResults();
		Map<String, Integer> executedAndPassed = new HashMap<String, Integer>();
		Map<String, Integer> executedAndFailed = new HashMap<String, Integer>();
		for (TestResult testResult : gzoloarTestResults) {
			totalTest++;
			if (testResult.wasSuccessful()) {
				successfulTests++;
			}
			List<Component> components = testResult.getCoveredComponents();
			for (Iterator<?> iterator = components.iterator(); iterator.hasNext();) {
				Statement component = (Statement) iterator.next();
				String key = component.getMethod().getParent().getLabel() + ":"
						+ component.getLineNumber();
				System.out.println(component.getMethod());
				if (testResult.wasSuccessful()) {
					if (!executedAndPassed.containsKey(key)) {
						executedAndPassed.put(key, 0);
					}
					executedAndPassed.put(key, executedAndPassed.get(key) + 1);
				} else {
					if (!executedAndFailed.containsKey(key)) {
						executedAndFailed.put(key, 0);
					}
					executedAndFailed.put(key, executedAndFailed.get(key) + 1);
				}
			}
		}
		String output = "";
		output += "/************************/\n";
		output += "/******** Tests *********/\n";
		output += "/************************/\n";
		output += "Executed tests:   " + (totalTest) + "\n";
		output += "Successful tests: " + successfulTests + "\n";
		output += "Failed tests:     " + (totalTest - successfulTests) + "\n\n";

		output += "/************************/\n";
		output += "/* Suspicious statement */\n";
		output += "/************************/\n";
		for (SuspiciousStatement suspiciousStatement : suspicousStatements) {
			SourceLocation location = suspiciousStatement.getSourceLocation();
			String key = location.getContainingClassName() + ":"
					+ location.getLineNumber();
			int executedAndPassedCount = (executedAndPassed.containsKey(key) ? executedAndPassed
					.get(key) : 0);
			int executedAndFailedCount = (executedAndFailed.containsKey(key) ? executedAndFailed
					.get(key) : 0);
			output += String.format(
					"%s:%d -> %s (ep: %d, ef: %d, np: %d, nf: %d)\n",
					location.getContainingClassName(),
					location.getLineNumber(),
					suspiciousStatement.getSuspiciousness(),
					executedAndPassedCount, executedAndFailedCount,
					sucesssTests.size() - executedAndPassedCount,
					failedTests.size() - executedAndFailedCount);
		}
		return output;
	}

	public Collection<SuspiciousStatement> getSuspisiousStatements() {
		// get suspicious statement of the current project
		return gZoltar.sortBySuspiciousness(testClasses);
	}

	public TestCasesListener getExecutedTests() {
		return this.executeTest(testClasses,
				this.jpfSpoonedProject.dumpedToClassLoader());
	}

	/**
	 * returns the list of failing tests
	 * 
	 * @param testClasses
	 * @return the list of failing tests
	 */
	private TestCasesListener executeTest(String[] testClasses,
			ClassLoader testClassLoader) {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(testClasses, testClassLoader, listener);
		return listener;
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonedProject jpfSpoonedProject;
	private URL[] classpath;
	private final File sourceFile;
	private final GZoltarSuspiciousProgramStatements gZoltar;
	private String[] testClasses;
}
