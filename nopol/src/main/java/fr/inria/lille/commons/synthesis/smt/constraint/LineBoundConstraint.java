package fr.inria.lille.commons.synthesis.smt.constraint;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.equality;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.lessOrEqualThan;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.Multimap;
import fr.inria.lille.commons.synthesis.expression.ObjectTemplate;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.OperatorLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ParameterLocationVariable;

public class LineBoundConstraint extends Constraint {

	public LineBoundConstraint(SMTLib smtlib) {
		super("LineBound", smtlib);
	}

	@Override
	public List<LocationVariable<?>> usedLocationVariables(LocationVariableContainer locationVariableContainer) {
		return locationVariableContainer.copyOfOperatorsParametersAndOutput();
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
		Collection<IExpr> locationVariableBounds = ListLibrary.newLinkedList();
		Multimap<ISort, LocationVariable<?>> bySort = (Multimap) ObjectTemplate.bySort((List) locationVariableContainer.copyOfOperatorsAndInputs());
		addOperatorBounds(locationVariableBounds, locationVariableContainer);
		addParameterTypeBounds(locationVariableBounds, bySort, locationVariableContainer.copyOfAllParameters());
		addOutputTypeBound(locationVariableBounds, bySort, locationVariableContainer.outputVariable());
		return locationVariableBounds;
	}
	
	private void addOperatorBounds(Collection<IExpr> expressions, LocationVariableContainer locationVariableContainer) {
		int from = locationVariableContainer.numberOfInputs();
		int to = from + locationVariableContainer.numberOfOperators() - 1;
		for (OperatorLocationVariable<?> operator : locationVariableContainer.operators()) {
			expressions.add(lineBoundaryFor(from, to, operator));
		}
	}

	private IExpr lineBoundaryFor(int from, int to, LocationVariable<?> variable) {
		ISymbol lessOrEqualThan = lessOrEqualThan();
		IExpr lowerBoundExpr = binaryOperationWithExpression(lessOrEqualThan, asNumeral(from), variable);
		IExpr upperBoundExpr = binaryOperationWithExpression(lessOrEqualThan, variable, asNumeral(to));
		return conjunctionOf(Arrays.asList(lowerBoundExpr, upperBoundExpr));
	}
	
	private void addParameterTypeBounds(Collection<IExpr> expressions, Multimap<ISort, LocationVariable<?>> bySort, List<ParameterLocationVariable<?>> parameters) {
		for (ParameterLocationVariable<?> parameter : parameters) {
			ISort sort = parameter.smtSort();
			Collection<LocationVariable<?>> operands = ListLibrary.newLinkedList(bySort.get(sort));
			operands.remove(parameter.operatorLocationVariable());
			expressions.add(equalToAnyExpression(operands, parameter));
		}
	}
	
	private void addOutputTypeBound(Collection<IExpr> expressions, Multimap<ISort, LocationVariable<?>> bySort, LocationVariable<?> output) {
		Collection<LocationVariable<?>> operands = bySort.get(output.smtSort());
		expressions.add(equalToAnyExpression(operands, output));
	}
	
	private IExpr equalToAnyExpression(Collection<LocationVariable<?>> operands, LocationVariable<?> variable) {
		Collection<IExpr> equalities = ListLibrary.newLinkedList();
		for (LocationVariable<?> operand : operands) {
			equalities.add(binaryOperationWithExpression(equality(), operand.encodedLineNumber(), variable));
		}
		return disjunctionOf(equalities);
	}
}
