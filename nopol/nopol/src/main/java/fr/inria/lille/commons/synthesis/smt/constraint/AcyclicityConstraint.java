package fr.inria.lille.commons.synthesis.smt.constraint;

import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ParameterLocationVariable;
import org.smtlib.IExpr;
import xxl.java.container.classic.MetaList;

import java.util.Collection;
import java.util.List;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.lessThan;

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
        Collection<IExpr> parametersBeforeOperators = MetaList.newLinkedList();
        for (ParameterLocationVariable<?> parameter : locationVariableContainer.allParameters()) {
            IExpr parameterExpr = expressionSymbolOf(parameter);
            IExpr operatorExpr = expressionSymbolOf(parameter.operatorLocationVariable());
            parametersBeforeOperators.add(binaryOperation(parameterExpr, lessThan(), operatorExpr));
        }
        return parametersBeforeOperators;
    }

}
