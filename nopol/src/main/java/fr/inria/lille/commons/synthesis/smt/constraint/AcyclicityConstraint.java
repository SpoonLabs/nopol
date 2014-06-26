package fr.inria.lille.commons.synthesis.smt.constraint;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.lessThan;

import java.util.Collection;
import java.util.List;

import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;

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
	public List<LocationVariable<?>> usedLocationVariables(LocationVariableContainer locationVariableContainer) {
		return locationVariableContainer.copyOfOperatorsAndParameters();
	}
	
	@Override
	protected List<IExpr> invocationArguments(LocationVariableContainer locationVariableContainer) {
		return (List) collectExpressions(usedLocationVariables(locationVariableContainer));
	}

	@Override
	protected List<IDeclaration> parameters(LocationVariableContainer locationVariableContainer) {
		return declarationsFromExpressions(usedLocationVariables(locationVariableContainer));
	}
	
	@Override
	protected Collection<IExpr> definitionExpressions(LocationVariableContainer locationVariableContainer) {
		Collection<IExpr> parametersBeforeOperators = ListLibrary.newLinkedList();
		for (ParameterLocationVariable<?> parameter : locationVariableContainer.allParameters()) {
			parametersBeforeOperators.add(binaryOperationWithExpression(lessThan(), parameter, parameter.operatorLocationVariable()));
		}
		return parametersBeforeOperators;
	}
}
