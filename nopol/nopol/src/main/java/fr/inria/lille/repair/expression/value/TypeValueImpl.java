package fr.inria.lille.repair.expression.value;

import com.sun.jdi.ClassType;

public class TypeValueImpl extends AbstractValue implements TypeValue {
    public TypeValueImpl(ClassType JDIValue) {
        super(JDIValue);
    }
}
