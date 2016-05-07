package fr.inria.lille.spirals.repair.expressionV2.value;

import com.sun.jdi.Value;

public class ComplexValueImpl extends AbstractValue implements ComplexValue {
    public ComplexValueImpl(Value JDIValue) {
        super(JDIValue);
        setJDIValue(JDIValue);
    }
}
