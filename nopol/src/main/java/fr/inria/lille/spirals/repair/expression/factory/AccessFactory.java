package fr.inria.lille.spirals.repair.expression.factory;

import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.expression.Expression;
import fr.inria.lille.spirals.repair.expression.access.*;
import fr.inria.lille.spirals.repair.expression.value.ArrayValue;
import fr.inria.lille.spirals.repair.expression.value.Value;

import java.util.List;

public class AccessFactory {

    public static Array array(Expression target, Expression index, Config config) {
        if (target.getValue() instanceof ArrayValue) {
            Value array = ((ArrayValue) target.getValue()).getValues().get(0);
            return new ArrayImpl(target, index, array, config);
        }
        return null;
    }

    public static Array array(Expression target, Expression index, Object v, Config config) {
        return new ArrayImpl(target, index, ValueFactory.create(v), config);
    }

    public static Literal literal(Object o, Config config) {
        return new LiteralImpl(ValueFactory.create(o), config);
    }

    public static Method method(String name, List<String> argumentTypes, String declaringType, Expression target, List<Expression> parameters, Object o, Config config) {
        return new MethodImpl(name, argumentTypes, declaringType, target, parameters, ValueFactory.create(o), config);
    }

    public static Variable variable(Expression target, String name, Object o, Config config) {
        return new VariableImpl(name, target, ValueFactory.create(o), config);
    }

    public static Variable variable(String name, Object o, Config config) {
        return variable(null, name, o, config);
    }
}
