package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

import java.util.List;

/**
 *
 *
 *
 */

public class ArrayMethodInvocationImpl extends ComplexMethodInvocationImpl implements ArrayMethod {
    /**
     *
     */
    public ArrayMethodInvocationImpl(String method, List<String> argumentTypes, String declaringType, Expression expression, List<Expression> parameters, Value value, Class type) {
        super(method, argumentTypes, declaringType, expression, parameters, value, type);
    }

    @Override
    public String getcType() {
        return null;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public List<ArrayAccessExpression> getValues() {
        return null;
    }
}

