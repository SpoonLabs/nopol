package fr.inria.lille.repair.infinitel.mining;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static xxl.java.library.LoggerLibrary.logDebug;
import static xxl.java.library.LoggerLibrary.loggerFor;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;
import org.slf4j.Logger;

import xxl.java.container.classic.MetaMap;
import xxl.java.junit.TestCase;
import fr.inria.lille.repair.infinitel.instrumenting.CompoundLoopMonitor;
import fr.inria.lille.repair.infinitel.loop.While;

public class LoopTestThresholdFinder {
	
	public LoopTestThresholdFinder(MonitoringTestExecutor testExecutor) {
		this.testExecutor = testExecutor;
	}

	public Map<TestCase, Integer> thresholdsByTest(While loop, Collection<TestCase> failedTests, Collection<TestCase> successfulTests) {
		Map<TestCase, Integer> thresholdMap = MetaMap.newHashMap();
		int threshold = monitor().threshold();
		findThresholdsFromExecution(successfulTests, loop, thresholdMap, threshold);
		findThresholdsProbing(failedTests, loop, thresholdMap, threshold);
		return thresholdMap;
	}
	
	protected void findThresholdsFromExecution(Collection<TestCase> tests, While loop, Map<TestCase, Integer> thresholdMap, Integer threshold) {
		for (TestCase testCase : tests) {
			logDebug(logger(), format("[Executing %s to get test threshold in %s]", testCase.toString(), loop.toString()));
			Result result = testExecutor().execute(testCase, loop);
			assertTrue(format("Could not find threshold for %s, it is a faling test", testCase), result.wasSuccessful());
			Integer lastRecord = monitor().lastRecordIn(loop);
			if (lastRecord.equals(threshold)) {
				probeTestThreshold(testCase, loop, thresholdMap, threshold);
			} else {
				thresholdMap.put(testCase, lastRecord);
			}
		}
	}
	
	protected void findThresholdsProbing(Collection<TestCase> tests, While loop, Map<TestCase, Integer> thresholdMap, Integer threshold) {
		for (TestCase testCase : tests) {
			logDebug(logger(), format("[Finding test threshold of %s in %s]", testCase.toString(), loop.toString()));
			probeTestThreshold(testCase, loop, thresholdMap, threshold);
		}
	}

	protected void probeTestThreshold(TestCase testCase, While loop, Map<TestCase, Integer> thresholdMap, Integer threshold) {
		for (int testThreshold = 0; testThreshold <= threshold; testThreshold += 1) {
			Result result = testExecutor().execute(testCase, loop, testThreshold);
			if (result.wasSuccessful()) {
				thresholdMap.put(testCase, testThreshold);
				return;
			}
		}
		fail("Could not find test threshold for " + testCase);
	}
	
	private CompoundLoopMonitor monitor() {
		return testExecutor().monitor();
	}
	
	private MonitoringTestExecutor testExecutor() {
		return testExecutor;
	}
	
	private Logger logger() {
		return loggerFor(this);
	}
	
	private MonitoringTestExecutor testExecutor;
}
