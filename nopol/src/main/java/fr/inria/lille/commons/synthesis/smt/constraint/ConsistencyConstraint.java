package fr.inria.lille.commons.synthesis.smt.constraint;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.distinct;

import java.util.Collection;
import java.util.List;

import org.smtlib.IExpr;

import xxl.java.extensions.collection.ListLibrary;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.OperatorLocationVariable;

public class ConsistencyConstraint extends Constraint {

	public ConsistencyConstraint(SMTLib smtlib) {
		super("Consistency", smtlib);
	}
	
	@Override
	public List<LocationVariable<?>> variablesForExpression(LocationVariableContainer container) {
		return (List) container.operators();
	}
	
	@Override
	protected Collection<IExpr> definitionExpressions(LocationVariableContainer locationVariableContainer) {
		Collection<IExpr> operatorsInDifferentLines = ListLibrary.newLinkedList();
		int index = 1;
		for (OperatorLocationVariable<?> operator : locationVariableContainer.operators()) {
			operatorsInDifferentLines.addAll(differentLineExpressionsFor(operator, locationVariableContainer, index));
			index += 1;
		}
		return operatorsInDifferentLines;
	}

	private Collection<IExpr> differentLineExpressionsFor(OperatorLocationVariable<?> operator, LocationVariableContainer locationVariableContainer, int startingIndex) {
		Collection<IExpr> expressions = ListLibrary.newLinkedList();
		for (int index = startingIndex; index < locationVariableContainer.numberOfOperators(); index += 1) {
			OperatorLocationVariable<?> otherOperator = locationVariableContainer.operators().get(index);
			expressions.add(binaryOperation(expressionSymbolOf(operator), distinct(), expressionSymbolOf(otherOperator)));
		}
		return expressions;
	}
}
