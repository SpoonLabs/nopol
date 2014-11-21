package fr.inria.lille.repair.nopol.spoon;

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtLocalVariableReference;

public class IntialisationAdder extends InitialisationProcessor {

	public IntialisationAdder(CtStatement target) {
		super(target, "guess_fix");
	}

	@Override
	public CtElement processInitialisation(CtStatement element, String newVlalue) {
		logger.debug("##### {} ##### Before:\n{}", element, element.getParent());
		CtElement parent = element.getParent();
		CtLocalVariableReference<Object> newIf = element.getFactory().Core().createLocalVariableReference();
		/*CtCodeSnippetExpression<Boolean> condition = element.getFactory()
				.Core().createCodeSnippetExpression();
		condition.setValue(newVlalue);
		newIf.setCondition(condition);
		// Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2]
		// != [CtElem3] )
		newIf.setParent(parent);
		element.replace(newIf);
		// this should be after the replace to avoid an StackOverflowException
		// caused by the circular reference.
		// see SpoonStatementPredicate
		newIf.setThenStatement((CtStatement) element);
		// Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2]
		// != [CtElem3] )
		newIf.getThenStatement().setParent(newIf);
		logger.debug("##### {} ##### After:\n{}", element, element.getParent()
				.getParent());*/
		return null;
	}
}
