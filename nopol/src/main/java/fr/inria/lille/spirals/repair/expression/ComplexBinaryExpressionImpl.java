package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.operator.Operator;

/**
 *
 *
 *
 */

public class ComplexBinaryExpressionImpl extends BinaryExpressionImpl implements ComplexBinaryExpression {
    /**
     *
     */
    public ComplexBinaryExpressionImpl(Operator operator, Expression first, Expression second, Object value, Class type) {
        super(operator, first, second, value, type);
    }

    public ComplexBinaryExpressionImpl(Operator operator, Expression first, Expression second) {
        super(operator, first, second, null, null);
        evaluate();
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

        Object exp1Value = this.getFirstExpression().getValue();
        Object exp2Value = this.getSecondExpression().getValue();

        ((ExpressionImpl) this.getFirstExpression()).setValue(this.getFirstExpression().evaluate(values));
        ((ExpressionImpl) this.getSecondExpression()).setValue(this.getSecondExpression().evaluate(values));

        Object value = null;
        try {
            value = getValueFromOperation();
        } catch (ArithmeticException e) {
            // ignore
        }

        ((ExpressionImpl) this.getFirstExpression()).setValue(exp1Value);
        ((ExpressionImpl) this.getSecondExpression()).setValue(exp2Value);

        return value;
    }


    private Object getValueFromOperation() {
        if (!getOperator().getParam1().isAssignableFrom(getFirstExpression().getType())) {
            return null;
        }
        if (!getOperator().getParam2().isAssignableFrom(getSecondExpression().getType())) {
            return null;
        }
        if (getFirstExpression().getValue() == null && !(getOperator() == Operator.EQ || getOperator() == Operator.NEQ)) {
            return null;
        }
        if (getSecondExpression().getValue() == null && !(getOperator() == Operator.EQ || getOperator() == Operator.NEQ)) {
            return null;
        }

        switch (getOperator()) {
            case EQ:
                return getFirstExpression().getValue() == getSecondExpression().getValue();
            case NEQ:
                return getFirstExpression().getValue() != getSecondExpression().getValue();
        }
        return null;
    }

