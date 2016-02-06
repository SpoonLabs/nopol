package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.operator.BinaryOperator;

/**
 *
 *
 *
 */

public class PrimitiveBinaryExpressionImpl extends BinaryExpressionImpl implements PrimitiveBinaryExpression {
    /**
     *
     */
    public PrimitiveBinaryExpressionImpl(BinaryOperator operator, Expression first, Expression second, Object value, Class type) {
        super(operator, first, second, value, type);
    }

    public PrimitiveBinaryExpressionImpl(BinaryOperator operator, Expression first, Expression second) {
        super(operator, first, second, null, operator.getReturnType());
        evaluate();
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


    @Override
    protected Object getValueFromOperation() {
        if (!getOperator().getParam1().isAssignableFrom(getFirstExpression().getType())) {
            return null;
        }
        if (!getOperator().getParam2().isAssignableFrom(getSecondExpression().getType())) {
            return null;
        }
        if (getFirstExpression().getValue() == null && !(getOperator() == BinaryOperator.EQ || getOperator() == BinaryOperator.NEQ)) {
            return null;
        }
        if (getSecondExpression().getValue() == null && !(getOperator() == BinaryOperator.EQ || getOperator() == BinaryOperator.NEQ || getOperator() == BinaryOperator.OR)) {
            return null;
        }
        if (Number.class.isAssignableFrom(getFirstExpression().getType()) && getFirstExpression() instanceof Constant) {
            Number value = ((Number) getFirstExpression().getValue());
            if (value != null) {
                int val = value.intValue();
                if (val == 0 && (getOperator() == BinaryOperator.MULT || getOperator() == BinaryOperator.DIV)) {
                    return null;
                }
                if (val == 1 && (getOperator() == BinaryOperator.MULT)) {
                    return null;
                }
                if (val == 0 && (getOperator() == BinaryOperator.ADD)) {
                    return null;
                }
            }
        }
        if (Number.class.isAssignableFrom(getSecondExpression().getType()) && getSecondExpression() instanceof Constant) {
            Number value = ((Number) getSecondExpression().getValue());
            if (value != null) {
                int val = value.intValue();
                if (val == 0 && (getOperator() == BinaryOperator.MULT || getOperator() == BinaryOperator.DIV)) {
                    return null;
                }
                if (val == 1 && (getOperator() == BinaryOperator.MULT || getOperator() == BinaryOperator.DIV)) {
                    return null;
                }
                if (val == 0 && (getOperator() == BinaryOperator.ADD || getOperator() == BinaryOperator.SUB)) {
                    return null;
                }
            }
        }
        if (getOperator() == BinaryOperator.SUB) {
            if (getFirstExpression().sameExpression(getSecondExpression())) {
                return null;
            }
            if (getFirstExpression() instanceof BinaryExpression && ((BinaryExpression) getFirstExpression()).getOperator() == BinaryOperator.ADD) {
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

            if ((getOperator() == BinaryOperator.AND || getOperator() == BinaryOperator.OR) && b1.getFirstExpression().sameExpression(b2.getFirstExpression()) && b1.getSecondExpression().sameExpression(b2.getSecondExpression())) {
                if (b1.getOperator() == BinaryOperator.EQ && (b2.getOperator() == BinaryOperator.LESSEQ || b2.getOperator() == BinaryOperator.NEQ)) {
                    return null;
                }
                if (b2.getOperator() == BinaryOperator.EQ && (b1.getOperator() == BinaryOperator.LESSEQ || b1.getOperator() == BinaryOperator.NEQ)) {
                    return null;
                }
            }
        }
        if (getFirstExpression() instanceof BinaryExpression && getSecondExpression() instanceof Variable && this.getOperator().getReturnType() == Boolean.class) {
            BinaryExpression b1 = ((BinaryExpression) getFirstExpression());
            Variable v2 = ((Variable) getSecondExpression());

            if (b1.getOperator() == BinaryOperator.ADD && (b1.getFirstExpression().sameExpression(v2) || b1.getSecondExpression().sameExpression(v2))) {
                return null;
            }
        }
        if (getSecondExpression() instanceof BinaryExpression && getFirstExpression() instanceof Variable && this.getOperator().getReturnType() == Boolean.class) {
            BinaryExpression b1 = ((BinaryExpression) getSecondExpression());
            Variable v2 = ((Variable) getFirstExpression());

            if (b1.getOperator() == BinaryOperator.ADD && (b1.getFirstExpression().sameExpression(v2) || b1.getSecondExpression().sameExpression(v2))) {
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
        } else if (getFirstExpression() instanceof ComplexTypeExpression) {
            value = executeOperatorComplex();
        }
        return value;
    }

    private Object executeOperatorComplex() {
        if (getSecondExpression().getValue() == null) {
            if (getOperator() == BinaryOperator.EQ) {
                return getFirstExpression().getValue() == null;
            } else if (getOperator() == BinaryOperator.NEQ) {
                return getFirstExpression().getValue() != null;
            }
        } else {
            if (getOperator() == BinaryOperator.EQ) {
                return getFirstExpression().getValue() == getSecondExpression().getValue();
            } else if (getOperator() == BinaryOperator.NEQ) {
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
            case OR:
                if (firstValue) {
                    return true;
                }
                if (getSecondExpression().getValue() instanceof Boolean) {
                    return getSecondExpression().getValue();
                }
                break;
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

