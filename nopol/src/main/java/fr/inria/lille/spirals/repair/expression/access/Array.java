package fr.inria.lille.spirals.repair.expression.access;


import fr.inria.lille.spirals.repair.expression.Expression;


public interface Array extends Expression {

    Expression getIndex();

    Expression getTarget();
}

