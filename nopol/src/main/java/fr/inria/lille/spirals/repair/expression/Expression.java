package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Type;
import fr.inria.lille.spirals.repair.commons.Candidates;

import java.util.List;

/**
 * is the generic type of an expression
 */
public interface Expression extends Cloneable, Comparable<Expression> {

    List<Expression> getAlternatives();

    List<Expression> getInAlternativesOf();

    List<Expression> getInExpressions();

    Class getType();

    boolean isAssignableTo(Type refAss);

    Object getValue();

    boolean sameExpression(Expression exp2);

    int countInnerExpression();

    double getWeight();

    void setPriority(double priority);

    double getPriority();

    Object evaluate(Candidates values);

    String asPatch();
}

