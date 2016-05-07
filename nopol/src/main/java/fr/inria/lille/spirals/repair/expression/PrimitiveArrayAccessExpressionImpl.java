package fr.inria.lille.spirals.repair.expression;


/**
 * is the generic type of a binary expression
 */

public class PrimitiveArrayAccessExpressionImpl extends ArrayAccessExpressionImpl implements PrimitiveArrayAccessExpression{

    /**
     * @param index
     * @param array
     * @param value
     * @param type
     */
    public PrimitiveArrayAccessExpressionImpl(int index, ArrayExpression array, Object value, Class type) {
        super(index, array, null, value, type);
    }

}

