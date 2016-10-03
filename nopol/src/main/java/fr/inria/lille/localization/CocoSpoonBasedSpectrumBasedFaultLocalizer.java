package fr.inria.lille.localization;

import fil.iagl.opl.cocospoon.processors.WatcherProcessor;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.nopol.SourceLocation;
import instrumenting._Instrumenting;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by bdanglot on 10/3/16.
 */
public class CocoSpoonBasedSpectrumBasedFaultLocalizer extends DumbFaultLocalizerImpl {

	private final Metric metric;
	private int nbSucceedTest;
	private int nbFailingTest;

	public CocoSpoonBasedSpectrumBasedFaultLocalizer(File[] sourcesClasses, URL[] classpath, String[] testClasses, Config config, Metric metric) {
		super(sourcesClasses, classpath, testClasses, config);
		this.metric = metric;
	}

	@Override
	protected void runTests(String[] testClasses, Config config, SpoonedProject spooner, WatcherProcessor processor) {
		ClassLoader cl = spooner.processedAndDumpedToClassLoader(processor);
		TestCasesListener listener = new TestCasesListener();
		Map<String, Boolean> resultsPerNameOfTest = new HashMap<>();
		Map<String, Map<String, Map<Integer, Boolean>>> linesExecutedPerTestNames = new HashMap<>();
		nbFailingTest = 0;
		nbSucceedTest = 0;
		for (int i = 0; i < testClasses.length; i++) {
			try {
				for (String methodName : super.getTestMethods(cl.loadClass(testClasses[i]))) {
					String testMethod = testClasses[i] + "#" + methodName;
					TestSuiteExecution.runTest(testMethod, cl, listener, config);
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
		List<StatementSourceLocation> sources = new ArrayList<>();
		for (SourceLocation sourceLocation : this.countPerSourceLocation.keySet()) {
			StatementSourceLocation current = new StatementSourceLocation(sourceLocation);
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
			sources.add(current);
		}
		Collections.sort(sources, new Comparator<StatementSourceLocation>() {
			@Override
			public int compare(StatementSourceLocation o1, StatementSourceLocation o2) {
				return Double.compare(metric.value(o1.getEf(), o1.getEp(), o1.getNf(), o1.getNp()),
						metric.value(o2.getEf(), o2.getEp(), o2.getNf(), o2.getNp()));
			}
		});

		LinkedHashMap<SourceLocation, List<TestResult>> map = new LinkedHashMap<>();
		for (StatementSourceLocation source : sources) {
			map.put(source.getLocation(), this.countPerSourceLocation.get(source.getLocation()));
		}
		this.countPerSourceLocation = map;
	}

}
