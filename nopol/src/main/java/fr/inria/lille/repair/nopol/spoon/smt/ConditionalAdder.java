package fr.inria.lille.repair.nopol.spoon.smt;

import fr.inria.lille.repair.common.synth.StatementType;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

public class ConditionalAdder extends ConditionalProcessor {

    public ConditionalAdder(CtStatement target) {
        super(target, "true", StatementType.PRECONDITION);
    }

    @Override
    public CtIf processCondition(CtStatement element, String newCondition) {
        //logger.debug("##### {} ##### Before:\n{}", element, element.getParent());
        CtElement parent = element.getParent();
        CtIf newIf = element.getFactory().Core().createIf();
        CtCodeSnippetExpression<Boolean> condition = element.getFactory().Core().createCodeSnippetExpression();
        condition.setValue(newCondition);
        newIf.setCondition(condition);
        // Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2] != [CtElem3] )
        newIf.setParent(parent);
        element.replace(newIf);
        // this should be after the replace to avoid an StackOverflowException caused by the circular reference.
        newIf.setThenStatement(element);
        // Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2] != [CtElem3] )
        newIf.getThenStatement().setParent(newIf);
        //logger.debug("##### {} ##### After:\n{}", element, element.getParent().getParent());
        return newIf;
    }
}
