package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

/**
 * is the generic type of a numerical value
 */

public class NumericalValueImpl extends PrimitiveValueImpl implements NumericalValue {
    /**
     *
     */
    public NumericalValueImpl(String variableName, Value jdiValue, Object value, Class type) {
        super(variableName, jdiValue, value, type);
    }
}

