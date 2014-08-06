package fr.inria.lille.nopol.synth;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import fr.inria.lille.commons.trace.RuntimeValues;

final class ResultMatrixBuilderListener extends RunListener {

	final InputOutputValues matrix;

	/**
	 * Optimist...
	 */
	boolean success = true;

	final boolean value;
	
	int mapID;

	/**
	 * @param matrix
	 */
	ResultMatrixBuilderListener(final InputOutputValues matrix, final boolean value, int mapID) {
		this.matrix = matrix;
		this.value = value;
		this.mapID = mapID;
	}

	private void cleanUp() {
		RuntimeValues.discardCollectedValues();
	}

	/**
	 *
	 */
	private void processSuccessfulRun() {
		if (! RuntimeValues.isEmpty()) {
			this.matrix.addValues(RuntimeValues.collectedValues(), this.value);
		}
	}

	/**
	 * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
	 */
	@Override
	public void testFailure(final Failure failure) throws Exception {
		this.success = false;
	}

	/**
	 * @see org.junit.runner.notification.RunListener#testFinished(org.junit.runner.Description)
	 */
	@Override
	public void testFinished(final Description description) throws Exception {
		if (this.success) {
			this.processSuccessfulRun();
		} else {
			// hope for the best
			this.success = true;
		}
		this.cleanUp();
	}
}
