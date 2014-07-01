package fr.inria.lille.commons.trace;

import spoon.reflect.code.CtCodeElement;

public class IterationRuntimeValuesProcessor<T extends CtCodeElement> extends RuntimeValuesProcessor<T> {

	public IterationRuntimeValuesProcessor(String iterationVariableName) {
		super();
		this.iterationVariableName = iterationVariableName;
	}

	@Override
	protected String valueCollectingSnippet(String variableName) {
		return IterationRuntimeValues.instance().collectValueInvocation(iterationVariableName(), variableName);
	}
	
	private String iterationVariableName() {
		return iterationVariableName;
	}
	
	private String iterationVariableName;
}
