package fr.inria.lille.repair.nopol.spoon.symbolic;

import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import gov.nasa.jpf.symbc.Debug;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

public class SymbolicConditionalAdder extends NopolProcessor {

    public SymbolicConditionalAdder(CtStatement target) {
        super(target, StatementType.PRECONDITION);
        setDefaultValue("true");
        super.setType(Boolean.class);
    }

    public void process(CtStatement element) {
        logger.debug("##### {} ##### Before:\n{}", element, element.getParent());
        CtElement parent = element.getParent();
        CtIf newIf = element.getFactory().Core().createIf();
        CtCodeSnippetExpression<Boolean> condition;
        if (getValue() != null) {
            switch (getValue()) {
                case "1":
                    condition = element.getFactory().Code()
                            .createCodeSnippetExpression("true");
                    break;
                case "0":
                    condition = element.getFactory().Code()
                            .createCodeSnippetExpression("false");
                    break;
                default:
                    condition = element.getFactory().Code()
                            .createCodeSnippetExpression(getValue());
            }
        } else {
            condition = element
                    .getFactory()
                    .Code()
                    .createCodeSnippetExpression(
                            Debug.class.getCanonicalName()
                                    + ".makeSymbolicBoolean(\"guess_fix\")");
        }
        newIf.setCondition(condition);
        // Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2] != [CtElem3] )
        newIf.setParent(parent);
        element.replace(newIf);
        // this should be after the replace to avoid an StackOverflowException caused by the circular reference.
        newIf.setThenStatement(element);
        // Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2] != [CtElem3] )
        newIf.getThenStatement().setParent(newIf);
        logger.debug("##### {} ##### After:\n{}", element, element.getParent().getParent());
    }
}
