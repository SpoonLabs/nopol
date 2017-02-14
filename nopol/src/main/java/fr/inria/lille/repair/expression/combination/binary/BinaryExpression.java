package fr.inria.lille.repair.expression.combination.binary;


import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.combination.CombinationExpression;

/**
 * is the generic type of a binary expression
 */
public interface BinaryExpression extends CombinationExpression {

    Expression getFirstExpression();

    BinaryOperator getOperator();

    Expression getSecondExpression();
}

