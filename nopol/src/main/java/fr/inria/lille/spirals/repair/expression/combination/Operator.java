package fr.inria.lille.spirals.repair.expression.combination;


import java.util.List;

public interface Operator {
    String getSymbol();

    Class getReturnType();

    List<Class> getTypeParameters();
}

