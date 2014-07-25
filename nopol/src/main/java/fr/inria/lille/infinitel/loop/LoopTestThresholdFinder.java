package fr.inria.lille.infinitel.loop;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;
import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;
import org.slf4j.Logger;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.suite.TestCase;

public class LoopTestThresholdFinder {
	
	public LoopTestThresholdFinder(MonitoringTestExecutor testExecutor) {
		this.testExecutor = testExecutor;
	}

	public Map<TestCase, Integer> thresholdsByTest(SourcePosition loopPosition, Collection<TestCase> failedTests, Collection<TestCase> successfulTests) {
		Map<TestCase, Integer> thresholdMap = MapLibrary.newHashMap();
		int threshold = monitor().threshold();
		findThresholdsFromExecution(successfulTests, loopPosition, thresholdMap, threshold);
		findThresholdsProbing(failedTests, loopPosition, thresholdMap, threshold);
		return thresholdMap;
	}
	
	protected void findThresholdsFromExecution(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap, Integer threshold) {
		for (TestCase testCase : tests) {
			logDebug(logger, format("[Executing %s to get test threshold in %s]", testCase.toString(), loopPosition.toString()));
			Result result = testExecutor().execute(testCase, loopPosition);
			assertTrue(format("Could not find threshold for %s, it is a faling test", testCase), result.wasSuccessful());
			Integer lastRecord = monitor().lastRecordIn(loopPosition);
			if (lastRecord.equals(threshold)) {
				probeTestThreshold(testCase, loopPosition, thresholdMap, threshold);
			} else {
				thresholdMap.put(testCase, lastRecord);
			}
		}
	}
	
	protected void findThresholdsProbing(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap, Integer threshold) {
		for (TestCase testCase : tests) {
			logDebug(logger, format("[Finding test threshold of %s in %s]", testCase.toString(), loopPosition.toString()));
			probeTestThreshold(testCase, loopPosition, thresholdMap, threshold);
		}
	}

	protected void probeTestThreshold(TestCase testCase, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap, Integer threshold) {
		for (int testThreshold = 0; testThreshold <= threshold; testThreshold += 1) {
			Result result = testExecutor().execute(testCase, loopPosition, testThreshold);
			if (result.wasSuccessful()) {
				thresholdMap.put(testCase, testThreshold);
				return;
			}
		}
		fail("Could not find test threshold for " + testCase);
	}
	
	private CentralLoopMonitor monitor() {
		return testExecutor().monitor();
	}
	
	private MonitoringTestExecutor testExecutor() {
		return testExecutor;
	}
	
	private MonitoringTestExecutor testExecutor;
	private static Logger logger = newLoggerFor(LoopTestThresholdFinder.class);
}
