package fr.inria.lille.commons.synthesis.smt.constraint;

import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.OperatorLocationVariable;
import org.smtlib.IExpr;
import org.smtlib.IExpr.ISymbol;
import xxl.java.container.classic.MetaList;

import java.util.Collection;
import java.util.List;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.equality;

public class LibraryConstraint extends Constraint {

    public LibraryConstraint(SMTLib smtlib) {
        super("Library", smtlib);
    }

    @Override
    public List<LocationVariable<?>> variablesForSubexpression(LocationVariableContainer container) {
        return container.operatorsAndParameters();
    }

    @Override
    protected Collection<IExpr> definitionExpressions(LocationVariableContainer locationVariableContainer) {
        Collection<IExpr> expressions = MetaList.newLinkedList();
        for (OperatorLocationVariable<?> operator : locationVariableContainer.operators()) {
            expressions.add(specificationOf(operator));
        }
        return expressions;
    }

    private IExpr specificationOf(OperatorLocationVariable<?> operator) {
        List<ISymbol> parameters = subexpressionSymbolsOf((List) operator.parameterLocationVariables());
        IExpr specification = smtlib().expression(operator.objectTemplate().smtlibIdentifier(), parameters);
        return binaryOperation(subexpressionSymbolOf(operator), equality(), specification);
    }
}
