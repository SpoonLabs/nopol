package fr.inria.lille.commons.synthesis.smt.constraint;

import java.util.Collection;
import java.util.List;

import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.ISymbol;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.OperatorLocationVariable;

public class ConsistencyConstraint extends Constraint {

	public ConsistencyConstraint(SMTLib smtlib) {
		super("Consistency", smtlib);
	}
	
	@Override
	public List<LocationVariable<?>> usedLocationVariables(LocationVariableContainer locationVariableContainer) {
		return (List) locationVariableContainer.copyOfOperators();
	}
	
	@Override
	protected List<IExpr> arguments(LocationVariableContainer locationVariableContainer) {
		return collectExpressions(usedLocationVariables(locationVariableContainer));
	}

	@Override
	protected List<IDeclaration> parameters(LocationVariableContainer locationVariableContainer) {
		return declarationsFromExpressions(usedLocationVariables(locationVariableContainer));
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
		ISymbol distinct = smtlib().distinct();
		for (int index = startingIndex; index < locationVariableContainer.numberOfOperators(); index += 1) {
			OperatorLocationVariable<?> otherOperator = locationVariableContainer.operators().get(index);
			expressions.add(binaryOperationWithExpression(distinct, operator, otherOperator));
		}
		return expressions;
	}
}
