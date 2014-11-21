package fr.inria.lille.repair.symbolic.spoon;

import gov.nasa.jpf.symbc.Debug;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

public class ConditionalReplacer extends SymbolicProcessor {

	public ConditionalReplacer(CtStatement target) {
		super(target);
		super.defaultValue = ((CtIf) target).getCondition().toString();
	}

	public static CtExpression<Boolean> getCondition(CtElement element) {
		CtExpression<Boolean> condition;
		if (element instanceof CtIf) {
			condition = ((CtIf) element).getCondition();
		} else if (element instanceof CtConditional) {
			condition = ((CtConditional<?>) element).getCondition();
		} else {
			throw new IllegalStateException("Unknown conditional class: "
					+ element.getClass());
		}
		return condition;
	}

	@Override
	public void process(CtStatement element) {
		String oldStatement = element.toString();
		CtCodeSnippetExpression<Boolean> snippet = null;
		if (getValue() != null) {
			if(getValue().equals("1")) {
				snippet = element.getFactory().Code()
					.createCodeSnippetExpression("true");
			} else if(getValue().equals("0")) {
				snippet = element.getFactory().Code()
						.createCodeSnippetExpression("false");
			} else {
				snippet = element.getFactory().Code()
						.createCodeSnippetExpression(getValue());
			}
		} else {
			snippet = element
					.getFactory()
					.Code()
					.createCodeSnippetExpression(
							Debug.class.getCanonicalName()
									+ ".makeSymbolicBoolean(\"guess_fix\")");
		}
		CtExpression<Boolean> condition = getCondition(element);

		condition.replace(snippet);
		logger.debug("Replacing:\n{}\nby:\n{}", oldStatement, element);
	}

}
