package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

import java.util.List;

/**
 *
 *
 *
 */
public interface MethodInvocation extends Expression {
    /**
     *
     */

    Expression getExpression();

    /**
     *
     */

    String getMethod();

    /**
     *
     */
    List<Expression> getParameters();

    Value getJdiValue();


}

