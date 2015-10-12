package fr.inria.lille.commons.synthesis.theory;

import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.UnaryOperator;

public class LogicTheory extends OperatorTheory {

    public LogicTheory() {
        super(UnaryOperator.not(), BinaryOperator.or(), BinaryOperator.and());
    }

}
