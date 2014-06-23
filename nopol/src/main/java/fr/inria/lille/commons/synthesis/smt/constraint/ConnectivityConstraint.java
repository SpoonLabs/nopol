package fr.inria.lille.commons.synthesis.smt.constraint;

import java.util.Collection;
import java.util.List;

import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.ISort;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.Multimap;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ParameterLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ValuedExpressionLocationVariable;

public class ConnectivityConstraint extends Constraint {

	public ConnectivityConstraint(SMTLib smtlib) {
		super("Connectivity", smtlib);
	}

	@Override
	public List<LocationVariable<?>> usedLocationVariables(LocationVariableContainer locationVariableContainer) {
		return locationVariableContainer.copyOfAllLocationVariables();
	}

	@Override
	protected List<IExpr> arguments(LocationVariableContainer locationVariableContainer) {
		List<IExpr> arguments = ListLibrary.newLinkedList();
		arguments.addAll(collectSubexpressions((Collection) locationVariableContainer.inputs()));
		List<LocationVariable<?>> restOfVariables = locationVariableContainer.copyOfOperatorsParametersAndOutput();
		arguments.addAll(collectExpressions(restOfVariables));
		arguments.addAll(collectSubexpressions(restOfVariables));
		return arguments;
	}

	@Override
	protected List<IDeclaration> parameters(LocationVariableContainer locationVariableContainer) {
		List<IDeclaration> parameters = ListLibrary.newLinkedList();
		parameters.addAll(declarationsFromSubexpressions((Collection) locationVariableContainer.inputs()));
		List<LocationVariable<?>> restOfVariables = locationVariableContainer.copyOfOperatorsParametersAndOutput();
		parameters.addAll(declarationsFromExpressions(restOfVariables));
		parameters.addAll(declarationsFromSubexpressions(restOfVariables));
		return parameters;
	}
	
	@Override
	protected Collection<IExpr> definitionExpressions(LocationVariableContainer locationVariableContainer) {
		Collection<IExpr> implications = ListLibrary.newLinkedList();
		Multimap<ISort, LocationVariable<?>> bySort = LocationVariable.bySort(locationVariableContainer.copyOfOperatorsAndInputs());
		addImplicationsForOutput(implications, locationVariableContainer.outputVariable(), bySort);
		addImplicationsForParameters(implications, locationVariableContainer.allParameters(), bySort);
		return implications;
	}

	private void addImplicationsForOutput(Collection<IExpr> implications, ValuedExpressionLocationVariable<?> outputVariable, Multimap<ISort, LocationVariable<?>> bySort) {
		Collection<LocationVariable<?>> sameTypeVariables = bySort.get(sortFor(outputVariable));
		addImplicationsBetween(implications, outputVariable, sameTypeVariables);
	}
	
	private void addImplicationsForParameters(Collection<IExpr> implications, List<ParameterLocationVariable<?>> parameters, Multimap<ISort, LocationVariable<?>> bySort) {
		for (ParameterLocationVariable<?> parameter : parameters) {
			Collection<LocationVariable<?>> sameTypeVariables = ListLibrary.newLinkedList(bySort.get(sortFor(parameter)));
			sameTypeVariables.remove(parameter.operatorLocationVariable());
			addImplicationsBetween(implications, parameter, sameTypeVariables);
		}
	}

	private void addImplicationsBetween(Collection<IExpr> implications, LocationVariable<?> locationVariable, Collection<LocationVariable<?>> sameTypeVariables) {
		for (LocationVariable<?> sameTypeVariable : sameTypeVariables) {
			if (! locationVariable.equals(sameTypeVariable)) {
				implications.add(sameLineSameVariableImplication(locationVariable, sameTypeVariable));
			}
		}
	}

	private IExpr sameLineSameVariableImplication(LocationVariable<?> firstVariable, LocationVariable<?> secondVariable) {
		IExpr sameLine = binaryOperationWithExpression(smtlib().equals(), firstVariable, secondVariable.encodedLineNumber());
		IExpr sameVariable = binaryOperationWithSubexpression(smtlib().equals(), firstVariable, secondVariable);
		return binaryOperation(smtlib().implies(), sameLine, sameVariable);
	}
}
