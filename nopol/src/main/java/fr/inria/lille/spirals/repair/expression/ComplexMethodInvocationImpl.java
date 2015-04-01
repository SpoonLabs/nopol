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
    public ComplexMethodInvocationImpl(String method, String declaringType, Expression expression, List<Expression> parameters, Value value, Class type) {
        super(method, declaringType, expression, parameters, value, value, type);
    }

}

