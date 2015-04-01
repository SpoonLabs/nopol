package fr.inria.lille.repair.nopol.spoon.brutpol;

import fr.inria.lille.repair.nopol.spoon.ConditionalProcessor;
import fr.inria.lille.repair.nopol.synth.AngelicExecution;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

public final class ConditionalInstrumenter extends AbstractProcessor<CtStatement> {

	public ConditionalInstrumenter(ConditionalProcessor subprocessor) {
		this.subprocessor = subprocessor;
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
		subprocessor().processCondition(statement, evaluationAccess);
	}
	
	protected String angelicInvocation(String booleanSnippet) {
		return AngelicExecution.invocation(booleanSnippet);
	}
	
	private ConditionalProcessor subprocessor() {
		return subprocessor;
	}
	
	private ConditionalProcessor subprocessor;
}
