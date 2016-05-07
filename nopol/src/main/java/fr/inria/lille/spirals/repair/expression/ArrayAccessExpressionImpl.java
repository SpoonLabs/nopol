package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;


/**
 * is the generic type of a binary expression
 */

public abstract class ArrayAccessExpressionImpl extends ExpressionImpl implements ArrayAccessExpression {
    private ArrayExpression array;
    private int index;
    private Value jdiValue;

    /**
     *
     */
    public ArrayAccessExpressionImpl(int index, ArrayExpression array, Value jdiValue, Object value, Class type) {
        super(value, type);
        this.array = array;
        this.index = index;
        this.jdiValue = jdiValue;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public ArrayExpression getArray() {
        return array;
    }

    @Override
    public int countInnerExpression() {
        return 1;
    }

    @Override
    public double getWeight() {
        return getPriority();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getArray().toString());
        sb.append("[");
        sb.append(index);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String asPatch() {
        StringBuilder sb = new StringBuilder();
        sb.append(getArray().asPatch());
        sb.append("[");
        sb.append(index);
        sb.append("]");
        return sb.toString();
    }
}

