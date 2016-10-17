package fr.inria.lille.repair.nopol.spoon.dynamoth;

import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.nopol.spoon.smt.ConditionalProcessor;
import fr.inria.lille.repair.nopol.synth.AngelicExecution;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclarationString;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

public final class ConditionalInstrumenter<T> extends AbstractProcessor<CtStatement> {

    private final Class cl;

    public ConditionalInstrumenter(NopolProcessor subprocessor, Class<T> cl) {
        this.subprocessor = subprocessor;
        this.cl = cl;
    }

    @Override
    public boolean isToBeProcessed(CtStatement statement) {
        return subprocessor().isToBeProcessed(statement);
    }

    @Override
    public void process(CtStatement statement) {
        String evaluationAccess = "runtimeAngelicValue";

        CtLocalVariable defaultValue = newLocalVariableDeclarationString(statement.getFactory(), cl, "spoonDefaultValue", "false");
        insertBeforeUnderSameParent(defaultValue, statement);

        CtTry aTry = getFactory().Core().createTry();
        CtAssignment variableAssignment = getFactory().Code().createVariableAssignment(defaultValue.getReference(), false, getFactory().Code().createCodeSnippetExpression(subprocessor().getDefaultValue()));
        aTry.setBody(getFactory().Code().createCtBlock(variableAssignment));

        CtCatch aCatch = getFactory().Core().createCatch();
        CtCatchVariable<Exception> nopolProcessorException = getFactory().Code().createCatchVariable(getFactory().Type().createReference(Exception.class), "__NopolProcessorException");
        aCatch.setParameter(nopolProcessorException);
        aCatch.setBody(getFactory().Core().createBlock());

        aTry.addCatcher(aCatch);

        insertBeforeUnderSameParent(aTry, statement);

        String evaluationValue = angelicInvocation("spoonDefaultValue");
        CtLocalVariable<T> evaluation = newLocalVariableDeclarationString(statement.getFactory(), cl, evaluationAccess, evaluationValue);
        insertBeforeUnderSameParent(evaluation, statement);
        ((ConditionalProcessor) subprocessor()).processCondition(statement, evaluationAccess);
    }

    protected String angelicInvocation(String booleanSnippet) {
        return AngelicExecution.invocation(booleanSnippet);
    }

    private NopolProcessor subprocessor() {
        return subprocessor;
    }

    private NopolProcessor subprocessor;
}
