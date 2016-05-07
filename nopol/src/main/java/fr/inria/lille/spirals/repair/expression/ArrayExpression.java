package fr.inria.lille.spirals.repair.expression;


import java.util.List;

/**
 * is the generic type of a binary expression
 */
public interface ArrayExpression extends Expression {

    String getcType();

    int length();

    List<ArrayAccessExpression> getValues();
}

