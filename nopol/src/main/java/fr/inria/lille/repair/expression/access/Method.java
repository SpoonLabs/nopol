package fr.inria.lille.repair.expression.access;


import fr.inria.lille.repair.expression.Expression;

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

