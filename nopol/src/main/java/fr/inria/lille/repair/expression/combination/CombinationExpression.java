package fr.inria.lille.repair.expression.combination;


import fr.inria.lille.repair.expression.Expression;

/**
 * is the generic type of a binary expression
 */
public interface CombinationExpression extends Expression {
    int nbSubExpression();
}

