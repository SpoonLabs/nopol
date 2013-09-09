package fr.inria.lille.nopol.synth;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import fr.inria.lille.nopol.synth.collector.ValuesCollector;

final class ResultMatrixBuilderListener extends RunListener {

	final InputOutputValues matrix;

	/**
	 * Optimist...
	 */
	boolean success = true;

	final boolean value;

	/**
	 * @param matrix
	 */
	ResultMatrixBuilderListener(final InputOutputValues matrix, final boolean value) {
		this.matrix = matrix;
		this.value = value;
	}

	private void cleanUp() {
		ValuesCollector.clear();
	}

	/**
	 *
	 */
	private void processSuccessfulRun() {
		if (!ValuesCollector.isEmpty()) {
			this.matrix.addValues(ValuesCollector.getValues(), this.value);
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
