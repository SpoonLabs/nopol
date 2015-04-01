package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

/**
 * is a boolean value
 */

public class BooleanValueImpl extends PrimitiveValueImpl implements BooleanValue {
    /**
     *
     */
    public BooleanValueImpl(String variableName, Value jdiValue, Boolean value) {
        super(variableName, jdiValue, value, Boolean.class);
    }
}

