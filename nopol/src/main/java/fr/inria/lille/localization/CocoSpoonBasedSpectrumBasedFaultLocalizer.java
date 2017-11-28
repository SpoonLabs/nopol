package fr.inria.lille.localization;

import fil.iagl.opl.cocospoon.processors.WatcherProcessor;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import instrumenting._Instrumenting;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.util.*;

/**
 * Created by bdanglot on 10/3/16.
 */
public class CocoSpoonBasedSpectrumBasedFaultLocalizer extends DumbFaultLocalizerImpl {

	private final Metric metric;
	private int nbSucceedTest;
	private int nbFailingTest;

	private List<StatementSourceLocation> statements;

	public CocoSpoonBasedSpectrumBasedFaultLocalizer(NopolContext nopolContext, Metric metric) {
		super(nopolContext);
		this.metric = metric;
		this.statements = new ArrayList<>();
	}

	@Override
	protected void runTests(String[] testClasses, NopolContext nopolContext, SpoonedProject spooner, WatcherProcessor processor) {
		ClassLoader cl = spooner.processedAndDumpedToClassLoader(processor);
		TestCasesListener listener = new TestCasesListener();
		Map<String, Boolean> resultsPerNameOfTest = new HashMap<>();
		Map<String, Map<SourceLocation, Boolean>> linesExecutedPerTestNames = new HashMap<>();
		nbFailingTest = 0;
		nbSucceedTest = 0;
		for (int i = 0; i < testClasses.length; i++) {
			try {
				for (String methodName : super.getTestMethods(cl.loadClass(testClasses[i]))) {
					String testMethod = testClasses[i] + "#" + methodName;
					TestSuiteExecution.runTest(testMethod, cl, listener, nopolContext);
					//Since we executed one test at the time, the listener contains one and only one TestCase
					boolean testSucceed = listener.numberOfFailedTests() == 0;
					resultsPerNameOfTest.put(testMethod, testSucceed);
					if (testSucceed) {
						nbSucceedTest++;
					} else {
						nbFailingTest++;
					}
					linesExecutedPerTestNames.put(testMethod, super.copyExecutedLinesAndReinit(_Instrumenting.lines));
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
		return super.getTestListPerStatement();
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
