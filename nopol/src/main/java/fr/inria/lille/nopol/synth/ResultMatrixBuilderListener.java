package fr.inria.lille.nopol.synth;

import java.util.Collection;
import java.util.Map.Entry;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import fr.inria.lille.commons.collections.SetLibrary;
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

	@Override
    public void testRunStarted(Description description) throws Exception {
		runtimeValues().enable();
	}
	
	@Override
    public void testRunFinished(Result result) throws Exception {
		runtimeValues().disable();
	}
	
	private void cleanUp() {
		runtimeValues().enable();
	}

	/**
	 *
	 */
	private void processSuccessfulRun() {
		if (! runtimeValues().isEmpty()) {
			Collection<Entry<String, Object>> collected = SetLibrary.newHashSet();
			for (int i = 0 ; i < runtimeValues().numberOfTraces(); i += 1) {
				collected.addAll(runtimeValues().valuesFor(i).entrySet());
			}
			this.matrix.addValues(collected, this.value);
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
	
	private RuntimeValues runtimeValues() {
		return SynthesizerFactory.runtimeValues;
	}
}
