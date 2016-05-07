package fr.inria.lille.spirals.repair.expressionV2.access;


import fr.inria.lille.spirals.repair.expressionV2.Expression;

import java.util.List;

/**
 *
 *
 *
 */
public interface Method extends Expression {

    Expression getTarget();

    String getMethod();

    List<Expression> getParameters();
}

