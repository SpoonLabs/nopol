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

public class LoopUnroller {
	
	public LoopUnroller(MonitoringTestExecutor testExecutor) {
		this.testExecutor = testExecutor;
	}

	public Map<TestCase, Integer> correctIterationsByTestIn(SourcePosition loopPosition, Collection<TestCase> failedTests, Collection<TestCase> successfulTests) {
		Map<TestCase, Integer> thresholdMap = MapLibrary.newHashMap();
		findThresholdsInSteps(failedTests, loopPosition, thresholdMap);
		findTracedThresholds(successfulTests, loopPosition, thresholdMap);
		return thresholdMap;
	}
	
	protected void findThresholdsInSteps(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap) {
		for (TestCase testCase : tests) {
			logDebug(logger, format("[Finding iteration threshold of %s in %s]", testCase.toString(), loopPosition.toString()));
			findThreshold(testCase, loopPosition, thresholdMap);
		}
	}

	protected void findThreshold(TestCase testCase, SourcePosition loopPosition, Map<TestCase, Integer> iterationsNeeded) {
		for (int iterations = 0; iterations < monitor().threshold(); iterations += 1) {
			Result result = testExecutor().execute(testCase, loopPosition, iterations);
			if (result.wasSuccessful()) {
				iterationsNeeded.put(testCase, iterations);
				return;
			}
		}
		fail("Did not find threshold for " + testCase);
	}
	
	protected void findTracedThresholds(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap) {
		Integer threshold = monitor().threshold();
		for (TestCase testCase : tests) {
			logDebug(logger, format("[Executing %s to get iteration threshold from run in %s]", testCase.toString(), loopPosition.toString()));
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

	private CentralLoopMonitor monitor() {
		return testExecutor().monitor();
	}
	
	private MonitoringTestExecutor testExecutor() {
		return testExecutor;
	}
	
	private MonitoringTestExecutor testExecutor;
	private static Logger logger = newLoggerFor(LoopUnroller.class);
}
