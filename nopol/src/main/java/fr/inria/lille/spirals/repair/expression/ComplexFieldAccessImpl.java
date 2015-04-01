package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

/**
 *
 *
 *
 */

public class ComplexFieldAccessImpl extends FieldAccessImpl implements ComplexFieldAccess {
    /**
     *
     */
    public ComplexFieldAccessImpl(String fieldName, ComplexTypeExpression expression, Value value, Class returnType) {
        super(fieldName, expression, value, value, returnType);
    }

}

