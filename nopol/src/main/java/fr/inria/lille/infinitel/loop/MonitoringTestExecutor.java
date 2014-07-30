package fr.inria.lille.infinitel.loop;

import static fr.inria.lille.commons.suite.TestSuiteExecution.runCasesIn;
import static fr.inria.lille.commons.suite.TestSuiteExecution.runTestCase;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.Table;
import fr.inria.lille.commons.suite.NullRunListener;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.infinitel.loop.counters.CentralLoopMonitor;

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
		return invocationsPerTest(asList(loopPosition), testCases).row(loopPosition);
	}
	
	public Table<SourcePosition, TestCase, Integer> invocationsPerTest(Collection<SourcePosition> loops, Collection<TestCase> testCases) {
		Table<SourcePosition, TestCase, Integer> invocationsPerTest = Table.newTable(loops);
		for (TestCase testCase : testCases) {
			execute(testCase, loops);
			for (SourcePosition loop : loops) {
				invocationsPerTest.put(loop, testCase, monitor().numberOfRecords(loop));
			}
		}
		return invocationsPerTest;
	}
	
	public Result execute(TestCase testCase, SourcePosition loopPosition, Number threshold) {
		return execute(testCase, loopPosition, threshold, nullRunListener());
	}

	public Result execute(TestCase testCase, Collection<SourcePosition> loops) {
		return execute(testCase, loops, nullRunListener());
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
	
	public Result execute(TestCase testCase, Collection<SourcePosition> loops, RunListener listener) {
		monitor().enable(loops);
		Result result = runTestCase(testCase, classLoader(), listener);
		monitor().disable(loops);
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
