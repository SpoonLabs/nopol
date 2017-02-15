package fr.inria.lille.repair.expression.access;


import fr.inria.lille.repair.expression.Expression;


public interface Array extends Expression {

    Expression getIndex();

    Expression getTarget();
}

