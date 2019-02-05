package fr.inria.lille.repair.expression.factory;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import fr.inria.lille.repair.expression.value.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ValueFactory {

    public static Value create(Object value) {
        if (value == null) {
            return new ComplexValueImpl(null);
        }
        if (value instanceof Value) {
            return (Value) value;
        }
        if (value instanceof ArrayReference) {
            ArrayReference array = (ArrayReference) value;
            List<Value> arrayValues = create(array.getValues());
            return new ArrayValueImpl(array.type().name(), array, arrayValues, value);
        }
        if (value instanceof ObjectReference) {
            return new ComplexValueImpl((ObjectReference) value);
        }
        if (value instanceof ClassType) {
            return new TypeValueImpl((ClassType) value);
        }
        if (value instanceof PrimitiveValue) {
            try {
                java.lang.reflect.Method valueMethod = value.getClass().getMethod("value");
                Object result = valueMethod.invoke(value);
                PrimitiveValueImpl primitiveValue = new PrimitiveValueImpl(result);
                primitiveValue.setJDIValue((PrimitiveValue) value);
                return primitiveValue;
            } catch (Exception e) {
                return null;
            }
        }
        if (value.getClass().isArray()) {
            List<Object> values = new ArrayList<>();
            int length = Array.getLength(value);
            for (int i = 0; i< length; i++) {
                values.add(Array.get(value, i));
            }
            return new ArrayValueImpl(value.getClass().getComponentType().getCanonicalName(), null, create(values), value);
        }
        return new PrimitiveValueImpl(value);
    }

    public static List<Value> create(List<?> v) {
        ArrayList<Value> output = new ArrayList<>(v.size());
        for (int i = 0; i < v.size(); i++) {
            Object o = v.get(i);
            output.add(ValueFactory.create(o));
        }
        return output;
    }
}
