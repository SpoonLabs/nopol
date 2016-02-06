package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.operator.UnaryOperator;

/**
 * is the generic type of a binary expression
 */
public class UnaryExpressionImpl extends ExpressionImpl implements UnaryExpression {
    private UnaryOperator operator;
    private Expression expression;
    private String strExpression = null;

    /**
     *
     */
    public UnaryExpressionImpl(UnaryOperator operator, Expression expression, Object value, Class type) {
        super(value, type);
        this.operator = operator;
        this.expression = expression;
    }

    public UnaryExpressionImpl(UnaryOperator operator, Expression expression) {
        this(operator, expression, null, operator.getReturnType());
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

    @Override
    public void setExpression(
            Expression expression) {
        this.expression = expression;
    }

    @Override
    public void evaluate() {
        try {
            Object value = getValueFromOperation();
            this.setValue(value);
            if (value != null) {
                this.setType(value.getClass());
            }
        } catch (ArithmeticException e) {
            // ignore
        }
    }

    @Override
    public Object evaluate(Candidates values) {

        Object expValue = this.getExpression().getValue();

        try {
            ((ExpressionImpl) this.getExpression()).setValue(this.getExpression().evaluate(values));
        } catch (RuntimeException e) {
            ((ExpressionImpl) this.getExpression()).setValue(null);
        }
        Object value = null;
        try {
            value = getValueFromOperation();
        } catch (ArithmeticException e) {
            // ignore
        }

        ((ExpressionImpl) this.getExpression()).setValue(expValue);

        return value;
    }

    private Object getValueFromOperation() {
        if (!getOperator().getReturnType().isAssignableFrom(getExpression().getType())) {
            return null;
        }
        if(getExpression() instanceof Constant) {
            return null;
        }
        if(getExpression() instanceof UnaryExpression) {
            return null;
        }
        if(getExpression() instanceof MethodInvocation && getOperator() != UnaryOperator.INV) {
            return null;
        }
        Object value = getExpression().getValue();
        if(value == null) {
            return null;
        }
        switch (getOperator()) {
        case INV:
            if(value instanceof Boolean) {
                return !(Boolean)value;
            }
            return null;
        /*case PREINC:
        case POSTINC:
            if (value instanceof Integer) {
                return ((Integer) value).intValue() + 1;
            } else if (value instanceof Double) {
                return ((Double) value).doubleValue() + 1;
            } else if (value instanceof Long) {
                return ((Long) value).longValue() + 1;
            } else if (value instanceof Float) {
                return ((Float) value).floatValue() + 1;
            } else if (value instanceof Short) {
                return ((Short) value).shortValue() + 1;
            } else if (value instanceof Byte) {
                return ((Byte) value).byteValue() + 1;
            }
            return null;
        case PREDEC:
        case POSTDEC:
            if (value instanceof Integer) {
                return ((Integer) value).intValue() - 1;
            } else if (value instanceof Double) {
                return ((Double) value).doubleValue() - 1;
            } else if (value instanceof Long) {
                return ((Long) value).longValue() - 1;
            } else if (value instanceof Float) {
                return ((Float) value).floatValue() - 1;
            } else if (value instanceof Short) {
                return ((Short) value).shortValue() - 1;
            } else if (value instanceof Byte) {
                return ((Byte) value).byteValue() - 1;
            }
            return null;*/
        }
        return null;
    }

    @Override
    public int countInnerExpression() {
        return getExpression().countInnerExpression();
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

