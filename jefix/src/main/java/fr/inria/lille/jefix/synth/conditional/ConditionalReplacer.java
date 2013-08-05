package fr.inria.lille.jefix.synth.conditional;

import java.io.File;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.cu.SourcePosition;
import spoon.support.reflect.code.CtCodeSnippetExpressionImpl;

/**
 * @author Favio D. DeMarco
 */
public final class ConditionalReplacer extends AbstractProcessor<CtCodeElement> {

	private final File file;
	private final int line;
	private final String value;

	/**
	 * @param file
	 * @param line
	 */
	public ConditionalReplacer(final File file, final int line, final String value) {
		this.file = file;
		this.line = line;
		this.value = value;
	}

	/**
	 * @param element
	 * @return
	 */
	private CtExpression<Boolean> getCondition(final CtCodeElement element) {
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

	/**
	 * @see spoon.processing.AbstractProcessor#isToBeProcessed(spoon.reflect.declaration.CtElement)
	 */
	@Override
	public boolean isToBeProcessed(final CtCodeElement candidate) {
		SourcePosition position = candidate.getPosition();
		boolean isConditional = candidate instanceof CtIf || candidate instanceof CtConditional;
		return isConditional && position.getLine() == this.line
				&& position.getFile().getAbsolutePath().equals(this.file.getAbsolutePath());
	}

	@Override
	public void process(final CtCodeElement element) {
		// we declare a new snippet of code to be inserted
		CtCodeSnippetExpression<Boolean> snippet = new CtCodeSnippetExpressionImpl<>();
		snippet.setFactory(this.getFactory());
		snippet.setValue(this.value);
		CtExpression<Boolean> condition = this.getCondition(element);
		condition.replace(snippet);
	}
}
