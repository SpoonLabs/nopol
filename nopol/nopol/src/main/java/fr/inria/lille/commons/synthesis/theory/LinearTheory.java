package fr.inria.lille.commons.synthesis.theory;

import fr.inria.lille.commons.synthesis.operator.BinaryOperator;

public class LinearTheory extends OperatorTheory {

    public LinearTheory() {
        super(BinaryOperator.addition(), BinaryOperator.subtraction());
    }
}
