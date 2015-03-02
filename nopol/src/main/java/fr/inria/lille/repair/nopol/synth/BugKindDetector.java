package fr.inria.lille.repair.nopol.synth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import xxl.java.library.FileLibrary;
import fr.inria.lille.repair.nopol.spoon.SpoonConditionalPredicate;
import fr.inria.lille.repair.nopol.spoon.SpoonStatementPredicate;
import fr.inria.lille.repair.symbolic.synth.StatementType;

final class BugKindDetector extends AbstractProcessor<CtElement> {

	private CtStatement statement;
	private StatementType answer;
	private final File file;
	private final int line;
	private StatementType type;

	/**
	 * @param file
	 * @param line
	 * @param type
	 */
	BugKindDetector(final File file, final int line, StatementType type) {
		checkArgument(line > 0, "Line should be greater than 0: %s", line);
		this.file = checkNotNull(file);
		this.line = line;
		this.type = type;
		answer = StatementType.NONE;
	}

	/**
	 * @return the answer
	 */
	StatementType getType() {
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
		if (position == null) {
			return false;
		}
		boolean isSameFile = FileLibrary.isSameFile(file, position.getFile());
		boolean isSameLine = position.getLine() == this.line;
		return isSameLine && isSameFile;
	}

	@Override
	public void process(final CtElement element) {
		if (type == StatementType.CONDITIONAL && SpoonConditionalPredicate.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.CONDITIONAL;
		} else if (type == StatementType.PRECONDITION && SpoonStatementPredicate.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.PRECONDITION;
		}
	}
}
