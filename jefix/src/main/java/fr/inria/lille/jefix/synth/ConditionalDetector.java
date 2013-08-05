package fr.inria.lille.jefix.synth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
		this.file = checkNotNull(file);
		checkArgument(line > 0, "Line should be greater than 0: %s", line);
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

		// XXX
		// position can be null. Spoon bug? WTF!? WTF!? WTF!?
		SourcePosition position = this.tryToGetAPosition(candidate);

		return /* WTF!? */position != null && /* WTF!? */position.getLine() == this.line
				&& position.getFile().getAbsolutePath().equals(this.file.getAbsolutePath());
	}

	@Override
	public void process(final CtExpression<Boolean> element) {
		CtElement parent = element.getParent();
		this.answer = this.answer || parent instanceof CtConditional || parent instanceof CtIf;
	}

	/**
	 * XXX Position can be null... Spoon bug? WTF!? WTF!? WTF!?
	 */
	private SourcePosition tryToGetAPosition(final CtExpression<Boolean> candidate) {
		SourcePosition position = candidate.getPosition();
		CtElement parent = candidate.getParent();
		while (null == position && parent != null) {
			position = parent.getPosition();
			parent = parent.getParent();
		}
		return position;
	}
}
