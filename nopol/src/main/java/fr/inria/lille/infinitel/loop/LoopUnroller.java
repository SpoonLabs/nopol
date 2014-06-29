package fr.inria.lille.infinitel.loop;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.suite.TestSuiteExecution;

public class LoopUnroller {
	
	public LoopUnroller(LoopStatementsMonitor monitor, ClassLoader classLoader, TestCasesListener listener) {
		this.monitor = monitor;
		this.classLoader = classLoader;
		this.listener = listener;
		monitor().disableAll();
	}

	public Map<TestCase, Integer> thresholdForEach(Collection<TestCase> successfulTests, Collection<TestCase> failedTests, SourcePosition loopPosition) {
		Map<TestCase, Integer> thresholdMap = MapLibrary.newHashMap();
		monitor().enable(loopPosition);
		findTracedThresholds(successfulTests, loopPosition, thresholdMap);
		findThresholdsInSteps(failedTests, loopPosition, thresholdMap);
		monitor().disable(loopPosition);
		return thresholdMap;
	}
	
	private void findTracedThresholds(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap) {
		for (TestCase testCase : tests) {
			assertTrue("Wrong threshold for passing test " + testCase, execute(testCase).wasSuccessful());
			int tracedThreshold = ListLibrary.last(monitor().iterationRecordOf(loopPosition));
			thresholdMap.put(testCase, tracedThreshold);
		}
	}

	private void findThresholdsInSteps(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap) {
		for (TestCase testCase : tests) {
			findThreshold(testCase, loopPosition, thresholdMap);
		}
	}

	private void findThreshold(TestCase testCase, SourcePosition loopPosition, Map<TestCase, Integer> iterationsNeeded) {
		for (int iterations = 0; iterations < monitor().threshold(); iterations += 1) {
			monitor().setThresholdOf(loopPosition, iterations); // XXX We assume the infinite loop is invoked once? Here we set the threshold for the whole test execution
			Result result = execute(testCase);
			if (result.wasSuccessful()) {
				iterationsNeeded.put(testCase, iterations);
				return;
			}
			listener().failedTests().remove(testCase);
		}
		assertFalse("Did not find threshold for " + testCase, true);
	}

	private Result execute(TestCase testCase) {
		return TestSuiteExecution.runTestCase(testCase, classLoader(), listener());
	}
	
	public LoopStatementsMonitor monitor() {
		return monitor;
	}
	
	public ClassLoader classLoader() {
		return classLoader;
	}
	
	private TestCasesListener listener() {
		return listener;
	}
	
	private ClassLoader classLoader;
	private LoopStatementsMonitor monitor;
	private TestCasesListener listener;
}
