package fr.inria.lille.repair.expression.value;


import java.util.List;

/**
 * is the generic type of a binary expression
 */
public interface ArrayValue extends Value {

    String getArrayType();

    int length();

    List<Value> getValues();
}

