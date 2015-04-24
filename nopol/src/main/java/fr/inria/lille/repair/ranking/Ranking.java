package fr.inria.lille.repair.ranking;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.count.ComponentCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;

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

		// get all test classes of the current project
		if (this.testClasses.length == 0) {
			this.testClasses = new TestClassesFinder().findIn(classpath, true);
		}

		// init gzolor
		gZoltar = GZoltarSuspiciousProgramStatements.create(sourceFile.getParentFile(), classpath, Arrays.asList(""));
	}

	public String summary() {
		int successfulTests = 0;
		int totalTest = 0;
		Collection<SuspiciousStatement> suspicousStatements = this
				.getSuspisiousStatements();
		List<TestResult> gzoloarTestResults = this.gZoltar.getGzoltar()
				.getSpectra().getTestResults();
		Map<String, Integer> executedAndPassed = new HashMap<>();
		Map<String, Integer> executedAndFailed = new HashMap<>();
		for (TestResult testResult : gzoloarTestResults) {
			totalTest++;
			if (testResult.wasSuccessful()) {
				successfulTests++;
			}
			List<ComponentCount> components = testResult.getCoveredComponents();
			for (Iterator<ComponentCount> iterator = components.iterator(); iterator.hasNext();) {
				Statement component = (Statement) iterator.next().getComponent();
				String key = component.getMethod().getParent().getLabel() + ":"
						+ component.getLineNumber();
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

		for (TestResult testResult : gzoloarTestResults) {
			if (!testResult.wasSuccessful()) {
				output += testResult.getName() + "\n";
				output += testResult.getTrace() + "\n";
			}
		}

		output += "\n/************************/\n";
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
					successfulTests - executedAndPassedCount,
					totalTest - successfulTests - executedAndFailedCount);
		}
		return output;
	}

	public Collection<SuspiciousStatement> getSuspisiousStatements() {
		// get suspicious statement of the current project
		return gZoltar.sortBySuspiciousness(testClasses);
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private URL[] classpath;
	private final File sourceFile;
	private final GZoltarSuspiciousProgramStatements gZoltar;
	private String[] testClasses;
}
