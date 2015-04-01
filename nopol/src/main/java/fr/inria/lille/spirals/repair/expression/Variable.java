package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

/**
 * is the generic type of an unary expression
 */
public interface Variable extends Expression {
    String getVariableName();


    Value getJdiValue();
}

