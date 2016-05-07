package fr.inria.lille.spirals.repair.expressionV2.combination;


import fr.inria.lille.spirals.repair.expressionV2.Expression;

/**
 * is the generic type of a binary expression
 */
public interface CombinationExpression extends Expression {
    int nbSubExpression();
}

