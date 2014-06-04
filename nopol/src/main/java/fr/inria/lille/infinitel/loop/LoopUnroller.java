package fr.inria.lille.infinitel.loop;

import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.spoon.SpoonClassLoader;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestSuiteExecution;

public class LoopUnroller {

	public LoopUnroller(ProjectReference project, Number threshold) {
		this.project = project;
		this.threshold = threshold.intValue();
	}

	public Map<TestCase, Integer> thresholdForEach(Collection<TestCase> successfulTests, Collection<TestCase> failedTests, SourcePosition loopPosition) {
		Map<TestCase, Integer> thresholdMap = MapLibrary.newHashMap();
		IterationsAuditor auditor = IterationsAuditor.newInstance(loopPosition, threshold());
		Map<String, Class<?>> processedClasses = SpoonClassLoader.allClassesTranformedWith(auditor, project().sourceFolder());
		findTracedThresholds(successfulTests, auditor, processedClasses, thresholdMap);
		findThresholdsInSteps(failedTests, auditor, processedClasses, thresholdMap);
		return thresholdMap;
	}
	
	private void findTracedThresholds(Collection<TestCase> tests, IterationsAuditor auditor, Map<String, Class<?>> processedClasses, Map<TestCase, Integer> thresholdMap) {
		for (TestCase testCase : tests) {
			executionResult(testCase, processedClasses);
			int tracedThreshold = CollectionLibrary.last(auditor.iterationsRecord());
			thresholdMap.put(testCase, tracedThreshold);
		}
	}

	private void findThresholdsInSteps(Collection<TestCase> tests, IterationsAuditor auditor, Map<String, Class<?>> processedClasses, Map<TestCase, Integer> thresholdMap) {
		for (TestCase testCase : tests) {
			findThreshold(auditor, testCase, processedClasses, thresholdMap);
		}
	}

	private void findThreshold(IterationsAuditor auditor, TestCase testCase, Map<String, Class<?>> processedClasses, Map<TestCase, Integer> iterationsNeeded) {
		for (int iterations = 0; iterations < threshold(); iterations += 1) {
			auditor.setThreshold(iterations); // XXX We assume the infinite loop is invoked once? Here we set the threshold for the whole test execution
			Result result = executionResult(testCase, processedClasses);
			if (result.wasSuccessful()) {
				iterationsNeeded.put(testCase, iterations);
				break;
			}
		}
	}

	private Result executionResult(TestCase testCase, Map<String, Class<?>> processedClasses) {
		return TestSuiteExecution.runTestCase(testCase, project().classpath(), processedClasses);
	}
	
	public int threshold() {
		return threshold;
	}

	public ProjectReference project() {
		return project;
	}
	
	private int threshold;
	private ProjectReference project;
}
