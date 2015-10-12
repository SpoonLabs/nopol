package fr.inria.lille.commons.synthesis.smt.constraint;

import fr.inria.lille.commons.synthesis.expression.ObjectTemplate;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.OperatorLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ParameterLocationVariable;
import org.smtlib.IExpr;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import xxl.java.container.classic.MetaList;
import xxl.java.container.map.Multimap;

import java.util.Collection;
import java.util.List;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.equality;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.lessOrEqualThan;
import static java.util.Arrays.asList;

public class LineBoundConstraint extends Constraint {

    public LineBoundConstraint(SMTLib smtlib) {
        super("LineBound", smtlib);
    }

    @Override
    public List<LocationVariable<?>> variablesForExpression(LocationVariableContainer container) {
        return container.operatorsParametersAndOutput();
    }

    @Override
    protected Collection<IExpr> definitionExpressions(LocationVariableContainer container) {
        Collection<IExpr> locationVariableBounds = MetaList.newLinkedList();
        Multimap<ISort, LocationVariable<?>> bySort = (Multimap) ObjectTemplate.bySort(container.inputsAndOperators());
        addOperatorBounds(locationVariableBounds, container);
        addParameterTypeBounds(locationVariableBounds, bySort, container.allParameters());
        addOutputTypeBound(locationVariableBounds, bySort, container.outputVariable());
        return locationVariableBounds;
    }

    private void addOperatorBounds(Collection<IExpr> expressions, LocationVariableContainer container) {
        IExpr from = asNumeral(container.numberOfInputs());
        IExpr to = asNumeral(container.numberOfInputs() + container.numberOfOperators() - 1);
        for (OperatorLocationVariable<?> operator : container.operators()) {
            expressions.add(lineBoundaryFor(from, to, operator));
        }
    }

    private IExpr lineBoundaryFor(IExpr from, IExpr to, LocationVariable<?> variable) {
        ISymbol lessOrEqualThan = lessOrEqualThan();
        ISymbol variableExpr = expressionSymbolOf(variable);
        IExpr lowerBoundExpr = binaryOperation(from, lessOrEqualThan, variableExpr);
        IExpr upperBoundExpr = binaryOperation(variableExpr, lessOrEqualThan, to);
        return conjunctionOf(asList(lowerBoundExpr, upperBoundExpr));
    }

    private void addParameterTypeBounds(Collection<IExpr> expressions, Multimap<ISort, LocationVariable<?>> bySort, List<ParameterLocationVariable<?>> parameters) {
        for (ParameterLocationVariable<?> parameter : parameters) {
            ISort sort = parameter.smtSort();
            Collection<LocationVariable<?>> operands = MetaList.newLinkedList(bySort.get(sort));
            operands.remove(parameter.operatorLocationVariable());
            expressions.add(equalToAnyExpression(operands, parameter));
        }
    }

    private void addOutputTypeBound(Collection<IExpr> expressions, Multimap<ISort, LocationVariable<?>> bySort, LocationVariable<?> output) {
        ISort outputSort = output.smtSort();
        if (bySort.containsKey(outputSort)) {
            Collection<LocationVariable<?>> operands = bySort.get(outputSort);
            expressions.add(equalToAnyExpression(operands, output));
        } else {
            expressions.add(SMTLib.booleanFalse());
        }
    }

    private IExpr equalToAnyExpression(Collection<LocationVariable<?>> operands, LocationVariable<?> variable) {
        Collection<IExpr> equalities = MetaList.newLinkedList();
        for (LocationVariable<?> operand : operands) {
            equalities.add(binaryOperation(operand.encodedLineNumber(), equality(), expressionSymbolOf(variable)));
        }
        return disjunctionOf(equalities);
    }
}
