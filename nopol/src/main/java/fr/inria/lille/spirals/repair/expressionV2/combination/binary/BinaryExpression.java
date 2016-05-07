package fr.inria.lille.spirals.repair.expressionV2.combination.binary;


import fr.inria.lille.spirals.repair.expressionV2.Expression;
import fr.inria.lille.spirals.repair.expressionV2.combination.CombinationExpression;

/**
 * is the generic type of a binary expression
 */
public interface BinaryExpression extends CombinationExpression {

    Expression getFirstExpression();

    BinaryOperator getOperator();

    Expression getSecondExpression();
}

