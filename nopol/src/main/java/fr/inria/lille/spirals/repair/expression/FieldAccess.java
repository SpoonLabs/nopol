package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

/**
 *
 *
 *
 */
public interface FieldAccess extends Expression {
    /**
     *
     */
    ComplexTypeExpression getExpression();

    /**
     *
     */
    String getFieldName();


    Value getJdiValue();
}

