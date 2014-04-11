package fr.inria.lille.nopol.synth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static fr.inria.lille.nopol.synth.BugKind.CONDITIONAL;
import static fr.inria.lille.nopol.synth.BugKind.NONE;
import static fr.inria.lille.nopol.synth.BugKind.PRECONDITION;

import java.io.File;

import spoon.processing.AbstractProcessor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import fr.inria.lille.nopol.synth.conditional.SpoonConditionalPredicate;
import fr.inria.lille.nopol.synth.precondition.SpoonStatementPredicate;

final class BugKindDetector extends AbstractProcessor<CtElement> {
	
	private BugKind answer = NONE;
	private final String absolutePath;
	private final int line;

	/**
	 * @param file
	 * @param line
	 */
	BugKindDetector(final File file, final int line) {
		absolutePath = checkNotNull(file).getAbsolutePath();
		checkArgument(line > 0, "Line should be greater than 0: %s", line);
		this.line = line;
	}

	/**
	 * @return the answer
	 */
	BugKind getType() {
		return answer;
	}

	/**
	 * @see spoon.processing.AbstractProcessor#isToBeProcessed(spoon.reflect.declaration.CtElement)
	 */
	@Override
	public boolean isToBeProcessed(final CtElement candidate) {
		if ( candidate.getPosition() == null )
			return false;
		
		SourcePosition position = candidate.getPosition();
		return position.getLine() == line
					&& position.getFile().getAbsolutePath().equals(absolutePath);

	}

	@Override
	public void process(final CtElement element) {
		if (SpoonConditionalPredicate.INSTANCE.apply(element)) {
			answer = CONDITIONAL;
		} else if (SpoonStatementPredicate.INSTANCE.apply(element)) {
			answer = PRECONDITION;
		}
	}
}
