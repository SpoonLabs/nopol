package fr.inria.lille.localization;

import fil.iagl.opl.cocospoon.processors.WatcherProcessor;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import instrumenting._Instrumenting;
import spoon.processing.Processor;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.util.*;

import static fr.inria.lille.localization.FaultLocalizerUtils.getTestMethods;

/**
 * Created by bdanglot on 10/3/16.
 */
public class CocoSpoonBasedSpectrumBasedFaultLocalizer implements FaultLocalizer {

	private final Metric metric;
	private int nbSucceedTest;
	private int nbFailingTest;

	private List<StatementSourceLocation> statements;

	public CocoSpoonBasedSpectrumBasedFaultLocalizer(NopolContext nopolContext) {
		this(nopolContext, new Ochiai());
	}
	public CocoSpoonBasedSpectrumBasedFaultLocalizer(NopolContext nopolContext, Metric metric) {
		runTests(nopolContext.getProjectTests(), nopolContext, new SpoonedProject(nopolContext.getProjectSources(), nopolContext),  new WatcherProcessor());
		this.metric = metric;
		this.statements = new ArrayList<>();
	}

	protected Map<SourceLocation, List<TestResult>> countPerSourceLocation;
	/**
	 * This method copy the original map given, and set all its boolean at false to re-run a new test case
	 *
	 * @param original map to copy and reinit
	 * @return a copy of the map original
	 */
	protected Map<SourceLocation, Boolean> copyExecutedLinesAndReinit(Map<String, Map<Integer, Boolean>> original) {
		Map<SourceLocation, Boolean> copy = new HashMap<>();
		for (String s : original.keySet()) {
			for (Integer i : original.get(s).keySet()) {
				copy.put(new SourceLocation(s, i), original.get(s).get(i));
				original.get(s).put(i, false);
			}
		}
		return copy;
	}

	/**
	 * @param resultsPerNameOfTest
	 * @param linesExecutedPerTestNames
	 */
	protected void buildTestResultPerSourceLocation(Map<String, Boolean> resultsPerNameOfTest, Map<String, Map<SourceLocation, Boolean>> linesExecutedPerTestNames) {
		this.countPerSourceLocation = new HashMap<>();
		for (String fullQualifiedMethodTestName : linesExecutedPerTestNames.keySet()) {
			Map<SourceLocation, Boolean> coveredLines = linesExecutedPerTestNames.get(fullQualifiedMethodTestName);
			for (SourceLocation sourceLocation : coveredLines.keySet()) {
				if (coveredLines.get(sourceLocation)) {
					if (!this.countPerSourceLocation.containsKey(sourceLocation))
						this.countPerSourceLocation.put(sourceLocation, new ArrayList<TestResult>());
					this.countPerSourceLocation.get(sourceLocation).add(new TestResultImpl(TestCase.from(fullQualifiedMethodTestName), resultsPerNameOfTest.get(fullQualifiedMethodTestName)));
				}
			}
		}
	}


	protected void runTests(String[] testClasses, NopolContext nopolContext, SpoonedProject spooner, Processor processor) {
		ClassLoader cl = spooner.processedAndDumpedToClassLoader(processor);
		TestCasesListener listener = new TestCasesListener();
		Map<String, Boolean> resultsPerNameOfTest = new HashMap<>();
		Map<String, Map<SourceLocation, Boolean>> linesExecutedPerTestNames = new HashMap<>();
		nbFailingTest = 0;
		nbSucceedTest = 0;
		for (int i = 0; i < testClasses.length; i++) {
			try {
				List<String> testMethods = new ArrayList<>();
				if (testClasses[i].contains("#")) {
					testMethods.add(testClasses[i]);
				} else {
					testMethods.addAll(getTestMethods(cl.loadClass(testClasses[i])));
				}
				for (String testMethod : testMethods) {
					TestSuiteExecution.runTest(testMethod, cl, listener, nopolContext);
					//Since we executed one test at the time, the listener contains one and only one TestCase
					boolean testSucceed = listener.numberOfFailedTests() == 0;
					resultsPerNameOfTest.put(testMethod, testSucceed);
					if (testSucceed) {
						nbSucceedTest++;
					} else {
						nbFailingTest++;
					}
					linesExecutedPerTestNames.put(testMethod, copyExecutedLinesAndReinit(_Instrumenting.lines));
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		this.buildTestResultPerSourceLocation(resultsPerNameOfTest, linesExecutedPerTestNames);
	}

	@Override
	public Map<SourceLocation, List<TestResult>> getTestListPerStatement() {
		sortBySuspiciousness();
		return this.countPerSourceLocation;
	}

	private void sortBySuspiciousness() {
		for (SourceLocation sourceLocation : this.countPerSourceLocation.keySet()) {
			StatementSourceLocation current = new StatementSourceLocation(this.metric, sourceLocation);
			int ef = 0;
			int ep = 0;
			for (TestResult results : this.countPerSourceLocation.get(sourceLocation)) {
				if (results.isSuccessful())
					ep++;
				else
					ef++;
			}
			current.setNf(nbFailingTest - ef);
			current.setNp(nbSucceedTest - ep);
			current.setEp(ep);
			current.setEf(ef);
			statements.add(current);
		}
		Collections.sort(statements, new Comparator<StatementSourceLocation>() {
			@Override
			public int compare(StatementSourceLocation o1, StatementSourceLocation o2) {
				return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness());
			}
		});

		LinkedHashMap<SourceLocation, List<TestResult>> map = new LinkedHashMap<>();
		for (StatementSourceLocation source : statements) {
			map.put(source.getLocation(), this.countPerSourceLocation.get(source.getLocation()));
		}
		this.countPerSourceLocation = map;
	}

	@Override
	public List<? extends StatementSourceLocation> getStatements() {
		return this.statements;
	}
}
