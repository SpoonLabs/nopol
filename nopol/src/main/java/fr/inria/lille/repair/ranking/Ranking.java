package fr.inria.lille.repair.ranking;

import java.io.File;
import java.net.URL;
import java.util.*;

import com.gzoltar.core.components.count.ComponentCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;

import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.TestClassesFinder;
import fr.inria.lille.repair.nopol.sps.SuspiciousStatement;
import fr.inria.lille.repair.nopol.sps.gzoltar.GZoltarSuspiciousProgramStatements;

public class Ranking {

	public Ranking(File[] sourceFile, URL[] classpath, String[] tests) {
		this(new ProjectReference(sourceFile, classpath, tests));
	}

	public Ranking(final File[] sourceFile, final URL[] classpath) {
		this(new ProjectReference(sourceFile, classpath));
	}

	public Ranking(ProjectReference project) {
		this.classpath = project.classpath();
		this.sourceFile = project.sourceFiles();
		this.testClasses = project.testClasses();

		// get all test classes of the current project
		if (this.testClasses.length == 0) {
			this.testClasses = new TestClassesFinder().findIn(classpath, false);
		}
		// init gzolor
		gZoltar = GZoltarSuspiciousProgramStatements.create(classpath, testClasses);
	}

	public String summary() {
		int nbFailingTest = 0;
		int successfulTests = 0;
		int nbTest = 0;

		Collection<SuspiciousStatement> suspiciousStatements = this
				.getSuspisiousStatements();
		List<TestResult> gzoloarTestResults = this.gZoltar.getGzoltar()
				.getSpectra().getTestResults();
		Map<Statement, Integer> executedAndPassed = new HashMap<>();
		Map<Statement, Integer> executedAndFailed = new HashMap<>();
		nbTest = gzoloarTestResults.size();
		for (int i = 0; i < gzoloarTestResults.size(); i++) {
			TestResult testResult = gzoloarTestResults.get(i);
			if (!testResult.wasSuccessful()) {
				nbFailingTest++;
			}
			List<ComponentCount> components = testResult.getCoveredComponents();
			for (Iterator<ComponentCount> iterator = components.iterator(); iterator.hasNext();) {
				Statement component = (Statement) iterator.next().getComponent();
				//String key = component.getMethod().getParent().getLabel() + ":"  + component.getLineNumber();
				if (testResult.wasSuccessful()) {
					if (!executedAndPassed.containsKey(component)) {
						executedAndPassed.put(component, 0);
					}
					executedAndPassed.put(component, executedAndPassed.get(component) + 1);
				} else {
					if (!executedAndFailed.containsKey(component)) {
						executedAndFailed.put(component, 0);
					}
					executedAndFailed.put(component, executedAndFailed.get(component) + 1);
				}
			}
		}
		successfulTests = nbTest - nbFailingTest;

		String output = "";
		output += "/************************/\n";
		output += "/******** Tests *********/\n";
		output += "/************************/\n";
		output += "Executed tests:   " + (nbTest) + "\n";
		output += "Successful tests: " + successfulTests + "\n";
		output += "Failed tests:     " + (nbFailingTest) + "\n\n";

		for (TestResult testResult : gzoloarTestResults) {
			if (!testResult.wasSuccessful()) {
				output += testResult.getName() + "\n";
				output += testResult.getTrace() + "\n";
			}
		}

		output += "\n/************************/\n";
		output += "/* Suspicious statement */\n";
		output += "/************************/\n";
		for (SuspiciousStatement statement : suspiciousStatements) {
			int executedAndPassedCount = (executedAndPassed.containsKey(statement.getStatement()) ? executedAndPassed
					.get(statement.getStatement()) : 0);
			int executedAndFailedCount = (executedAndFailed.containsKey(statement.getStatement()) ? executedAndFailed
					.get(statement.getStatement()) : 0);

			String cl = statement.getStatement().getMethod().getParent().getLabel();
			int line = statement.getStatement().getLineNumber();

			output += String.format(
					"%s:%d -> %s (ep: %d, ef: %d, np: %d, nf: %d)\n",
					cl,
					line,
					statement.getSuspiciousness(),
					executedAndPassedCount, executedAndFailedCount,
					successfulTests - executedAndPassedCount,
					nbFailingTest - executedAndFailedCount);
		}
		return output;
	}

	public Collection<SuspiciousStatement> getSuspisiousStatements() {
		// get suspicious statement of the current project
		return gZoltar.sortBySuspiciousness(testClasses);
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private URL[] classpath;
	private final File sourceFile[];
	private final GZoltarSuspiciousProgramStatements gZoltar;
	private String[] testClasses;
}
