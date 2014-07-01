package fr.inria.lille.infinitel.loop;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.suite.NullRunListener;
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

	public Map<TestCase, Integer> numberOfIterationsByTestIn(SourcePosition loopPosition, Collection<TestCase> successfulTests, Collection<TestCase> failedTests) {
		Map<TestCase, Integer> thresholdMap = MapLibrary.newHashMap();
		findTracedThresholds(testsUsingLoop(loopPosition, successfulTests), loopPosition, thresholdMap);
		findThresholdsInSteps(testsUsingLoop(loopPosition, failedTests), loopPosition, thresholdMap);
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
		int oldThreshold = monitor().setThresholdOf(loopPosition, 0).intValue();
		execute(testCase, NullRunListener.instance(), loopPosition);
		monitor().setThresholdOf(loopPosition, oldThreshold);
		boolean usedLoop = ! monitor().iterationRecordOf(loopPosition).isEmpty();
		return usedLoop;
	}
	
	private void findTracedThresholds(Collection<TestCase> tests, SourcePosition loopPosition, Map<TestCase, Integer> thresholdMap) {
		for (TestCase testCase : tests) {
			assertTrue("Wrong threshold for passing test " + testCase, execute(testCase, listener(), loopPosition).wasSuccessful());
			List<Integer> iterationRecord = monitor().iterationRecordOf(loopPosition);
			if (! iterationRecord.isEmpty()) {
				int tracedThreshold = ListLibrary.last(iterationRecord);
				thresholdMap.put(testCase, tracedThreshold);
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
			monitor().setThresholdOf(loopPosition, iterations); // XXX We assume the infinite loop is invoked once? Here we set the threshold for the whole test execution
			Result result = execute(testCase, listener(), loopPosition);
			if (result.wasSuccessful()) {
				iterationsNeeded.put(testCase, iterations);
				return;
			}
			listener().failedTests().remove(testCase);
		}
		assertFalse("Did not find threshold for " + testCase, true);
	}

	private Result execute(TestCase testCase, RunListener listener, SourcePosition loopPosition) {
		monitor().enable(loopPosition);
		Result result = TestSuiteExecution.runTestCase(testCase, classLoader(), listener);
		monitor().disable(loopPosition);
		return result;
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
