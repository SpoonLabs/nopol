package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;


/**
 * is the generic type of a binary expression
 */

public class ComplexArrayAccessExpressionImpl extends ArrayAccessExpressionImpl implements ComplexArrayAccessExpression {

    /**
     * @param index
     * @param array
     * @param jdiValue
     * @param value
     * @param type
     */
    public ComplexArrayAccessExpressionImpl(int index, ArrayExpression array, Value jdiValue, Object value, Class type) {
        super(index, array, jdiValue, value, type);
    }

}

