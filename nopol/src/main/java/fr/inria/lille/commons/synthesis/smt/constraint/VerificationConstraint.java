package fr.inria.lille.commons.synthesis.smt.constraint;

import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.IndexedLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class VerificationConstraint extends CompoundConstraint {

    public VerificationConstraint(SMTLib smtlib) {
        super("Verification", smtlib, Arrays.asList(new ConnectivityConstraint(smtlib), new LibraryConstraint(smtlib)));
    }

    @Override
    public List<LocationVariable<?>> variablesForExpression(LocationVariableContainer container) {
        return container.operatorsParametersAndOutput();
    }

    @Override
    public List<LocationVariable<?>> variablesForSubexpression(LocationVariableContainer container) {
        return (List) container.inputsAndOutput();
    }

    @Override
    protected Collection<IExpr> definitionExpressions(LocationVariableContainer container) {
        IExpr predicate = conjunctionOf(subconstraintInvocations(container));
        List<LocationVariable<?>> variables = container.operatorsAndParameters();
        if (variables.isEmpty()) {
            return (List) asList(predicate);
        }
        List<IDeclaration> declarations = declarationsFromSubexpression(variables);
        return (List) asList(smtlib().exists(declarations, predicate));
    }

    @Override
    public List<IExpr> instantiatedArguments(LocationVariableContainer container, Map<String, Object> values) {
        List<IndexedLocationVariable<?>> instantiables = container.inputsAndOutput();
        List<Object> actualValues = IndexedLocationVariable.extractWithObjectExpressions(values, instantiables);
        List<IExpr> arguments = (List) expressionSymbolsOf(variablesForExpression(container));
        arguments.addAll(asSMTExpressions(actualValues));
        return arguments;
    }
}