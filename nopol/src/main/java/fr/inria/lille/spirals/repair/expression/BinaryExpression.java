package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.spirals.repair.expression.operator.Operator;

/**
 * is the generic type of a binary expression
 */
public interface BinaryExpression extends Expression {

    Expression getFirstExpression();

    void setFirst(Expression first);

    Operator getOperator();

    Expression getSecondExpression();

    void setSecond(Expression second);

    void evaluate();
}

