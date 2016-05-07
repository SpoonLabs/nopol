package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

import java.util.List;


/**
 * is the generic type of a binary expression
 */

public class ArrayExpressionImpl extends ExpressionImpl implements ArrayExpression {
    private String cType;
    private Value jdiValue;
    private int length;
    private List<ArrayAccessExpression> values;

    /**
     *
     */
    public ArrayExpressionImpl(String cType, List<ArrayAccessExpression> values, Value jdiValue, Object value, Class type) {
        super(value, type);
        this.cType = cType;
        this.jdiValue = jdiValue;
        this.length = values.size();
        this.values = values;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public List<ArrayAccessExpression> getValues() {
        return values;
    }

    public Value getJdiValue() {
        return jdiValue;
    }

    public String getcType() {
        return cType;
    }

    @Override
    public int countInnerExpression() {
        return length();
    }

    @Override
    public double getWeight() {
        double weight = 0;
        for (Expression expression : values) {
            weight += expression.getWeight();
        }
        return getPriority() + weight;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    @Override
    public String asPatch() {
        return toString();
    }
}

