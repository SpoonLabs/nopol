package fr.inria.lille.infinitel.synthesis;

import static fr.inria.lille.commons.utils.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.utils.LoggerLibrary.newLoggerFor;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.infinitel.loop.While;
import fr.inria.lille.infinitel.mining.MonitoringTestExecutor;

public class LoopSpecificationCollector {

	public LoopSpecificationCollector(MonitoringTestExecutor testExecutor) {
		this.testExecutor = testExecutor;
	}
	
	public Collection<Specification<Boolean>> testSpecifications(Map<TestCase, Integer> thresholdsByTest, While loop) {
		Collection<Specification<Boolean>> specifications = SetLibrary.newHashSet();
		for (TestCase testCase : thresholdsByTest.keySet()) {
			Integer testThreshold = thresholdsByTest.get(testCase);
			executeCollectingRuntimeValues(testCase, loop, testThreshold);
			RuntimeValues runtimeValues = testExecutor().monitor().runtimeValuesOf(loop);
			addTestSpecifications(specifications, runtimeValues, testThreshold);
		}
		return specifications;
	}

	protected void executeCollectingRuntimeValues(TestCase testCase, While loop, Integer testThreshold) {
		logDebug(logger, format("[Executing %s to collect runtime values in %s]", testCase.toString(), loop.toString()));
		testExecutor().executeTracing(testCase, loop, testThreshold);
	}
	
	protected void addTestSpecifications(Collection<Specification<Boolean>> specifications, RuntimeValues runtimeValues, Integer loopEntrances) {
		for (int iteration = 0; iteration < loopEntrances; iteration += 1) {
			addTestSpecification(specifications, runtimeValues, iteration, true);
		}
		if (runtimeValues.numberOfTraces() > loopEntrances) {
			addTestSpecification(specifications, runtimeValues, loopEntrances, false);
		}
	}

	protected void addTestSpecification(Collection<Specification<Boolean>> specifications, RuntimeValues runtimeValues, int iterationNumber, boolean expectedOutput) {
		Map<String, Object> values = runtimeValues.valuesFor(iterationNumber);
		specifications.add(new Specification<Boolean>(values, expectedOutput));
	}
	
	private MonitoringTestExecutor testExecutor() {
		return testExecutor;
	}
	
	private MonitoringTestExecutor testExecutor;
	private static Logger logger = newLoggerFor(LoopSpecificationCollector.class);
}
