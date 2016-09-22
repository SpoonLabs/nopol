package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.value.Value;

import java.io.Serializable;

/**
 * is the generic type of an expression
 */
public interface Expression extends Cloneable, Comparable<Expression>, Serializable {

    Value getValue();

    void setValue(Value value);

    boolean sameExpression(Expression exp2);

    double getWeight();

    double getPriority();

    Value evaluate(Candidates values);

    String asPatch();
}

