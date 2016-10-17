package fr.inria.lille.repair.nopol.spoon;

import fr.inria.lille.commons.spoon.collectable.CollectableValueFinder;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.RuntimeValuesInstrumenter;
import fr.inria.lille.repair.nopol.spoon.smt.ConditionalProcessor;
import fr.inria.lille.repair.nopol.synth.AngelicExecution;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.map.Multimap;

import java.util.Collection;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclarationString;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

public final class ConditionalLoggingInstrumenter extends AbstractProcessor<CtStatement> {

    public ConditionalLoggingInstrumenter(RuntimeValues<Boolean> runtimeValues, NopolProcessor subprocessor) {
        this.subprocessor = subprocessor;
        this.runtimeValues = runtimeValues;
    }

    @Override
    public boolean isToBeProcessed(CtStatement statement) {
        return subprocessor().isToBeProcessed(statement);
    }

    @Override
    public void process(CtStatement statement) {
        String evaluationAccess = "runtimeAngelicValue";

        CtLocalVariable defaultValue = newLocalVariableDeclarationString(statement.getFactory(), Boolean.class, "spoonDefaultValue", "false");
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
        CtLocalVariable<Boolean> evaluation = newLocalVariableDeclaration(statement.getFactory(), Boolean.class, evaluationAccess, evaluationValue);
        insertBeforeUnderSameParent(evaluation, statement);
        appendValueCollection(statement, evaluationAccess, "spoonDefaultValue");
        ((ConditionalProcessor) subprocessor()).processCondition(statement, evaluationAccess);
    }

    protected String angelicInvocation(String booleanSnippet) {
        return AngelicExecution.invocation(booleanSnippet);
    }

    public void appendValueCollection(CtStatement element, String outputName, String... ignore) {
        CollectableValueFinder finder;
        if (CtIf.class.isInstance(element)) {
            finder = CollectableValueFinder.valueFinderFromIf((CtIf) element);
        } else {
            finder = CollectableValueFinder.valueFinderFrom(element);
        }
        Collection<String> collectables = finder.reachableVariables();
        Multimap<String, String> getters = finder.accessibleGetters();
        collectables.remove(outputName);
        for (int i = 0; i < ignore.length; i++) {
            String s = ignore[i];
            collectables.remove(s);
        }
        RuntimeValuesInstrumenter.runtimeCollectionBefore(element, MetaMap.autoMap(collectables), getters, outputName, runtimeValues());
    }

    private RuntimeValues<Boolean> runtimeValues() {
        return runtimeValues;
    }

    private NopolProcessor subprocessor() {
        return subprocessor;
    }

    private NopolProcessor subprocessor;
    private RuntimeValues<Boolean> runtimeValues;
}
