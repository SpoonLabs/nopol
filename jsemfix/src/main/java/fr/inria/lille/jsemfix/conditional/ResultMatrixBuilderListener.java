package fr.inria.lille.jsemfix.conditional;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.google.common.collect.Table;

import fr.inria.lille.jsemfix.test.Test;
import fr.inria.lille.jsemfix.test.junit.JUnitTest;

final class ResultMatrixBuilderListener extends RunListener {

	final Table<Test, Boolean, Result> matrix;

	final boolean value;

	/**
	 * @param matrix
	 */
	ResultMatrixBuilderListener(final Table<Test, Boolean, Result> matrix, final boolean value) {
		this.matrix = matrix;
		this.value = value;
	}

	/**
	 * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
	 */
	@Override
	public void testFailure(final Failure failure) throws Exception {
		this.matrix.put(new JUnitTest(failure.getDescription()), this.value, Result.FAIL);
	}

	/**
	 * @see org.junit.runner.notification.RunListener#testFinished(org.junit.runner.Description)
	 */
	@Override
	public void testFinished(final Description description) throws Exception {
		JUnitTest desc = new JUnitTest(description);
		if (null == this.matrix.get(desc, this.value)) {
			this.matrix.put(desc, this.value, Result.OK);
		}
	}
}
