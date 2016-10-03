package fr.inria.lille.localization;

import com.gzoltar.core.components.Statement;
import fil.iagl.opl.cocospoon.processors.WatcherProcessor;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.nopol.SourceLocation;
import instrumenting._Instrumenting;
import org.junit.Test;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Created by bdanglot on 9/30/16.
 */
public class FaultLocalizerImpl implements FaultLocalizer {

	private Map<SourceLocation, List<TestResult>> countPerSourceLocation;

	public FaultLocalizerImpl(File[] sourcesClasses, URL[] classpath, String[] testClasses, Config config) {
		SpoonedProject spooner = new SpoonedProject(sourcesClasses, classpath, config);
		WatcherProcessor processor = new WatcherProcessor();
		runTests(testClasses, config, spooner, processor);
	}

	/**
	 * run all testClasses to build the covered code
	 * @param testClasses
	 * @param config
	 * @param spooner
	 * @param processor
	 */
	private void runTests(String[] testClasses, Config config, SpoonedProject spooner, WatcherProcessor processor) {
		ClassLoader cl = spooner.processedAndDumpedToClassLoader(processor);
		TestCasesListener listener = new TestCasesListener();
		Map<String, Boolean> resultsPerNameOfTest = new HashMap<>();
		Map<String, Map<String, Map<Integer, Boolean>>> linesExecutedPerTestNames = new HashMap<>();
		for (int i = 0; i < testClasses.length; i++) {
			try {
				for (String methodName : this.getMethodsNames(testClasses[i], cl.loadClass(testClasses[i]))) {
					String testName = methodName;
					TestSuiteExecution.runTest(testName, cl, listener, config);
					linesExecutedPerTestNames.put(testName, copyExecutedLinesAndReinit(_Instrumenting.lines));
					//Since we executed one test at the time, the listener contains one and only one TestCase
					resultsPerNameOfTest.put(testName, listener.numberOfFailedTests() == 0);
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		buildTestResultPerSourceLocation(resultsPerNameOfTest, linesExecutedPerTestNames);
	}

	/**
	 * @param resultsPerNameOfTest
	 * @param linesExecutedPerTestNames
	 */
	private void buildTestResultPerSourceLocation(Map<String, Boolean> resultsPerNameOfTest, Map<String, Map<String, Map<Integer, Boolean>>> linesExecutedPerTestNames) {
		this.countPerSourceLocation = new HashMap<>();
		for (String testName : linesExecutedPerTestNames.keySet()) {
			for (String className : linesExecutedPerTestNames.get(testName).keySet()) {
				Map<Integer, Boolean> coveredLines = linesExecutedPerTestNames.get(testName).get(className);
				for (Integer line : coveredLines.keySet()) {
					SourceLocation sourceLocation = new SourceLocation(className, line);
					if (coveredLines.get(line)) {
						if (!this.countPerSourceLocation.containsKey(sourceLocation))
							this.countPerSourceLocation.put(sourceLocation, new ArrayList<TestResult>());
						this.countPerSourceLocation.get(sourceLocation).add(new TestResult(testName, resultsPerNameOfTest.get(testName)));
					}
				}
			}
		}
	}

	/**
	 * using reflection to build the name of all test methods to be run
	 * @param fullQualifiedNameOfTest
	 * @param classOfTestCase
	 * @return
	 */
	private List<String> getMethodsNames(String fullQualifiedNameOfTest, Class classOfTestCase) {
		List<String> methodsNames = new ArrayList<>();
		for (Method method : classOfTestCase.getMethods()) {
			if (method.getAnnotation(Test.class) != null)
				methodsNames.add(fullQualifiedNameOfTest + "#" + method.getName());
		}
		return methodsNames;
	}

	/**
	 * This method copy the original map given, and set all its boolean at false to re-run a new test case
	 * @param original map to copy and reinit
	 * @return a copy of the map original
	 */
	private Map<String, Map<Integer, Boolean>> copyExecutedLinesAndReinit(Map<String, Map<Integer, Boolean>> original) {
		Map<String, Map<Integer, Boolean>> copy = new HashMap<>();
		for (String s : original.keySet()) {
			Map<Integer, Boolean> internalCopy = new HashMap<>();
			for (Integer i : original.get(s).keySet()) {
				internalCopy.put(i, original.get(s).get(i));
				original.get(s).put(i, false);
			}
			copy.put(s, internalCopy);
		}
		return copy;
	}

	@Override
	public Map<SourceLocation, List<com.gzoltar.core.instr.testing.TestResult>> getTestListPerStatement() {
//		return this.countPerSourceLocation;
		return null;
	}

	@Override
	public Collection<Statement> getStatements() {
		return null;
	}
}
