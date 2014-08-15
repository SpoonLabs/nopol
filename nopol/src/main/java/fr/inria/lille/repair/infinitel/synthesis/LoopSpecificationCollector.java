package fr.inria.lille.repair.infinitel.synthesis;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;

import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.commons.trace.SpecificationTestCasesListener;
import fr.inria.lille.commons.utils.Function;
import fr.inria.lille.repair.infinitel.loop.While;
import fr.inria.lille.repair.infinitel.mining.MonitoringTestExecutor;

public class LoopSpecificationCollector {

	public LoopSpecificationCollector(MonitoringTestExecutor testExecutor) {
		this.testExecutor = testExecutor;
	}
	
	public Collection<Specification<Boolean>> testSpecifications(Map<TestCase, Integer> thresholdsByTest, While loop) {
		Collection<Specification<Boolean>> specifications = SetLibrary.newHashSet();
		for (TestCase testCase : thresholdsByTest.keySet()) {
			Integer testThreshold = thresholdsByTest.get(testCase);
			addSpecificationsAfterExecution(specifications, testCase, loop, testThreshold);
		}
		return specifications;
	}

	protected void addSpecificationsAfterExecution(Collection<Specification<Boolean>> specifications, TestCase testCase, While loop, int testThreshold) {
		RuntimeValues runtimeValues = testExecutor().monitor().runtimeValuesOf(loop);
		SpecificationTestCasesListener<Boolean> listener = new SpecificationTestCasesListener<Boolean>(runtimeValues, outputForEachTrace(testThreshold));
		Result result = testExecutor().execute(testCase, loop, testThreshold, listener);
		assertTrue("Test failure during runtime collection", result.wasSuccessful());
		specifications.addAll(listener.specifications());
	}
	
	protected Function<Integer, Boolean> outputForEachTrace(final int loopEntrances) {
		return new Function<Integer, Boolean>() {
			@Override
			public Boolean outputFor(Integer trace) {
				return trace < loopEntrances;
			}
		};
	}
	
	private MonitoringTestExecutor testExecutor() {
		return testExecutor;
	}
	
	private MonitoringTestExecutor testExecutor;
}
