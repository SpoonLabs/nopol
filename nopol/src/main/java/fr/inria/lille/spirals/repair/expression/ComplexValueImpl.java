package fr.inria.lille.spirals.repair.expression;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;

public class ComplexValueImpl extends VariableImpl implements ComplexValue {
    /**
     * @param variableName
     * @param value
     */
    public ComplexValueImpl(String variableName, Value value) {
        super(variableName, value, value, Object.class);
    }

    public ComplexValueImpl(String variableName, ReferenceType ref) {
        super(variableName, null, ref, Object.class);
    }
}

