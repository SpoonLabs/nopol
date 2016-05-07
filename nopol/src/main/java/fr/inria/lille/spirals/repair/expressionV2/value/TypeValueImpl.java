package fr.inria.lille.spirals.repair.expressionV2.value;

import com.sun.jdi.ClassType;

public class TypeValueImpl extends AbstractValue implements TypeValue {
    public TypeValueImpl(ClassType JDIValue) {
        super(JDIValue);
    }
}
