package fr.inria.lille.commons.synthesis.smt.constraint;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.equality;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.implies;

import java.util.Collection;
import java.util.List;

import org.smtlib.IExpr;
import org.smtlib.ISort;

import xxl.java.extensions.collection.ListLibrary;
import xxl.java.extensions.collection.Multimap;
import fr.inria.lille.commons.synthesis.expression.ObjectTemplate;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.IndexedLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ParameterLocationVariable;

public class ConnectivityConstraint extends Constraint {

	public ConnectivityConstraint(SMTLib smtlib) {
		super("Connectivity", smtlib);
	}

	@Override
	public List<LocationVariable<?>> variablesForExpression(LocationVariableContainer container) {
		return container.operatorsParametersAndOutput();
	}
	
	@Override
	public List<LocationVariable<?>> variablesForSubexpression(LocationVariableContainer container) {
		return container.allVariables();
	}
	
	@Override
	protected Collection<IExpr> definitionExpressions(LocationVariableContainer container) {
		Collection<IExpr> implications = ListLibrary.newLinkedList();
		Multimap<ISort, LocationVariable<?>> bySort = (Multimap) ObjectTemplate.bySort(container.inputsAndOperators());
		addImplicationsForOutput(implications, container.outputVariable(), bySort);
		addImplicationsForParameters(implications, container.allParameters(), bySort);
		return implications;
	}

	private void addImplicationsForOutput(Collection<IExpr> implications, IndexedLocationVariable<?> outputVariable, Multimap<ISort, LocationVariable<?>> bySort) {
		ISort outputSort = outputVariable.smtSort();
		if (bySort.containsKey(outputSort)) {
			Collection<LocationVariable<?>> sameTypeVariables = bySort.get(outputSort);
			addImplicationsBetween(implications, outputVariable, sameTypeVariables);
		} else {
			implications.add(SMTLib.booleanFalse());
		}
	}
	
	private void addImplicationsForParameters(Collection<IExpr> implications, List<ParameterLocationVariable<?>> parameters, Multimap<ISort, LocationVariable<?>> bySort) {
		for (ParameterLocationVariable<?> parameter : parameters) {
			Collection<LocationVariable<?>> sameTypeVariables = ListLibrary.newLinkedList(bySort.get(parameter.smtSort()));
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
		IExpr sameLine = binaryOperation(expressionSymbolOf(firstVariable), equality(), secondVariable.encodedLineNumber());
		IExpr sameVariable = binaryOperation(subexpressionSymbolOf(firstVariable), equality(), subexpressionSymbolOf(secondVariable));
		return binaryOperation(sameLine, implies(), sameVariable);
	}
}
