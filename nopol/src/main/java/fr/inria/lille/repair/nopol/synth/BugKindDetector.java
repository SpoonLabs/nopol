package fr.inria.lille.repair.nopol.synth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static fr.inria.lille.repair.nopol.synth.BugKind.CONDITIONAL;
import static fr.inria.lille.repair.nopol.synth.BugKind.NONE;
import static fr.inria.lille.repair.nopol.synth.BugKind.PRECONDITION;

import java.io.File;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import xxl.java.library.FileLibrary;
import fr.inria.lille.repair.nopol.spoon.SpoonConditionalPredicate;
import fr.inria.lille.repair.nopol.spoon.SpoonStatementPredicate;

final class BugKindDetector extends AbstractProcessor<CtElement> {
	
	private CtStatement statement;
	private BugKind answer;
	private final File file;
	private final int line;

	/**
	 * @param file
	 * @param line
	 */
	BugKindDetector(final File file, final int line) {
		checkArgument(line > 0, "Line should be greater than 0: %s", line);
		this.file = checkNotNull(file);
		this.line = line;
		answer = NONE;
	}

	/**
	 * @return the answer
	 */
	BugKind getType() {
		return answer;
	}

	CtStatement statement() {
		return statement;
	}
	
	/**
	 * @see spoon.processing.AbstractProcessor#isToBeProcessed(spoon.reflect.declaration.CtElement)
	 */
	@Override
	public boolean isToBeProcessed(final CtElement candidate) {
		SourcePosition position = candidate.getPosition();
		if (position == null){
			return false;
		}
		boolean isSameFile = FileLibrary.isSameFile(file, position.getFile());
		boolean isSameLine = position.getLine() == this.line;
		return isSameLine && isSameFile;
	}

	@Override
	public void process(final CtElement element) {
		if (SpoonConditionalPredicate.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = CONDITIONAL;
		} else if (SpoonStatementPredicate.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = PRECONDITION;
		}
	}
}
