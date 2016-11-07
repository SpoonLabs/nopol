package fr.inria.lille.repair.common.synth;

import fr.inria.lille.repair.nopol.spoon.SpoonConditionalPredicate;
import fr.inria.lille.repair.nopol.spoon.SpoonStatementPredicate;
import fr.inria.lille.repair.nopol.spoon.symbolic.SpoonBooleanStatement;
import fr.inria.lille.repair.nopol.spoon.symbolic.SpoonDoubleStatement;
import fr.inria.lille.repair.nopol.spoon.symbolic.SpoonIntegerStatement;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import xxl.java.library.FileLibrary;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class StatementTypeDetector extends AbstractProcessor<CtStatement> {

	private CtStatement statement = null;
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
		this.answer = StatementType.NONE;
		this.typeToAnalyse = type;
	}

	/**
	 * @return the answer
	 */
	public StatementType getType() {
		return answer;
	}

	public CtStatement getStatement() {
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
		System.out.println(SpoonConditionalPredicate.INSTANCE.apply(element));
		System.out.println(SpoonStatementPredicate.INSTANCE.apply(element));
		if (typeToAnalyse == StatementType.PRE_THEN_COND &&
				SpoonConditionalPredicate.INSTANCE.apply(element) ||
				SpoonStatementPredicate.INSTANCE.apply(element)) {
			statement = element;
		} else if (typeToAnalyse == StatementType.CONDITIONAL
				&& SpoonConditionalPredicate.INSTANCE.apply(element)) {
			statement = element;
		} else if (typeToAnalyse == StatementType.PRECONDITION
				&& SpoonStatementPredicate.INSTANCE.apply(element)) {
			statement = element;
		} else if (typeToAnalyse == StatementType.INTEGER_LITERAL
				&& SpoonIntegerStatement.INSTANCE.apply(element)) {
			statement = element;
		} else if (typeToAnalyse == StatementType.INTEGER_LITERAL
				&& SpoonBooleanStatement.INSTANCE.apply(element)) {
			statement = element;
		} else if (typeToAnalyse == StatementType.DOUBLE_LITERAL
				&& SpoonDoubleStatement.INSTANCE.apply(element)) {
			statement = element;
		}
		if (statement != null)
			answer = typeToAnalyse;
	}

	@Override
	public String toString() {
		return statement.toString() + " : " + answer;
	}

}
