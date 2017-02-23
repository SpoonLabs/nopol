package fr.inria.lille.localization;

import fil.iagl.opl.cocospoon.processors.WatcherProcessor;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import instrumenting._Instrumenting;
import org.junit.Test;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bdanglot on 9/30/16.
 */
public class DumbFaultLocalizerImpl implements FaultLocalizer {

	protected Map<SourceLocation, List<TestResult>> countPerSourceLocation;

	public DumbFaultLocalizerImpl(NopolContext nopolContext) {
		SpoonedProject spooner = new SpoonedProject(nopolContext.getProjectSources(), nopolContext);
		WatcherProcessor processor = new WatcherProcessor();
		runTests(nopolContext.getProjectTests(), nopolContext, spooner, processor);
	}

	/**
	 * run all testClasses to build the covered code
	 *
	 * @param nopolContext
	 * @param spooner
	 * @param processor
	 */
	protected void runTests(String[] testClasses, NopolContext nopolContext, SpoonedProject spooner, WatcherProcessor processor) {
		ClassLoader cl = spooner.processedAndDumpedToClassLoader(processor);
		TestCasesListener listener = new TestCasesListener();
		Map<String, Boolean> resultsPerNameOfTest = new HashMap<>();
		Map<String, Map<SourceLocation, Boolean>> linesExecutedPerTestNames = new HashMap<>();
		for (int i = 0; i < testClasses.length; i++) {
			try {
				for (String methodName : this.getTestMethods(cl.loadClass(testClasses[i]))) {
					String testMethod = testClasses[i] + "#" + methodName;
					TestSuiteExecution.runTest(testMethod, cl, listener, nopolContext);
					linesExecutedPerTestNames.put(testMethod, copyExecutedLinesAndReinit(_Instrumenting.lines));
					resultsPerNameOfTest.put(testMethod, listener.numberOfFailedTests() == 0);
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		buildTestResultPerSourceLocation(resultsPerNameOfTest, linesExecutedPerTestNames);
	}


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

	/**
	 * using reflection to build the name of all test methods to be run
	 *
	 * @param classOfTestCase
	 * @return
	 */
	protected List<String> getTestMethods(Class classOfTestCase) {
		List<String> methodsNames = new ArrayList<>();
		for (Method method : classOfTestCase.getMethods()) {
			if (method.getAnnotation(Test.class) != null || isPublicTestMethod(method))
				methodsNames.add(method.getName());
		}
		return methodsNames;
	}

	private boolean isPublicTestMethod(Method m) {
		return this.isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	}

	private boolean isTestMethod(Method m) {
		return m.getParameterTypes().length == 0 && m.getName().startsWith("test") && m.getReturnType().equals(Void.TYPE);
	}


	@Override
	public Map<SourceLocation, List<TestResult>> getTestListPerStatement() {
		return this.countPerSourceLocation;
	}

	@Override
	public List<AbstractStatement> getStatements() {
		throw new UnsupportedOperationException();
	}
}
