package fr.inria.lille.nopol.synth.conditional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;

/**
 * @author Favio D. DeMarco
 */
public final class ConditionalReplacer extends AbstractProcessor<CtCodeElement> {

	private final String value;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param file
	 * @param line
	 */
	public ConditionalReplacer(final String value) {
		this.value = value;
	}

	private CtExpression<Boolean> getCondition(CtCodeElement element) {
		CtExpression<Boolean> condition;
		if (element instanceof CtIf) {
			condition = ((CtIf) element).getCondition();
		} else if (element instanceof CtConditional) {
			condition = ((CtConditional<?>) element).getCondition();
		} else {
			throw new IllegalStateException("Unknown conditional class: " + element.getClass());
		}
		return condition;
	}

	@Override
	public void process(CtCodeElement element) {
		logger.debug("Replacing:\n{}", element);
		// we declare a new snippet of code to be inserted
		CtCodeSnippetExpression<Boolean> snippet = element.getFactory().Core().createCodeSnippetExpression();
		snippet.setValue(value);
		CtExpression<Boolean> condition = getCondition(element);
		condition.replace(snippet);
	}

	
}
