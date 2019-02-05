package fr.inria.lille.repair.expression.value;

import java.util.List;

public class ArrayValueImpl extends AbstractValue implements ArrayValue {
    private final int length;
    private String arrayType;
    private transient com.sun.jdi.Value JDIValue;
    private List<Value> values;

    public ArrayValueImpl(String arrayType, com.sun.jdi.Value JDIValue, List<Value> values, Object realValue) {
        super(realValue);
        this.JDIValue = JDIValue;
        this.values = values;
        this.length = values.size();
        this.arrayType = arrayType;
    }

    @Override
    public String getArrayType() {
        return arrayType;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public List<Value> getValues() {
        return values;
    }

    @Override
    public com.sun.jdi.Value getJDIValue() {
        return JDIValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getArrayType());
        sb.append("[]{");
        for (int i = 0; i < values.size(); i++) {
            Value value = values.get(i);
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(value);
        }
        sb.append("}");
        return sb.toString();
    }
}
