package fr.inria.lille.repair.expression.combination.unary;


import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.ExpressionImpl;
import fr.inria.lille.repair.expression.access.Method;
import fr.inria.lille.repair.expression.combination.binary.BinaryExpression;
import fr.inria.lille.repair.expression.factory.ValueFactory;
import fr.inria.lille.repair.expression.value.Value;

/**
 * is the generic type of a binary expression
 */
public class UnaryExpressionImpl extends ExpressionImpl implements UnaryExpression {
    private UnaryOperator operator;
    private Expression expression;
    private String strExpression = null;

    public UnaryExpressionImpl(UnaryOperator operator, Expression expression, NopolContext nopolContext) {
        super(null, nopolContext);
        this.operator = operator;
        this.expression = expression;
        evaluate();
    }

    /**
     *
     */
    public UnaryOperator getOperator() {
        return operator;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(
            Expression expression) {
        this.expression = expression;
    }

    public void evaluate() {
        Value value = getValueFromOperation();
        this.setValue(value);
    }

    @Override
    public Value evaluate(Candidates values) {
        Value value = this.getExpression().getValue();

        Value newValue = Value.NOVALUE;
        try {
            Value evaluate = this.getExpression().evaluate(values);
            if (evaluate != Value.NOVALUE) {
                this.setValue(evaluate);
                newValue = getValueFromOperation();
            }
        } finally {
            // rollback previous value
            this.getExpression().setValue(value);
        }
        return newValue;
    }

    private Value getValueFromOperation() {
        Value value1 = getExpression().getValue();
        if (value1 == null) {
            return null;
        }
        if (!getOperator().getReturnType().isAssignableFrom(value1.getType())) {
            return null;
        }
        if(value1.isConstant()) {
            return null;
        }
        if(getExpression() instanceof UnaryExpression) {
            return null;
        }
        if(getExpression() instanceof Method && getOperator() != UnaryOperator.INV) {
            return null;
        }
        Object value = value1.getRealValue();
        if(value == null) {
            return null;
        }
        switch (getOperator()) {
        case INV:
            if(value instanceof Boolean) {
                return ValueFactory.create(!(Boolean)value);
            }
            return null;
        }
        return null;
    }

    @Override
    public int nbSubExpression() {
        return 1;
    }

    @Override
    public double getWeight() {
        double weight = 1;
        return weight * getPriority() * getExpression().getWeight();
    }

    @Override
    public String toString() {
        if (strExpression == null) {
            String first = getExpression().toString().intern();
            if (getExpression() instanceof BinaryExpression) {
                first = "(" + first + ")";
            }
            if(getOperator().getPosition() == UnaryOperator.OperatorPosition.PRE) {
                strExpression = getOperator().getSymbol() + first;
            } else {
                strExpression = first + getOperator().getSymbol();
            }
        }

        return strExpression;
    }

    @Override
    public String asPatch() {
        String first = getExpression().asPatch().intern();
        if (getExpression() instanceof BinaryExpression) {
            first = "(" + first + ")";
        }
        if(getOperator().getPosition() == UnaryOperator.OperatorPosition.PRE) {
            return getOperator().getSymbol() + first;
        } else {
            return first + getOperator().getSymbol();
        }
    }
}

