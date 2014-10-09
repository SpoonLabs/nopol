package fr.inria.lille.repair.nopol.spoon;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

import java.util.Collection;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import xxl.java.container.classic.MetaMap;
import xxl.java.support.Singleton;
import fr.inria.lille.commons.spoon.collectable.CollectableValueFinder;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.RuntimeValuesInstrumenter;
import fr.inria.lille.repair.nopol.synth.AngelicExecution;

public final class ConditionalLoggingInstrumenter extends AbstractProcessor<CtStatement> {

	public ConditionalLoggingInstrumenter(RuntimeValues<Boolean> runtimeValues, ConditionalProcessor subprocessor) {
		this.subprocessor = subprocessor;
		this.runtimeValues = runtimeValues;
		this.collectableFinder = Singleton.of(CollectableValueFinder.class);
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
		Collection<String> collectables = collectablesOf(element);
		collectables.remove(outputName);
		RuntimeValuesInstrumenter.runtimeCollectionBefore(element,  MetaMap.autoMap(collectables), outputName, runtimeValues());
	}
	
	private Collection<String> collectablesOf(CtStatement element) {
		Collection<String> collectables;
		if (CtIf.class.isInstance(element)) {
			collectables = collectableFinder().findFromIf((CtIf) element);
		} else {
			collectables = collectableFinder().findFromStatement(element);
		}
		return collectables;
	}
	
	private CollectableValueFinder collectableFinder() {
		return collectableFinder;
	}
	
	private RuntimeValues<Boolean> runtimeValues() {
		return runtimeValues;
	}
	
	private ConditionalProcessor subprocessor() {
		return subprocessor;
	}
	
	private ConditionalProcessor subprocessor;
	private RuntimeValues<Boolean> runtimeValues;
	private CollectableValueFinder collectableFinder;
}
