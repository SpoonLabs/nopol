package fr.inria.lille.commons.trace;

import spoon.reflect.code.CtCodeElement;
import fr.inria.lille.commons.utils.Singleton;

public class IterationRuntimeValuesProcessor<T extends CtCodeElement> extends RuntimeValuesProcessor<T> {

	public IterationRuntimeValuesProcessor(String iterationVariableName) {
		super();
		this.iterationVariableName = iterationVariableName;
	}

	@Override
	protected String valueCollectingSnippet(String variableName) {
		return Singleton.of(IterationRuntimeValues.class).collectValueInvocation(iterationVariableName(), variableName);
	}
	
	private String iterationVariableName() {
		return iterationVariableName;
	}
	
	private String iterationVariableName;
}
