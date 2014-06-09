package fr.inria.lille.infinitel.loop;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestSuiteExecution;

public class LoopUnroller {

	public LoopUnroller(LoopStatementsMonitor monitor, ClassLoader classLoader) {
		this.monitor = monitor;
		this.classLoader = classLoader;
	}

	public Map<TestCase, Integer> thresholdForEach(Collection<TestCase> successfulTests, Collection<TestCase> failedTests, SourcePosition loopPosition) {
		Map<TestCase, Integer> thresholdMap = MapLibrary.newHashMap();
		findTracedThresholds(successfulTests, loopPosition, thresholdMap);
		findThresholdsInSteps(failedTests, loopPosition, thresholdMap);
		return thresholdMap;
	}
	
	private void findTracedThresholds(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap) {
		for (TestCase testCase : tests) {
			execute(testCase);
			int tracedThreshold = ListLibrary.last(monitor().auditorIn(loopPosition).iterationsRecord());
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
			monitor().auditorIn(loopPosition).setThreshold(iterations); // XXX We assume the infinite loop is invoked once? Here we set the threshold for the whole test execution
			Result result = execute(testCase);
			if (result.wasSuccessful()) {
				iterationsNeeded.put(testCase, iterations);
				break;
			}
		}
	}

	private Result execute(TestCase testCase) {
		return TestSuiteExecution.runTestCase(testCase, classLoader());
	}
	
	public LoopStatementsMonitor monitor() {
		return monitor;
	}
	
	public ClassLoader classLoader() {
		return classLoader;
	}
	
	private LoopStatementsMonitor monitor;
	private ClassLoader classLoader;
}
