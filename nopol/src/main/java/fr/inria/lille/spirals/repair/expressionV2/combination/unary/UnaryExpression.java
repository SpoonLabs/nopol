package fr.inria.lille.spirals.repair.expressionV2.combination.unary;

import fr.inria.lille.spirals.repair.expressionV2.Expression;
import fr.inria.lille.spirals.repair.expressionV2.combination.CombinationExpression;

/**
 * is the generic type of an unary expression
 */
public interface UnaryExpression extends CombinationExpression {

    Expression getExpression();

    UnaryOperator getOperator();
}

