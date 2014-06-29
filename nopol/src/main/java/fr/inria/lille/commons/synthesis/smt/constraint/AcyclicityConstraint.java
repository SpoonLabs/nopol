package fr.inria.lille.commons.synthesis.smt.constraint;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.lessThan;

import java.util.Collection;
import java.util.List;

import org.smtlib.IExpr;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ParameterLocationVariable;

public class AcyclicityConstraint extends Constraint {

	public AcyclicityConstraint(SMTLib smtlib) {
		super("Acyclicity", smtlib);
	}

	@Override
	public List<LocationVariable<?>> variablesForExpression(LocationVariableContainer container) {
		return container.operatorsAndParameters();
	}
	
	@Override
	protected Collection<IExpr> definitionExpressions(LocationVariableContainer locationVariableContainer) {
		Collection<IExpr> parametersBeforeOperators = ListLibrary.newLinkedList();
		for (ParameterLocationVariable<?> parameter : locationVariableContainer.allParameters()) {
			IExpr parameterExpr = expressionSymbolOf(parameter);
			IExpr operatorExpr = expressionSymbolOf(parameter.operatorLocationVariable());
			parametersBeforeOperators.add(binaryOperation(parameterExpr, lessThan(), operatorExpr));
		}
		return parametersBeforeOperators;
	}

}
