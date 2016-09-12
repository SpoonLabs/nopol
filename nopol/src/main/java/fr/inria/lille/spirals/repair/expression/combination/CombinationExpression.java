package fr.inria.lille.spirals.repair.expression.combination;


import fr.inria.lille.spirals.repair.expression.Expression;

/**
 * is the generic type of a binary expression
 */
public interface CombinationExpression extends Expression {
    int nbSubExpression();
}

