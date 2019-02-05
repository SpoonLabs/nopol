package fr.inria.lille.repair.expression.factory;


import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.combination.CombinationExpression;
import fr.inria.lille.repair.expression.combination.Operator;
import fr.inria.lille.repair.expression.combination.binary.BinaryExpression;
import fr.inria.lille.repair.expression.combination.binary.BinaryExpressionImpl;
import fr.inria.lille.repair.expression.combination.binary.BinaryOperator;
import fr.inria.lille.repair.expression.combination.unary.UnaryExpression;
import fr.inria.lille.repair.expression.combination.unary.UnaryExpressionImpl;
import fr.inria.lille.repair.expression.combination.unary.UnaryOperator;

import java.util.List;

public class CombinationFactory {

    public static CombinationExpression create(Operator operator, List<Expression> expressions, NopolContext nopolContext) {
        switch (expressions.size()) {
            case 1:
                return create((UnaryOperator) operator, expressions.get(0), nopolContext);
            case 2:
                return create((BinaryOperator) operator, expressions.get(0), expressions.get(1), nopolContext);
            default:
                throw new IllegalArgumentException("Combination expression with " + expressions.size() + " is not supported");
        }
    }

    public static BinaryExpression create(BinaryOperator operator, Expression first, Expression second, NopolContext nopolContext) {
        return new BinaryExpressionImpl(operator, first, second, nopolContext);
    }

    public static UnaryExpression create(UnaryOperator operator, Expression first, NopolContext nopolContext) {
        return new UnaryExpressionImpl(operator, first, nopolContext);
    }
}
