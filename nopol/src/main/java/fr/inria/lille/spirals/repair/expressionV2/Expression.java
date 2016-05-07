package fr.inria.lille.spirals.repair.expressionV2;


import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expressionV2.value.Value;

/**
 * is the generic type of an expression
 */
public interface Expression extends Cloneable, Comparable<Expression> {

    Value getValue();

    void setValue(Value value);

    boolean sameExpression(Expression exp2);

    double getWeight();

    double getPriority();

    Value evaluate(Candidates values);

    String asPatch();
}

