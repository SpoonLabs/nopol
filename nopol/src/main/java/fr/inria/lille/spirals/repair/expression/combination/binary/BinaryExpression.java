package fr.inria.lille.spirals.repair.expression.combination.binary;


import fr.inria.lille.spirals.repair.expression.Expression;
import fr.inria.lille.spirals.repair.expression.combination.CombinationExpression;

/**
 * is the generic type of a binary expression
 */
public interface BinaryExpression extends CombinationExpression {

    Expression getFirstExpression();

    BinaryOperator getOperator();

    Expression getSecondExpression();
}

