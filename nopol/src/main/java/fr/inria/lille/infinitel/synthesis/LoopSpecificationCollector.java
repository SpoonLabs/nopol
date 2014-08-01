package fr.inria.lille.infinitel.synthesis;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.trace.IterationRuntimeValues;
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
			addTestSpecifications(specifications, testThreshold);
		}
		return specifications;
	}

	protected void executeCollectingRuntimeValues(TestCase testCase, While loop, Integer testThreshold) {
		logDebug(logger, format("[Executing %s to collect runtime values in %s]", testCase.toString(), loop.toString()));
		runtimeValues().enable();
		testExecutor().execute(testCase, loop, testThreshold);
		runtimeValues().disable();
	}
	
	protected void addTestSpecifications(Collection<Specification<Boolean>> specifications, Integer loopEntrances) {
		for (int iteration = 0; iteration < loopEntrances; iteration += 1) {
			addTestSpecification(specifications, iteration, true);
		}
		if (runtimeValues().inputsSize() > loopEntrances) {
			addTestSpecification(specifications, loopEntrances, false);
		}
	}

	protected void addTestSpecification(Collection<Specification<Boolean>> specifications, int iterationNumber, boolean expectedOutput) {
		Map<String, Object> values = runtimeValues().inputsFor(iterationNumber);
		specifications.add(new Specification<>(values, expectedOutput));
	}
	
	private IterationRuntimeValues runtimeValues() {
		return IterationRuntimeValues.instance();
	}
	
	private MonitoringTestExecutor testExecutor() {
		return testExecutor;
	}
	
	private MonitoringTestExecutor testExecutor;
	private static Logger logger = newLoggerFor(LoopSpecificationCollector.class);
}
