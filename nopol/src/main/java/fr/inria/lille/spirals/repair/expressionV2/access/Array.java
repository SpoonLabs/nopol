package fr.inria.lille.spirals.repair.expressionV2.access;


import fr.inria.lille.spirals.repair.expressionV2.Expression;


public interface Array extends Expression {

    Expression getIndex();

    Expression getTarget();
}

