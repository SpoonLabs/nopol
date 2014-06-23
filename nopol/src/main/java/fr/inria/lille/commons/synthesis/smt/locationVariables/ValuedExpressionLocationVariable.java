package fr.inria.lille.commons.synthesis.smt.locationVariables;

import org.smtlib.IExpr;

import fr.inria.lille.commons.synthesis.expression.ValuedExpression;
import fr.inria.lille.commons.synthesis.smt.SMTLib;

public class ValuedExpressionLocationVariable<T> extends LocationVariable<T> {

	public ValuedExpressionLocationVariable(ValuedExpression<T> valuedExpression, String subexpression, int inputIndex) {
		super(valuedExpression, subexpression);
		this.inputIndex = inputIndex;
	}

	public int index() {
		return inputIndex;
	}
	
	@Override
	public ValuedExpression<T> objectTemplate() {
		return (ValuedExpression<T>) super.objectTemplate();
	}
	
	@Override
	public IExpr encodedLineNumber() {
		return SMTLib.smtlib().numeral(Integer.toString(index()));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + inputIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValuedExpressionLocationVariable<?> other = (ValuedExpressionLocationVariable<?>) obj;
		if (inputIndex != other.inputIndex)
			return false;
		return true;
	}

	private int inputIndex;
}
