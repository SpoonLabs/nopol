package fr.inria.lille.commons.suite.trace;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import fr.inria.lille.nopol.synth.InputOutputValues;

public class TestValuesCollectorListener<T> extends RunListener {

	public TestValuesCollectorListener(final InputOutputValues matrix, T fixedValue) {
		testFailed = false;
		this.matrix = matrix;
		this.fixedValue = fixedValue;
	}
	
	@Override
	public void testFailure(final Failure failure) throws Exception {
		testFailed = true;
	}

	@Override
	public void testFinished(final Description description) throws Exception {
		if (! testFailed) {
			processSuccessfulRun();
		}
		cleanUp();
	}
	
	private void processSuccessfulRun() {
		if (! RuntimeValues.isEmpty()) {
			matrix.addValues(RuntimeValues.collectedValues(), fixedValue);
		}
	}
	
	private void cleanUp() {
		testFailed = false;
		RuntimeValues.discardCollectedValues();
	}
	
	private T fixedValue;
	private boolean testFailed;
	private InputOutputValues matrix;
}
