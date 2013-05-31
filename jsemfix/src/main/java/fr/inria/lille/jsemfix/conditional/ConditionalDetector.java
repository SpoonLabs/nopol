package fr.inria.lille.jsemfix.conditional;

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

	@Override
	public void process(final CtExpression<Boolean> element) {
		SourcePosition position = element.getPosition();
		if (position.getLine() == this.line && position.getFile().equals(this.file)) {
			CtElement parent = element.getParent();
			if (parent instanceof CtConditional || parent instanceof CtIf) {
				this.answer = true;
			}
		}
	}
}
