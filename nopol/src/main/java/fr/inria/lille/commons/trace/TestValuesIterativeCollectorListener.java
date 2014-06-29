package fr.inria.lille.commons.trace;

import java.util.Collection;
import java.util.Map;

import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;

public class TestValuesIterativeCollectorListener extends TestCasesListener {

	public TestValuesIterativeCollectorListener() {
		cleanUp();
		specifications = SetLibrary.newHashSet();
	}

	@Override
	protected void processSuccessfulRun(TestCase testCase) {
		int lastIteration = runtimeValues().numberOfIterations() - 1;
		for (int iteration = 0; iteration < lastIteration; iteration += 1) {
			addSpecificationFor(iteration, true);
		}
		addSpecificationFor(lastIteration, false);
		cleanUp();
	}
	
	private void addSpecificationFor(int iterationNumber, boolean expectedOutput) {
		Map<String, Object> values = runtimeValues().valuesCacheFor(iterationNumber);
		specifications().add(new Specification<>(values, expectedOutput));
	}
	
	@Override
	protected void processFailedRun(TestCase testCase) {
		cleanUp();
	}
	
	private void cleanUp() {
		runtimeValues().discardCollectedValues();
	}
	
	public Collection<Specification<Boolean>> specifications() {
		return specifications;
	}
	
	private IterationRuntimeValues runtimeValues() {
		return IterationRuntimeValues.instance();
	}
	
	private Collection<Specification<Boolean>> specifications;
}