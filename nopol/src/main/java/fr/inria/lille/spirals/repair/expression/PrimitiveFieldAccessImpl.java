package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

/**
 *
 *
 *
 */

public class PrimitiveFieldAccessImpl extends FieldAccessImpl implements PrimitiveFieldAccess {
    /**
     *
     */
    public PrimitiveFieldAccessImpl(String fieldName, ComplexTypeExpression expression, Value jdiValue, Object value, Class returnType) {
        super(fieldName, expression, jdiValue, value, returnType);
    }
}

