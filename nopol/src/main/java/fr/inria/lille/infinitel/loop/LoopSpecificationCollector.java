package fr.inria.lille.infinitel.loop;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.trace.IterationRuntimeValues;
import fr.inria.lille.commons.trace.Specification;

public class LoopSpecificationCollector {

	public LoopSpecificationCollector(MonitoringTestExecutor testExecutor) {
		this.testExecutor = testExecutor;
	}
	
	public Collection<Specification<Boolean>> testSpecifications(Map<TestCase, Integer> testsAndThresholds, SourcePosition loopPosition) {
		Collection<Specification<Boolean>> specifications = SetLibrary.newHashSet();
		for (TestCase testCase : testsAndThresholds.keySet()) {
			Integer threshold = testsAndThresholds.get(testCase);
			executeCollectingRuntimeValues(testCase, loopPosition, threshold);
			addTestSpecifications(specifications, threshold);
		}
		return specifications;
	}

	protected void executeCollectingRuntimeValues(TestCase testCase, SourcePosition loopPosition, Integer threshold) {
		logDebug(logger, format("[Executing %s to collect runtime values in %s]", testCase.toString(), loopPosition.toString()));
		runtimeValues().enable();
		testExecutor().execute(testCase, loopPosition, threshold);
		runtimeValues().disable();
	}
	
	protected void addTestSpecifications(Collection<Specification<Boolean>> specifications, Integer threshold) {
		for (int iteration = 0; iteration < threshold; iteration += 1) {
			addTestSpecification(specifications, iteration, true);
		}
		addTestSpecification(specifications, threshold, false);
	}

	protected void addTestSpecification(Collection<Specification<Boolean>> specifications, int iterationNumber, boolean expectedOutput) {
		Map<String, Object> values = runtimeValues().valuesCacheFor(iterationNumber);
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
