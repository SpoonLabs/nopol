package fr.inria.lille.spirals.repair.expression.access;


import fr.inria.lille.spirals.repair.expression.Expression;

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

