package fr.inria.lille.repair.nopol.spoon;

import fr.inria.lille.commons.spoon.collectable.CollectableValueFinder;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.RuntimeValuesInstrumenter;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.map.Multimap;

import java.util.Collection;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

public final class LoggingInstrumenter<T> extends
        AbstractProcessor<CtStatement> {

    private static Object value;

    public LoggingInstrumenter(RuntimeValues<T> runtimeValues,
                               NopolProcessor subprocessor) {
        this.subprocessor = subprocessor;
        this.runtimeValues = runtimeValues;
    }

    @Override
    public boolean isToBeProcessed(CtStatement statement) {
        return subprocessor().isToBeProcessed(statement);
    }

    public static void setValue(Object value) {
        LoggingInstrumenter.value = value;
    }

    public static void disable() {
        setValue(null);
    }

    public static Object getValue(Object def) {
        if (value == null) {
            return def;
        }
        return value;
    }

    @Override
    public void process(CtStatement statement) {
        String evaluationAccess = "runtimeAngelicValue";
        // create the angelic value
        String type = subprocessor.getType().getCanonicalName();
        CtLocalVariable<?> evaluation = newLocalVariableDeclaration(
                statement.getFactory(), subprocessor.getType(), evaluationAccess, "(" + type + ")"
                        + this.getClass().getCanonicalName() + ".getValue("
                        + subprocessor.getDefaultValue() + ")");
        // insert angelic value before the statement
        insertBeforeUnderSameParent(evaluation, statement);
        // collect values of the statement
        appendValueCollection(statement, evaluationAccess);
        // insert angelic value in the condition
        subprocessor.setValue("runtimeAngelicValue");
        subprocessor().process(statement);
    }

    public void appendValueCollection(CtStatement element, String outputName) {
        CollectableValueFinder finder;
        if (CtIf.class.isInstance(element)) {
            finder = CollectableValueFinder.valueFinderFromIf((CtIf) element);
        } else {
            finder = CollectableValueFinder.valueFinderFrom(element);
        }
        Collection<String> collectables = finder.reachableVariables();
        Multimap<String, String> getters = finder.accessibleGetters();
        collectables.remove(outputName);
        RuntimeValuesInstrumenter.runtimeCollectionBefore(element,
                MetaMap.autoMap(collectables), getters, outputName,
                runtimeValues());
    }

    private RuntimeValues<?> runtimeValues() {
        return runtimeValues;
    }

    private NopolProcessor subprocessor() {
        return subprocessor;
    }

    private final NopolProcessor subprocessor;
    private final RuntimeValues<T> runtimeValues;
}
