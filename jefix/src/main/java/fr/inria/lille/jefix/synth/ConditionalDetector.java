package fr.inria.lille.jefix.synth;

import java.io.File;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

final class ConditionalDetector extends AbstractProcessor<CtExpression<Boolean>> {

	boolean answer;

	final File file;
	final int line;

	/**
	 * @param file
	 * @param line
	 */
	ConditionalDetector(final File file, final int line) {
		this.file = file;
		this.line = line;
	}

	/**
	 * @return the answer
	 */
	boolean isConditional() {
		return this.answer;
	}

	/**
	 * @see spoon.processing.AbstractProcessor#isToBeProcessed(spoon.reflect.declaration.CtElement)
	 */
	@Override
	public boolean isToBeProcessed(final CtExpression<Boolean> candidate) {
		SourcePosition position = candidate.getPosition();
		return position.getLine() == this.line
				&& position.getFile().getAbsolutePath().equals(this.file.getAbsolutePath());
	}

	@Override
	public void process(final CtExpression<Boolean> element) {
		CtElement parent = element.getParent();
		this.answer = this.answer || parent instanceof CtConditional || parent instanceof CtIf;
	}
}
