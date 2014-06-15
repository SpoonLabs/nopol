package fr.inria.lille.commons.trace;

import org.junit.runner.Description;

import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.nopol.synth.InputOutputValues;

public class TestValuesCollectorListener<T> extends TestCasesListener {

	public TestValuesCollectorListener(final InputOutputValues matrix, T fixedValue) {
		this.matrix = matrix;
		this.fixedValue = fixedValue;
	}

	@Override
	public void testFinished(final Description description) throws Exception {
		super.testFinished(description);
		cleanUp();
	}
	
	@Override
	protected void processSuccessfulRun(TestCase testCase) {
		if (! RuntimeValues.isEmpty()) {
			matrix.addValues(RuntimeValues.collectedValues(), fixedValue);
		}
	}
	
	private void cleanUp() {
		RuntimeValues.discardCollectedValues();
	}
	
	private T fixedValue;
	private InputOutputValues matrix;
}
