package fr.inria.lille.repair.expression.factory;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.access.*;
import fr.inria.lille.repair.expression.value.ArrayValue;
import fr.inria.lille.repair.expression.value.Value;

import java.util.List;

public class AccessFactory {

    public static Array array(Expression target, Expression index, NopolContext nopolContext) {
        if (target.getValue() instanceof ArrayValue) {
            Value array = ((ArrayValue) target.getValue()).getValues().get(0);
            return new ArrayImpl(target, index, array, nopolContext);
        }
        return null;
    }

    public static Array array(Expression target, Expression index, Object v, NopolContext nopolContext) {
        return new ArrayImpl(target, index, ValueFactory.create(v), nopolContext);
    }

    public static Literal literal(Object o, NopolContext nopolContext) {
        return new LiteralImpl(ValueFactory.create(o), nopolContext);
    }

    public static Method method(String name, List<String> argumentTypes, String declaringType, Expression target, List<Expression> parameters, Object o, NopolContext nopolContext) {
        return new MethodImpl(name, argumentTypes, declaringType, target, parameters, ValueFactory.create(o), nopolContext);
    }

    public static Variable variable(Expression target, String name, Object o, NopolContext nopolContext) {
        return new VariableImpl(name, target, ValueFactory.create(o), nopolContext);
    }

    public static Variable variable(String name, Object o, NopolContext nopolContext) {
        return variable(null, name, o, nopolContext);
    }
}
