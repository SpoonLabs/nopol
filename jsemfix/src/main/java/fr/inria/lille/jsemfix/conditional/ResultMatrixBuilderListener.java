package fr.inria.lille.jsemfix.conditional;

import java.util.Map.Entry;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

final class ResultMatrixBuilderListener extends RunListener {

	final InputOutputData matrix;

	/**
	 * Optimist...
	 */
	boolean success = true;

	final boolean value;

	/**
	 * @param matrix
	 */
	ResultMatrixBuilderListener(final InputOutputData matrix, final boolean value) {
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
		this.matrix.addOutputValue(this.value);
		for (Entry<String, Object> entry : ValuesCollector.getValues()) {
			this.matrix.addInputValue(entry.getKey(), entry.getValue());
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
