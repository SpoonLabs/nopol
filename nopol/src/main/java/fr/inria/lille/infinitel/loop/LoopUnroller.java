package fr.inria.lille.infinitel.loop;

import static java.lang.String.format;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.suite.TestCase;

public class LoopUnroller {
	
	public LoopUnroller(MonitoringTestExecutor testExecutor) {
		this.testExecutor = testExecutor;
	}

	public Map<TestCase, Integer> correctIterationsByTestIn(SourcePosition loopPosition, Collection<TestCase> successfulTests, Collection<TestCase> failedTests) {
		Map<TestCase, Integer> thresholdMap = MapLibrary.newHashMap();
		Collection<TestCase> successfulTestsUsingLoop = testsUsingLoop(loopPosition, successfulTests);
		Collection<TestCase> failedTestsUsingLoop = testsUsingLoop(loopPosition, failedTests);
		findTracedThresholds(successfulTestsUsingLoop, loopPosition, thresholdMap);
		findThresholdsInSteps(failedTestsUsingLoop, loopPosition, thresholdMap);
		return thresholdMap;
	}
	
	public Collection<TestCase> testsUsingLoop(SourcePosition loopPosition, Collection<TestCase> testCases) {
		Collection<TestCase> testsUsingLoop = ListLibrary.newLinkedList();
		for (TestCase testCase : testCases) {
			if (testUsesLoop(loopPosition, testCase)) {
				testsUsingLoop.add(testCase);
			}
		}
		return testsUsingLoop;
	}
	
	public boolean testUsesLoop(SourcePosition loopPosition, TestCase testCase) {
		testExecutor().execute(testCase, loopPosition, 0);
		Integer lastRecord = monitor().lastRecordIn(loopPosition);
		if (lastRecord != null) {
			String message = format("Unable to fix infinite loop (%s), it is invoked more than once", loopPosition.toString());
			assertFalse(message, monitor().numberOfRecords(loopPosition) > 1);
			return true;
		}
		return false;
	}
	
	private void findTracedThresholds(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap) {
		Integer threshold = monitor().threshold();
		for (TestCase testCase : tests) {
			Result result = testExecutor().execute(testCase, loopPosition);
			assertTrue("Wrong threshold for passing test " + testCase, result.wasSuccessful());
			Integer tracedThreshold = monitor().lastRecordIn(loopPosition);
			if (tracedThreshold != null) {
				if (tracedThreshold.equals(threshold)) {
					findThreshold(testCase, loopPosition, thresholdMap);
				} else {
					thresholdMap.put(testCase, tracedThreshold);
				}
			}
		}
	}

	private void findThresholdsInSteps(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap) {
		for (TestCase testCase : tests) {
			findThreshold(testCase, loopPosition, thresholdMap);
		}
	}

	private void findThreshold(TestCase testCase, SourcePosition loopPosition, Map<TestCase, Integer> iterationsNeeded) {
		for (int iterations = 0; iterations < monitor().threshold(); iterations += 1) {
			Result result = testExecutor().execute(testCase, loopPosition, iterations);
			if (result.wasSuccessful()) {
				iterationsNeeded.put(testCase, iterations);
				return;
			}
		}
		fail("Did not find threshold for " + testCase);
	}

	private CentralLoopMonitor monitor() {
		return testExecutor().monitor();
	}
	
	private MonitoringTestExecutor testExecutor() {
		return testExecutor;
	}
	
	private MonitoringTestExecutor testExecutor;
}
