package fr.inria.lille.repair.nopol.spoon;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

import java.util.Collection;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
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
		String evaluationValue = angelicInvocation(subprocessor().defaultCondition());
		CtLocalVariable<Boolean> evaluation = newLocalVariableDeclaration(statement.getFactory(), boolean.class, evaluationAccess, evaluationValue);
		insertBeforeUnderSameParent(evaluation, statement);
		appendValueCollection(statement, evaluationAccess);
		subprocessor().processCondition(statement, evaluationAccess);
	}
	
	protected String angelicInvocation(String booleanSnippet) {
		return AngelicExecution.invocation(booleanSnippet);
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
