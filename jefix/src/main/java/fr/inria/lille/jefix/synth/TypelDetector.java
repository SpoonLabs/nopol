package fr.inria.lille.jefix.synth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static fr.inria.lille.jefix.synth.Type.CONDITIONAL;
import static fr.inria.lille.jefix.synth.Type.NONE;
import static fr.inria.lille.jefix.synth.Type.PRECONDITION;

import java.io.File;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.jefix.synth.conditional.SpoonConditionalPredicate;
import fr.inria.lille.jefix.synth.precondition.SpoonStatementPredicate;

final class TypelDetector extends AbstractProcessor<CtCodeElement> {

	private Type answer = NONE;

	private final String absolutePath;
	private final int line;

	/**
	 * @param file
	 * @param line
	 */
	TypelDetector(final File file, final int line) {
		absolutePath = checkNotNull(file).getAbsolutePath();
		checkArgument(line > 0, "Line should be greater than 0: %s", line);
		this.line = line;
	}

	/**
	 * @return the answer
	 */
	Type getType() {
		return answer;
	}

	/**
	 * @see spoon.processing.AbstractProcessor#isToBeProcessed(spoon.reflect.declaration.CtElement)
	 */
	@Override
	public boolean isToBeProcessed(final CtCodeElement candidate) {
		SourcePosition position = candidate.getPosition();
		return answer == NONE && position != null && position.getLine() == line
				&& position.getFile().getAbsolutePath().equals(absolutePath);
	}

	@Override
	public void process(final CtCodeElement element) {
		if (SpoonConditionalPredicate.INSTANCE.apply(element)) {
			answer = CONDITIONAL;
		} else if (SpoonStatementPredicate.INSTANCE.apply(element)) {
			answer = PRECONDITION;
		}
	}
}
