package fr.inria.lille.repair.nopol.spoon.symbolic;

import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.nopol.spoon.smt.ConditionalProcessor;
import gov.nasa.jpf.symbc.Debug;
import spoon.reflect.code.*;

public class SymbolicConditionalReplacer extends NopolProcessor {

    public SymbolicConditionalReplacer(CtStatement target) {
        super(target, StatementType.CONDITIONAL);
        setDefaultValue(((CtIf) target).getCondition().toString());
        super.setType(Boolean.class);
    }

    public static CtExpression<Boolean> getCondition(CtStatement element) {
        return ConditionalProcessor.getCondition(element);
    }

    @Override
    public void process(CtStatement element) {
        String oldStatement = element.toString();
        CtCodeSnippetExpression<Boolean> snippet = null;
        if (getValue() != null) {
            if (getValue().equals("1")) {
                snippet = element.getFactory().Code()
                        .createCodeSnippetExpression("true");
            } else if (getValue().equals("0")) {
                snippet = element.getFactory().Code()
                        .createCodeSnippetExpression("false");
            } else {
                snippet = element.getFactory().Code()
                        .createCodeSnippetExpression(getValue());
            }
        } else {
            snippet = element
                    .getFactory()
                    .Code()
                    .createCodeSnippetExpression(
                            Debug.class.getCanonicalName()
                                    + ".makeSymbolicBoolean(\"guess_fix\")");
        }
        CtExpression<Boolean> condition = getCondition(element);

        condition.replace(snippet);
        logger.debug("Replacing:\n{}\nby:\n{}", oldStatement, element);
    }

}