    private Object executeOperatorInteger() {
        int firstValue = ((Number) getFirstExpression().getValue()).intValue();
        switch (getOperator()) {
            case ADD:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue + ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue + ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue + ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue + ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue + ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue + ((Number) getSecondExpression().getValue()).byteValue();
                }
            case SUB:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue - ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue - ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue - ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue - ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue - ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue - ((Number) getSecondExpression().getValue()).byteValue();
                }
            case MULT:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue * ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue * ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue * ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue * ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue * ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue * ((Number) getSecondExpression().getValue()).byteValue();
                }
            case DIV:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue / ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue / ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue / ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue / ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue / ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue / ((Number) getSecondExpression().getValue()).byteValue();
                }
            case EQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue == ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue == ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue == ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue == ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue == ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue == ((Number) getSecondExpression().getValue()).byteValue();
                }
            case NEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue != ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue != ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue != ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue != ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue != ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue != ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESS:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue < ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue < ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue < ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue < ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue < ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue < ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESSEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).byteValue();
                }
        }
        return null;
    }

    private Object executeOperatorLong() {
        long firstValue = ((Number) getFirstExpression().getValue()).longValue();
        switch (getOperator()) {
            case ADD:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue + ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue + ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue + ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue + ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue + ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue + ((Number) getSecondExpression().getValue()).byteValue();
                }
            case SUB:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue - ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue - ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue - ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue - ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue - ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue - ((Number) getSecondExpression().getValue()).byteValue();
                }
            case MULT:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue * ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue * ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue * ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue * ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue * ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue * ((Number) getSecondExpression().getValue()).byteValue();
                }
            case DIV:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue / ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue / ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue / ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue / ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue / ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue / ((Number) getSecondExpression().getValue()).byteValue();
                }
            case EQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue == ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue == ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue == ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue == ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue == ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue == ((Number) getSecondExpression().getValue()).byteValue();
                }
            case NEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue != ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue != ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue != ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue != ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue != ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue != ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESS:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue < ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue < ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue < ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue < ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue < ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue < ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESSEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).byteValue();
                }
        }
        return null;
    }

    private Object executeOperatorFloat() {
        float firstValue = ((Number) getFirstExpression().getValue()).floatValue();
        switch (getOperator()) {
            case ADD:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue + ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue + ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue + ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue + ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue + ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue + ((Number) getSecondExpression().getValue()).byteValue();
                }
            case SUB:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue - ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue - ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue - ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue - ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue - ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue - ((Number) getSecondExpression().getValue()).byteValue();
                }
            case MULT:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue * ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue * ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue * ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue * ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue * ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue * ((Number) getSecondExpression().getValue()).byteValue();
                }
            case DIV:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue / ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue / ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue / ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue / ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue / ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue / ((Number) getSecondExpression().getValue()).byteValue();
                }
            case EQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue == ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue == ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue == ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue == ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue == ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue == ((Number) getSecondExpression().getValue()).byteValue();
                }
            case NEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue != ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue != ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue != ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue != ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue != ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue != ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESS:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue < ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue < ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue < ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue < ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue < ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue < ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESSEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).byteValue();
                }
        }
        return null;
    }

    private Object executeOperatorShort() {
        short firstValue = ((Number) getFirstExpression().getValue()).shortValue();
        switch (getOperator()) {
            case ADD:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue + ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue + ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue + ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue + ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue + ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue + ((Number) getSecondExpression().getValue()).byteValue();
                }
            case SUB:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue - ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue - ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue - ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue - ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue - ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue - ((Number) getSecondExpression().getValue()).byteValue();
                }
            case MULT:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue * ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue * ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue * ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue * ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue * ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue * ((Number) getSecondExpression().getValue()).byteValue();
                }
            case DIV:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue / ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue / ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue / ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue / ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue / ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue / ((Number) getSecondExpression().getValue()).byteValue();
                }
            case EQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue == ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue == ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue == ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue == ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue == ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue == ((Number) getSecondExpression().getValue()).byteValue();
                }
            case NEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue != ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue != ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue != ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue != ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue != ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue != ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESS:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue < ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue < ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue < ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue < ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue < ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue < ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESSEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).byteValue();
                }
        }
        return null;
    }

    private Object executeOperatorDouble() {
        double firstValue = ((Number) getFirstExpression().getValue()).doubleValue();
        switch (getOperator()) {
            case ADD:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue + ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue + ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue + ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue + ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue + ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue + ((Number) getSecondExpression().getValue()).byteValue();
                }
            case SUB:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue - ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue - ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue - ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue - ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue - ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue - ((Number) getSecondExpression().getValue()).byteValue();
                }
            case MULT:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue * ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue * ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue * ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue * ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue * ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue * ((Number) getSecondExpression().getValue()).byteValue();
                }
            case DIV:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue / ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue / ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue / ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue / ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue / ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue / ((Number) getSecondExpression().getValue()).byteValue();
                }
            case EQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue == ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue == ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue == ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue == ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue == ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue == ((Number) getSecondExpression().getValue()).byteValue();
                }
            case NEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue != ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue != ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue != ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue != ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue != ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue != ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESS:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue < ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue < ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue < ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue < ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue < ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue < ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESSEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).byteValue();
                }
        }
        return null;
    }

    private Object executeOperatorByte() {
        byte firstValue = ((Number) getFirstExpression().getValue()).byteValue();
        switch (getOperator()) {
            case ADD:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue + ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue + ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue + ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue + ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue + ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue + ((Number) getSecondExpression().getValue()).byteValue();
                }
            case SUB:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue - ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue - ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue - ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue - ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue - ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue - ((Number) getSecondExpression().getValue()).byteValue();
                }
            case MULT:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue * ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue * ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue * ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue * ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue * ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue * ((Number) getSecondExpression().getValue()).byteValue();
                }
            case DIV:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue / ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue / ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue / ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue / ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue / ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue / ((Number) getSecondExpression().getValue()).byteValue();
                }
            case EQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue == ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue == ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue == ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue == ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue == ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue == ((Number) getSecondExpression().getValue()).byteValue();
                }
            case NEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue != ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue != ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue != ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue != ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue != ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue != ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESS:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue < ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue < ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue < ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue < ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue < ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue < ((Number) getSecondExpression().getValue()).byteValue();
                }
            case LESSEQ:
                if (getSecondExpression().getValue() instanceof Integer) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).intValue();
                } else if (getSecondExpression().getValue() instanceof Double) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).doubleValue();
                } else if (getSecondExpression().getValue() instanceof Long) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).longValue();
                } else if (getSecondExpression().getValue() instanceof Float) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).floatValue();
                } else if (getSecondExpression().getValue() instanceof Short) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).shortValue();
                } else if (getSecondExpression().getValue() instanceof Byte) {
                    return firstValue <= ((Number) getSecondExpression().getValue()).byteValue();
                }
        }
        return null;
    }

    private Object executeOperatorBoolean() {
        boolean firstValue = ((boolean) getFirstExpression().getValue());
        switch (getOperator()) {
            case AND:
                if (getSecondExpression().getValue() instanceof Boolean) {
                    return firstValue && ((boolean) getSecondExpression().getValue());
                }
            case OR:
                if (getSecondExpression().getValue() instanceof Boolean) {
                    return firstValue || ((boolean) getSecondExpression().getValue());
                }
        }
        return null;
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof PrimitiveBinaryExpression)) return false;

        PrimitiveBinaryExpression that = (PrimitiveBinaryExpression) o;
        return this.toString().equals(that.toString());
    }*/


}

