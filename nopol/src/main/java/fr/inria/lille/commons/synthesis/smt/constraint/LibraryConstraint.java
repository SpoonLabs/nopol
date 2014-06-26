package fr.inria.lille.commons.synthesis.smt.constraint;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.equality;

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

public class LibraryConstraint extends Constraint {

	public LibraryConstraint(SMTLib smtlib) {
		super("Library", smtlib);
	}

	@Override
	public List<LocationVariable<?>> usedLocationVariables(LocationVariableContainer locationVariableContainer) {
		return locationVariableContainer.copyOfOperatorsAndParameters();
	}	

	@Override
	protected List<IExpr> invocationArguments(LocationVariableContainer locationVariableContainer) {
		return (List) collectSubexpressions(usedLocationVariables(locationVariableContainer));
	}

	@Override
	protected List<IDeclaration> parameters(LocationVariableContainer locationVariableContainer) {
		return declarationsFromSubexpressions(usedLocationVariables(locationVariableContainer));
	}

	@Override
	protected Collection<IExpr> definitionExpressions(LocationVariableContainer locationVariableContainer) {
		Collection<IExpr> expressions = ListLibrary.newLinkedList();
		for (OperatorLocationVariable<?> operator : locationVariableContainer.operators()) {
			expressions.add(specificationOf(operator));
		}
		return expressions;
	}
	
	private IExpr specificationOf(OperatorLocationVariable<?> operator) {
		List<ISymbol> parameters = collectSubexpressions((List) operator.parameterLocationVariables());
		IExpr specification = smtlib().expression(operator.objectTemplate().smtlibIdentifier(), parameters);
		return smtlib().expression(equality(), symbolFromSubexpressionOf(operator), specification);
	}
}
