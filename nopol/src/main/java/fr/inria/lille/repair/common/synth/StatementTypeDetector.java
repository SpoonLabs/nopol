package fr.inria.lille.repair.common.synth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import xxl.java.library.FileLibrary;
import fr.inria.lille.repair.nopol.spoon.SpoonConditionalPredicate;
import fr.inria.lille.repair.nopol.spoon.SpoonStatementPredicate;
import fr.inria.lille.repair.nopol.spoon.symbolic.SpoonBooleanStatement;
import fr.inria.lille.repair.nopol.spoon.symbolic.SpoonDoubleStatement;
import fr.inria.lille.repair.nopol.spoon.symbolic.SpoonIntegerStatement;

public class StatementTypeDetector extends AbstractProcessor<CtStatement> {

	private CtStatement statement;
	private StatementType answer;
	private final File file;
	private final int line;
	private StatementType typeToAnalyse;

	/**
	 * @param file
	 * @param line
	 */
	public StatementTypeDetector(final File file, final int line,
			final StatementType type) {
		checkArgument(line > 0, "Line should be greater than 0: %s", line);
		this.file = checkNotNull(file);
		this.line = line;
		answer = StatementType.NONE;
		this.typeToAnalyse = type;
	}

	/**
	 * @return the answer
	 */
	public StatementType getType() {
		return answer;
	}

    public CtStatement statement() {
		return statement;
	}

	/**
	 * @see spoon.processing.AbstractProcessor#isToBeProcessed(spoon.reflect.declaration.CtElement)
	 */
	@Override
	public boolean isToBeProcessed(final CtStatement candidate) {
		SourcePosition position = candidate.getPosition();
		if (position == null) {
			return false;
		}
		boolean isSameFile = FileLibrary.isSameFile(file, position.getFile());
		boolean isSameLine = position.getLine() == this.line;
		return isSameLine && isSameFile;
	}

	@Override
	public void process(final CtStatement element) {
		if (typeToAnalyse == StatementType.CONDITIONAL
				&& SpoonConditionalPredicate.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.CONDITIONAL;
		} else if (typeToAnalyse == StatementType.PRECONDITION
				&& SpoonStatementPredicate.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.PRECONDITION;
		} else if (typeToAnalyse == StatementType.INTEGER_LITERAL
				&& SpoonIntegerStatement.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.INTEGER_LITERAL;
		} else if (typeToAnalyse == StatementType.INTEGER_LITERAL
				&& SpoonBooleanStatement.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.BOOLEAN_LITERAL;
		} else if (typeToAnalyse == StatementType.DOUBLE_LITERAL
				&& SpoonDoubleStatement.INSTANCE.apply(element)) {
			statement = (CtStatement) element;
			answer = StatementType.DOUBLE_LITERAL;
		}
	}

	@Override
	public String toString() {
		return statement.toString() + " : " + answer;
	}

}
