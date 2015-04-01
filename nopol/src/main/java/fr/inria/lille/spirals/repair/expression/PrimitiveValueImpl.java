package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

/**
 * is the generic type of a primitive value
 */

public abstract class PrimitiveValueImpl extends VariableImpl implements PrimitiveValue {

    /**
     * @param variableName
     * @param value
     * @param type
     */
    public PrimitiveValueImpl(String variableName, Value jdiValue, Object value, Class<?> type) {
        super(variableName, jdiValue, value, type);
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof PrimitiveValue)) return false;

        PrimitiveValue that = (PrimitiveValue) o;
        return this.toString().equals(that.toString());
    }*/
}

