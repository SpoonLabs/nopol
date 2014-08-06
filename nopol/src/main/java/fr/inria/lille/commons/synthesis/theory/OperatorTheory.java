package fr.inria.lille.commons.synthesis.theory;

import static java.util.Arrays.asList;

import java.util.Collection;

import fr.inria.lille.commons.synthesis.operator.Operator;

public abstract class OperatorTheory {

	public OperatorTheory(Operator<?>... operators) {
		this(asList(operators));
	}
	
	public OperatorTheory(Collection<Operator<?>> operators) {
		this.operators = operators;
	}
	
	public Collection<Operator<?>> operators() {
		return operators;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + operators();
	}
	
	private Collection<Operator<?>> operators;
}