package fr.inria.lille.commons.trace;

import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.nopol.synth.InputOutputValues;

public class TestValuesCollectorListener extends TestCasesListener {

	public TestValuesCollectorListener(final InputOutputValues matrix, boolean fixedValue) {
		this.matrix = matrix;
		this.fixedValue = fixedValue;
		cleanUp();
	}

	@Override
	protected void processSuccessfulRun(TestCase testCase) {
		if (! RuntimeValues.isEmpty()) {
			matrix().addValues(RuntimeValues.collectedValues(), fixedValue());
		}
		cleanUp();
	}
	
	@Override
	protected void processFailedRun(TestCase testCase) {
		cleanUp();
	}
	
	private void cleanUp() {
		RuntimeValues.discardCollectedValues();
	}
	
	public InputOutputValues matrix() {
		return matrix;
	}
	
	private boolean fixedValue() {
		return fixedValue;
	}
	
	private boolean fixedValue;
	private InputOutputValues matrix;
}