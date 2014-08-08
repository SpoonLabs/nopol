package fr.inria.lille.infinitel.mining;

import static fr.inria.lille.commons.suite.TestSuiteExecution.runCasesIn;
import static fr.inria.lille.commons.suite.TestSuiteExecution.runTestCase;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import fr.inria.lille.commons.collections.Table;
import fr.inria.lille.commons.suite.NullRunListener;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.utils.Singleton;
import fr.inria.lille.infinitel.instrumenting.CompoundLoopMonitor;
import fr.inria.lille.infinitel.loop.While;

public class MonitoringTestExecutor {
	
	public MonitoringTestExecutor(ClassLoader classLoader, CompoundLoopMonitor monitor) {
		this.monitor = monitor;
		this.classLoader = classLoader;
		monitor().disableAll();
	}
	
	public Collection<While> allLoops() {
		return monitor().allLoops();
	}
	
	public Collection<While> loopsReachingThresholdFor(String[] testClasses) {
		return loopsReachingThresholdFor(testClasses, nullRunListener());
	}
	
	public Collection<While> loopsReachingThresholdFor(String[] testClasses, RunListener listener) {
		execute(testClasses, listener);
		return monitor().loopsReachingThreshold();
	}
	
	public Map<TestCase, Integer> invocationsPerTest(While loop, Collection<TestCase> testCases) {
		return invocationsPerTest(asList(loop), testCases).row(loop);
	}
	
	public Table<While, TestCase, Integer> invocationsPerTest(Collection<While> loops, Collection<TestCase> testCases) {
		Table<While, TestCase, Integer> invocationsPerTest = Table.newTable(loops);
		for (TestCase testCase : testCases) {
			execute(testCase, loops);
			for (While loop : loops) {
				invocationsPerTest.put(loop, testCase, monitor().numberOfRecordsIn(loop));
			}
		}
		return invocationsPerTest;
	}
	
	public Result execute(TestCase testCase, While loop, Number threshold) {
		return execute(testCase, loop, threshold, nullRunListener());
	}

	public Result execute(TestCase testCase, Collection<While> loops) {
		return execute(testCase, loops, nullRunListener());
	}
	
	public Result execute(TestCase testCase, While loop) {
		return execute(testCase, loop, nullRunListener());
	}
	
	public Result execute(String[] testClasses) {
		return execute(testClasses, nullRunListener());
	}
	
	public Result execute(TestCase testCase, While loop, Number threshold, RunListener listener) {
		Number oldThreshold = monitor().setThresholdOf(loop, threshold);
		Result result = execute(testCase, loop, listener);
		monitor().setThresholdOf(loop, oldThreshold);
		return result;
	}
	
	public Result execute(TestCase testCase, While loop, RunListener listener) {
		monitor().enable(loop);
		Result result = runTestCase(testCase, classLoader(), listener);
		monitor().disable(loop);
		return result;
	}
	
	public Result execute(TestCase testCase, Collection<While> loops, RunListener listener) {
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
	
	public boolean execute(Map<TestCase, Integer> iterationsByTest, While loop, RunListener listener) {
		boolean success = true;
		for (TestCase testCase : iterationsByTest.keySet()) {
			Result result = execute(testCase, loop, iterationsByTest.get(testCase), listener);
			success = success && result.wasSuccessful();
		}
		return success; 
	}

	public CompoundLoopMonitor monitor() {
		return monitor;
	}
	
	protected ClassLoader classLoader() {
		return classLoader;
	}
	
	private RunListener nullRunListener() {
		return Singleton.of(NullRunListener.class);
	}
	
	private ClassLoader classLoader;
	private CompoundLoopMonitor monitor;
}
