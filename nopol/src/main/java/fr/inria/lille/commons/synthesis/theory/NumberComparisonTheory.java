package fr.inria.lille.commons.synthesis.theory;

import fr.inria.lille.commons.synthesis.operator.BinaryOperator;

public class NumberComparisonTheory extends OperatorTheory {

    public NumberComparisonTheory() {
        super(BinaryOperator.numberEquality(), BinaryOperator.numberDistinction(), BinaryOperator.lessThan(), BinaryOperator.lessOrEqualThan());
    }
}
