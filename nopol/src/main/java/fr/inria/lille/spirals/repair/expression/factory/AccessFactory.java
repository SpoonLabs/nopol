package fr.inria.lille.spirals.repair.expression.factory;

import fr.inria.lille.spirals.repair.expression.Expression;
import fr.inria.lille.spirals.repair.expression.access.*;
import fr.inria.lille.spirals.repair.expression.value.ArrayValue;
import fr.inria.lille.spirals.repair.expression.value.Value;

import java.util.List;

public class AccessFactory {

    public static Array array(Expression target, Expression index) {
        if (target.getValue() instanceof ArrayValue) {
            Value array = ((ArrayValue) target.getValue()).getValues().get(0);
            return new ArrayImpl(target, index, array);
        }
        return null;
    }

    public static Array array(Expression target, Expression index, Object v) {
        return new ArrayImpl(target, index, ValueFactory.create(v));
    }

    public static Literal literal(Object o) {
        return new LiteralImpl(ValueFactory.create(o));
    }

    public static Method method(String name, List<String> argumentTypes, String declaringType, Expression target, List<Expression> parameters, Object o) {
        return new MethodImpl(name, argumentTypes, declaringType, target, parameters, ValueFactory.create(o));
    }

    public static Variable variable(Expression target, String name, Object o) {
        return new VariableImpl(name, target, ValueFactory.create(o));
    }

    public static Variable variable(String name, Object o) {
        return variable(null, name, o);
    }
}
