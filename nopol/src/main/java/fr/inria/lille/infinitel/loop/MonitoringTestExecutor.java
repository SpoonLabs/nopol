package fr.inria.lille.infinitel.loop;

import static fr.inria.lille.commons.suite.TestSuiteExecution.runCasesIn;
import static fr.inria.lille.commons.suite.TestSuiteExecution.runTestCase;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.suite.NullRunListener;
import fr.inria.lille.commons.suite.TestCase;

public class MonitoringTestExecutor {
	
	public MonitoringTestExecutor(ClassLoader classLoader, CentralLoopMonitor monitor) {
		this.monitor = monitor;
		this.classLoader = classLoader;
		monitor().disableAll();
	}
	
	public Collection<SourcePosition> allLoops() {
		return monitor().allLoops();
	}
	
	public Collection<SourcePosition> loopsReachingThresholdFor(String[] testClasses) {
		return loopsReachingThresholdFor(testClasses, nullRunListener());
	}
	
	public Collection<SourcePosition> loopsReachingThresholdFor(String[] testClasses, RunListener listener) {
		execute(testClasses, listener);
		return monitor().loopsReachingThreshold();
	}
	
	public Map<TestCase, Integer> invocationsPerTest(SourcePosition loopPosition, Collection<TestCase> testCases) {
		Map<TestCase, Integer> invocations = MapLibrary.newHashMap();
		for (TestCase testCase : testCases) {
			execute(testCase, loopPosition);
			invocations.put(testCase, monitor().numberOfRecords(loopPosition));
		}
		return invocations;
	}
	
	public Result execute(TestCase testCase, SourcePosition loopPosition, Number threshold) {
		return execute(testCase, loopPosition, threshold, nullRunListener());
	}

	public Result execute(TestCase testCase, SourcePosition loopPosition) {
		return execute(testCase, loopPosition, nullRunListener());
	}
	
	public Result execute(String[] testClasses) {
		return execute(testClasses, nullRunListener());
	}
	
	public Result execute(TestCase testCase, SourcePosition loopPosition, Number threshold, RunListener listener) {
		Number oldThreshold = monitor().setThresholdOf(loopPosition, threshold);
		Result result = execute(testCase, loopPosition, listener);
		monitor().setThresholdOf(loopPosition, oldThreshold);
		return result;
	}
	
	public Result execute(TestCase testCase, SourcePosition loopPosition, RunListener listener) {
		monitor().enable(loopPosition);
		Result result = runTestCase(testCase, classLoader(), listener);
		monitor().disable(loopPosition);
		return result;
	}
	
	public Result execute(String[] testClasses, RunListener listener) {
		monitor().enableAll();
		Result result = runCasesIn(testClasses, classLoader(), listener);
		monitor().disableAll();
		return result;
	}
	
	public boolean execute(Map<TestCase, Integer> iterationsByTest, SourcePosition loopPosition, RunListener listener) {
		boolean success = true;
		for (TestCase testCase : iterationsByTest.keySet()) {
			Result result = execute(testCase, loopPosition, iterationsByTest.get(testCase), listener);
			success = success && result.wasSuccessful();
		}
		return success; 
	}

	public CentralLoopMonitor monitor() {
		return monitor;
	}
	
	protected ClassLoader classLoader() {
		return classLoader;
	}
	
	private RunListener nullRunListener() {
		return NullRunListener.instance();
	}
	
	private ClassLoader classLoader;
	private CentralLoopMonitor monitor;
}
