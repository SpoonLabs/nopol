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
	
	public Collection<SourcePosition> loopsAboveThresholdFor(String[] testClasses) {
		return loopsAboveThresholdFor(testClasses, nullRunListener());
	}
	
	public Collection<SourcePosition> loopsAboveThresholdFor(String[] testClasses, RunListener listener) {
		execute(testClasses, listener);
		return monitor().loopsAboveThreshold();
	}
	
	public Map<TestCase, Integer> invocationsPerTest(SourcePosition loopPosition, Collection<TestCase> testCases) {
		Map<TestCase, Integer> invocations = MapLibrary.newHashMap();
		for (TestCase testCase : testCases) {
			execute(testCase, loopPosition, 0);
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
	
	public boolean execute(Map<TestCase, Integer> testsWithThresholds, SourcePosition loopPosition, RunListener listener) {
		boolean success = true;
		for (TestCase testCase : testsWithThresholds.keySet()) {
			Result result = execute(testCase, loopPosition, testsWithThresholds.get(testCase), listener);
			success = success && result.wasSuccessful();
		}
		return success; 
	}
	
	protected ClassLoader classLoader() {
		return classLoader;
	}
	
	protected CentralLoopMonitor monitor() {
		return monitor;
	}
	
	private RunListener nullRunListener() {
		return NullRunListener.instance();
	}
	
	private ClassLoader classLoader;
	private CentralLoopMonitor monitor;
}
