package fr.inria.lille.spirals.repair.expressionV2.access;


import fr.inria.lille.spirals.repair.expressionV2.Expression;

/**
 * is the generic type of an unary expression
 */
public interface Variable extends Expression {

    /**
     * get the variable name
     * @return the variableName
     */
    String getVariableName();

    Expression getTarget();
}

