package fr.inria.lille.spirals.repair.expression.value;


public class PrimitiveValueImpl extends AbstractValue implements PrimitiveValue {

    public PrimitiveValueImpl(Object realValue) {
        super(realValue);
        super.setPrimitive(true);
    }
}
