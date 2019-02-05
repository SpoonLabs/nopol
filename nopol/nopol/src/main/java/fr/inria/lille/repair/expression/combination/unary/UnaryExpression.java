package fr.inria.lille.repair.expression.combination.unary;

import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.combination.CombinationExpression;

/**
 * is the generic type of an unary expression
 */
public interface UnaryExpression extends CombinationExpression {

    Expression getExpression();

    UnaryOperator getOperator();
}

