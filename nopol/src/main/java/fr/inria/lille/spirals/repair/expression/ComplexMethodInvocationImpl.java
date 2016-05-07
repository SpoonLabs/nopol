package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

import java.util.List;

/**
 *
 *
 *
 */

public class ComplexMethodInvocationImpl extends MethodInvocationImpl implements ComplexMethodInvocation {
    /**
     *
     */
    public ComplexMethodInvocationImpl(String method, List<String> argumentTypes, String declaringType, Expression expression, List<Expression> parameters, Value value, Class type) {
        super(method, argumentTypes, declaringType, expression, parameters, value, value, type);
    }

}

