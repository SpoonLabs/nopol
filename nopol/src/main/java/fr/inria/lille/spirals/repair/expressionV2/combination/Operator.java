package fr.inria.lille.spirals.repair.expressionV2.combination;


import java.util.List;

public interface Operator {
    String getSymbol();

    Class getReturnType();

    List<Class> getTypeParameters();
}

