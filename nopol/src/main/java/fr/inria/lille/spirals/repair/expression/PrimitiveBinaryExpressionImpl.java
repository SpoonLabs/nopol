package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.operator.Operator;

/**
 *
 *
 *
 */

public class PrimitiveBinaryExpressionImpl extends BinaryExpressionImpl implements PrimitiveBinaryExpression {
    /**
     *
     */
    public PrimitiveBinaryExpressionImpl(Operator operator, Expression first, Expression second, Object value, Class type) {
        super(operator, first, second, value, type);
    }

    public PrimitiveBinaryExpressionImpl(Operator operator, Expression first, Expression second) {
        super(operator, first, second, null, operator.getReturnType());
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

        try {
            ((ExpressionImpl) this.getFirstExpression()).setValue(this.getFirstExpression().evaluate(values));
        } catch (RuntimeException e) {
            ((ExpressionImpl) this.getFirstExpression()).setValue(null);
        }
        try {
            ((ExpressionImpl) this.getSecondExpression()).setValue(this.getSecondExpression().evaluate(values));
        } catch (RuntimeException e) {
            ((ExpressionImpl) this.getSecondExpression()).setValue(null);
        }
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
        if (getSecondExpression().getValue() == null && !(getOperator() == Operator.EQ || getOperator() == Operator.NEQ || getOperator() == Operator.OR)) {
            return null;
        }
        if (Number.class.isAssignableFrom(getFirstExpression().getType()) && getFirstExpression() instanceof Constant) {
            Number value = ((Number) getFirstExpression().getValue());
            if (value != null) {
                int val = value.intValue();
                if (val == 0 && (getOperator() == Operator.MULT || getOperator() == Operator.DIV)) {
                    return null;
                }
                if (val == 1 && (getOperator() == Operator.MULT)) {
                    return null;
                }
                if (val == 0 && (getOperator() == Operator.ADD)) {
                    return null;
                }
            }
        }
        if (Number.class.isAssignableFrom(getSecondExpression().getType()) && getSecondExpression() instanceof Constant) {
            Number value = ((Number) getSecondExpression().getValue());
            if (value != null) {
                int val = value.intValue();
                if (val == 0 && (getOperator() == Operator.MULT || getOperator() == Operator.DIV)) {
                    return null;
                }
                if (val == 1 && (getOperator() == Operator.MULT || getOperator() == Operator.DIV)) {
                    return null;
                }
                if (val == 0 && (getOperator() == Operator.ADD || getOperator() == Operator.SUB)) {
                    return null;
                }
            }
        }
        if (getOperator() == Operator.SUB) {
            if (getFirstExpression().sameExpression(getSecondExpression())) {
                return null;
            }
            if (getFirstExpression() instanceof BinaryExpression && ((BinaryExpression) getFirstExpression()).getOperator() == Operator.ADD) {
                if (((BinaryExpression) getFirstExpression()).getFirstExpression().sameExpression(getSecondExpression()) ||
                        ((BinaryExpression) getFirstExpression()).getSecondExpression().sameExpression(getSecondExpression())) {
                    return null;
                }
            }
        }
        if (getFirstExpression() instanceof BinaryExpression && getSecondExpression() instanceof BinaryExpression) {
            BinaryExpression b1 = ((BinaryExpression) getFirstExpression());
            BinaryExpression b2 = ((BinaryExpression) getSecondExpression());

            if (b1.getFirstExpression() instanceof Constant && b1.getSecondExpression() instanceof Constant && getOperator().getReturnType() == Boolean.class &&
                    b2.getFirstExpression() instanceof Constant && b2.getSecondExpression() instanceof Constant) {
                return null;
            }
            if (b1.getOperator() == b2.getOperator() && b1.getOperator().getReturnType() != Boolean.class) {
                if (b1.getSecondExpression().sameExpression(b2.getSecondExpression())) {
                    return null;
                }
                if (b1.getFirstExpression().sameExpression(b2.getFirstExpression())) {
                    return null;
                }
            }

            if ((getOperator() == Operator.AND || getOperator() == Operator.OR) && b1.getFirstExpression().sameExpression(b2.getFirstExpression()) && b1.getSecondExpression().sameExpression(b2.getSecondExpression())) {
                if (b1.getOperator() == Operator.EQ && (b2.getOperator() == Operator.LESSEQ || b2.getOperator() == Operator.NEQ)) {
                    return null;
                }
                if (b2.getOperator() == Operator.EQ && (b1.getOperator() == Operator.LESSEQ || b1.getOperator() == Operator.NEQ)) {
                    return null;
                }
            }
        }
        if (getFirstExpression() instanceof BinaryExpression && getSecondExpression() instanceof Variable && this.getOperator().getReturnType() == Boolean.class) {
            BinaryExpression b1 = ((BinaryExpression) getFirstExpression());
            Variable v2 = ((Variable) getSecondExpression());

            if (b1.getOperator() == Operator.ADD && (b1.getFirstExpression().sameExpression(v2) || b1.getSecondExpression().sameExpression(v2))) {
                return null;
            }
        }
        if (getSecondExpression() instanceof BinaryExpression && getFirstExpression() instanceof Variable && this.getOperator().getReturnType() == Boolean.class) {
            BinaryExpression b1 = ((BinaryExpression) getSecondExpression());
            Variable v2 = ((Variable) getFirstExpression());

            if (b1.getOperator() == Operator.ADD && (b1.getFirstExpression().sameExpression(v2) || b1.getSecondExpression().sameExpression(v2))) {
                return null;
            }
        }

        Object value = null;
        if (getFirstExpression().getValue() instanceof Integer) {
            value = executeOperatorInteger();
        } else if (getFirstExpression().getValue() instanceof Double) {
            value = executeOperatorDouble();
        } else if (getFirstExpression().getValue() instanceof Long) {
            value = executeOperatorLong();
        } else if (getFirstExpression().getValue() instanceof Float) {
            value = executeOperatorFloat();
        } else if (getFirstExpression().getValue() instanceof Short) {
            value = executeOperatorShort();
        } else if (getFirstExpression().getValue() instanceof Byte) {
            value = executeOperatorByte();
        } else if (getFirstExpression().getValue() instanceof Boolean) {
            value = executeOperatorBoolean();
        }  else if (getFirstExpression() instanceof ComplexTypeExpression) {
            value = executeOperatorComplex();
        }
        return value;
    }

    private Object executeOperatorComplex() {
        if(getSecondExpression().getValue() == null) {
            if(getOperator() == Operator.EQ) {
                return getFirstExpression().getValue() == null;
            } else if(getOperator() == Operator.NEQ) {
                return getFirstExpression().getValue() != null;
            }
        } else {
            if(getOperator() == Operator.EQ) {
                return getFirstExpression().getValue() == getSecondExpression().getValue();
            } else if(getOperator() == Operator.NEQ) {
                return getFirstExpression().getValue() != getSecondExpression().getValue();
            }
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
                if (!firstValue) {
                    return false;
                }
                if (getSecondExpression().getValue() instanceof Boolean) {
                    return getSecondExpression().getValue();
                }
            case OR:
                if (firstValue) {
                    return true;
                }
                if (getSecondExpression().getValue() instanceof Boolean) {
                    return getSecondExpression().getValue();
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

