package fr.inria.lille.spirals.repair.expression;

import fr.inria.lille.spirals.repair.expression.operator.UnaryOperator;

/**
 * is the generic type of an unary expression
 */
public interface UnaryExpression extends Expression {

    Expression getExpression();

    void setExpression(Expression first);

    UnaryOperator getOperator();

    void evaluate();
}

