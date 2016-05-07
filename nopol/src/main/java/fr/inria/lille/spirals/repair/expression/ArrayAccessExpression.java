package fr.inria.lille.spirals.repair.expression;


/**
 * is the generic type of a binary expression
 */
public interface ArrayAccessExpression extends Expression {

    int getIndex();

    ArrayExpression getArray();
}

