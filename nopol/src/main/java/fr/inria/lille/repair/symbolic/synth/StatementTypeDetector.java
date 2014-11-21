package fr.inria.lille.repair.symbolic.synth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import fr.inria.lille.repair.nopol.spoon.SpoonConditionalPredicate;
import fr.inria.lille.repair.nopol.spoon.SpoonStatementPredicate;
import fr.inria.lille.repair.symbolic.spoon.SpoonBooleanStatement;
import fr.inria.lille.repair.symbolic.spoon.SpoonIntegerStatement;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import xxl.java.library.FileLibrary;

public class StatementTypeDetector extends AbstractProcessor<CtElement> {
	
	private CtStatement statement;
	private StatementType answer;
	private final File file;
	private final int line;
	
	/**
	 * @param file
	 * @param line
	 */
	StatementTypeDetector(final File file, final int line) {
		checkArgument(line > 0, "Line should be greater than 0: %s", line);
		this.file = checkNotNull(file);
		this.line = line;
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
			answer = StatementType.CONDITIONAL;
		} else if (SpoonStatementPredicate.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.PRECONDITION;
		} else if (SpoonIntegerStatement.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.INTEGER_LITERAL;
		} else if (SpoonBooleanStatement.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.BOOLEAN_LITERAL;
		}
	}
	
	@Override
	public String toString() {
		return statement.toString() + " : " + answer;
	}

}
