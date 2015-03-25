package fr.inria.lille.repair.nopol.spoon;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

import java.util.Collection;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.map.Multimap;
import fr.inria.lille.commons.spoon.collectable.CollectableValueFinder;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.RuntimeValuesInstrumenter;
import fr.inria.lille.repair.nopol.synth.AngelicExecution;

public final class ConditionalLoggingInstrumenter extends AbstractProcessor<CtStatement> {

	public ConditionalLoggingInstrumenter(RuntimeValues<Boolean> runtimeValues, ConditionalProcessor subprocessor) {
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

        CtLocalVariable<Boolean> defaultValue = newLocalVariableDeclaration(statement.getFactory(), Boolean.class, "spoonDefaultValue", "false");
        insertBeforeUnderSameParent(defaultValue, statement);
        CtCodeSnippetStatement defaultValueEvaluation = getFactory().Code().createCodeSnippetStatement("try{spoonDefaultValue=" + subprocessor().defaultCondition() + ";}catch(" + Exception.class.getCanonicalName()  +" e){}");
        insertBeforeUnderSameParent(defaultValueEvaluation, statement);

        String evaluationValue = angelicInvocation("spoonDefaultValue");
		CtLocalVariable<Boolean> evaluation = newLocalVariableDeclaration(statement.getFactory(), Boolean.class, evaluationAccess, evaluationValue);
		insertBeforeUnderSameParent(evaluation, statement);
		appendValueCollection(statement, evaluationAccess, "spoonDefaultValue");
		subprocessor().processCondition(statement, evaluationAccess);
	}
	
	protected String angelicInvocation(String booleanSnippet) {
		return AngelicExecution.invocation(booleanSnippet);
	}
	
	public void appendValueCollection(CtStatement element, String outputName, String...ignore) {
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
		RuntimeValuesInstrumenter.runtimeCollectionBefore(element,  MetaMap.autoMap(collectables), getters, outputName, runtimeValues());
	}
	
	private RuntimeValues<Boolean> runtimeValues() {
		return runtimeValues;
	}
	
	private ConditionalProcessor subprocessor() {
		return subprocessor;
	}
	
	private ConditionalProcessor subprocessor;
	private RuntimeValues<Boolean> runtimeValues;
}
