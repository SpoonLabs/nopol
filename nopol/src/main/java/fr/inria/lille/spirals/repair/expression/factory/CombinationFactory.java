package fr.inria.lille.spirals.repair.expression.factory;


import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.expression.Expression;
import fr.inria.lille.spirals.repair.expression.combination.CombinationExpression;
import fr.inria.lille.spirals.repair.expression.combination.Operator;
import fr.inria.lille.spirals.repair.expression.combination.binary.BinaryExpression;
import fr.inria.lille.spirals.repair.expression.combination.binary.BinaryExpressionImpl;
import fr.inria.lille.spirals.repair.expression.combination.binary.BinaryOperator;
import fr.inria.lille.spirals.repair.expression.combination.unary.UnaryExpression;
import fr.inria.lille.spirals.repair.expression.combination.unary.UnaryExpressionImpl;
import fr.inria.lille.spirals.repair.expression.combination.unary.UnaryOperator;

import java.util.List;

public class CombinationFactory {

    public static CombinationExpression create(Operator operator, List<Expression> expressions, Config config) {
        switch (expressions.size()) {
            case 1:
                return create((UnaryOperator) operator, expressions.get(0), config);
            case 2:
                return create((BinaryOperator) operator, expressions.get(0), expressions.get(1), config);
            default:
                throw new IllegalArgumentException("Combination expression with " + expressions.size() + " is not supported");
        }
    }

    public static BinaryExpression create(BinaryOperator operator, Expression first, Expression second, Config config) {
        return new BinaryExpressionImpl(operator, first, second, config);
    }

    public static UnaryExpression create(UnaryOperator operator, Expression first, Config config) {
        return new UnaryExpressionImpl(operator, first, config);
    }
}
