package fr.inria.lille.repair.nopol.spoon.smt;

import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

public abstract class ConditionalProcessor extends NopolProcessor {

    public abstract CtIf processCondition(CtStatement statement, String newCondition);

    public ConditionalProcessor(CtStatement target, String defaultCondition, StatementType type) {
        super(target, type);
        setDefaultValue(defaultCondition);
    }

    public static CtExpression<Boolean> getCondition(CtElement element) {
        CtExpression<Boolean> condition;
        if (element instanceof CtIf) {
            condition = ((CtIf) element).getCondition();
        } else if (element instanceof CtConditional) {
            condition = ((CtConditional<?>) element).getCondition();
        } else {
            throw new IllegalStateException("Unknown conditional class: " + element.getClass());
        }
        return condition;
    }

    @Override
    public void process(CtStatement statement) {
        if (getValue() != null) {
            processCondition(statement, getValue());
        } else {
            processCondition(statement, getDefaultValue());
        }
    }
}
