package fr.inria.lille.commons.trace;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.factory.Factory;
import xxl.java.container.classic.MetaList;
import xxl.java.container.map.Multimap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.*;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

public class RuntimeValuesInstrumenter {

    public static <T> CtStatement runtimeCollectionBefore(CtStatement location, Map<String, String> inputs, Multimap<String, String> getters, String output, RuntimeValues<T> runtime) {
        Factory factory = location.getFactory();
        List<CtStatement> newStatements = MetaList.newLinkedList();
        addCollectionStatementFor(newStatements, factory, runtime.invocationOnCollectionStart());
        addVariableCollection(inputs, runtime, factory, newStatements);
        addGetterCollection(getters, runtime, factory, newStatements);
        addCollectionStatementFor(newStatements, factory, runtime.invocationOnOutputCollection(output));
        addCollectionStatementFor(newStatements, factory, runtime.invocationOnCollectionEnd());
        return collectionWrappingIf(newStatements, runtime, location);
    }

    private static <T> void addVariableCollection(Map<String, String> inputs, RuntimeValues<T> runtime, Factory factory, List<CtStatement> newStatements) {
        String variableName;
        String executableCode;
        for (Entry<String, String> entry : inputs.entrySet()) {
            variableName = entry.getKey();
            executableCode = entry.getValue();
            addCollectionStatementFor(newStatements, factory, runtime.invocationOnCollectionOf(variableName, executableCode));
        }
    }

    private static <T> void addGetterCollection(Multimap<String, String> getters, RuntimeValues<T> runtime, Factory factory, List<CtStatement> newStatements) {
        String invocation;
        for (String receiver : getters.keySet()) {
            List<CtStatement> invocations = MetaList.newLinkedList();
            for (String getterName : getters.get(receiver)) {
                invocation = receiver + '.' + getterName + "()";
                addCollectionStatementFor(invocations, factory, runtime.invocationOnCollectionOf(invocation));
            }
            newStatements.add(newIf(factory, newExpressionFromSnippet(factory, receiver + "!=null", Boolean.class), newBlock(factory, invocations)));
        }
    }

    private static <T> void addCollectionStatementFor(List<CtStatement> statements, Factory factory, String codeSnippet) {
        statements.add(newStatementFromSnippet(factory, codeSnippet));
    }

    private static <T> CtIf collectionWrappingIf(List<CtStatement> collectingStatements, RuntimeValues<T> runtimeValues, CtStatement insertionPoint) {
        Factory factory = insertionPoint.getFactory();
        CtStatement newBlock = newBlock(factory, collectingStatements);
        CtExpression<Boolean> isEnabled = newExpressionFromSnippet(factory, runtimeValues.isEnabledInquiry(), Boolean.class);
        CtIf newIf = newIf(factory, isEnabled, newBlock);
        insertBeforeUnderSameParent(newIf, insertionPoint);
        return newIf;
    }

}
