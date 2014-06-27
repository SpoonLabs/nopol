package fr.inria.lille.commons.trace;

import java.util.Collection;

import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.nopol.synth.InputOutputValues;

public class TestValuesCollectorListener<T> extends TestCasesListener {

	public TestValuesCollectorListener(final InputOutputValues matrix, T fixedValue) {
		this.matrix = matrix;
		this.fixedValue = fixedValue;
		specifications = SetLibrary.newHashSet();
		cleanUp();
	}
	
	public Collection<Specification<T>> specifications() {
		return specifications;
	}

	@Override
	protected void processSuccessfulRun(TestCase testCase) {
		if (! RuntimeValues.isEmpty()) {
			matrix.addValues(RuntimeValues.collectedValues(), fixedValue);
		}
		if (! RuntimeValues.isEmpty()) {
			specifications().add(new Specification<>(RuntimeValues.collectedValuesMap(), fixedValue()));
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
	
	private T fixedValue() {
		return fixedValue;
	}
	
	private T fixedValue;
	private InputOutputValues matrix;
	private Collection<Specification<T>> specifications;
}