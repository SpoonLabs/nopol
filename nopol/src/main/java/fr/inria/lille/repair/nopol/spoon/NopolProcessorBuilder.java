package fr.inria.lille.repair.nopol.spoon;

import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.spoon.smt.ConditionalAdder;
import fr.inria.lille.repair.nopol.spoon.smt.ConditionalReplacer;
import fr.inria.lille.repair.nopol.spoon.symbolic.*;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import xxl.java.library.FileLibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by bdanglot on 11/7/16.
 */
public class NopolProcessorBuilder extends AbstractProcessor<CtStatement> {

	private final File file;
	private final int line;
	private final Config config;

	private List<NopolProcessor> nopolProcessors;

	public NopolProcessorBuilder(final File file, final int line,
								 final Config config) {
		checkArgument(line > 0, "Line should be greater than 0: %s", line);
		this.file = checkNotNull(file);
		this.line = line;
		this.config = config;
		this.nopolProcessors = new ArrayList<>();
	}

	public List<NopolProcessor> getNopolProcessors() {
		return nopolProcessors;
	}

	@Override
	public boolean isToBeProcessed(CtStatement candidate) {
		SourcePosition position = candidate.getPosition();
		if (position == null) {
			return false;
		}
		boolean isSameFile = FileLibrary.isSameFile(file, position.getFile());
		boolean isSameLine = position.getLine() == this.line;
		return isSameLine && isSameFile && super.isToBeProcessed(candidate);
	}

	@Override
	public void process(CtStatement statement) {
		StatementType typeToAnalyse = config.getType();
		if (typeToAnalyse == StatementType.PRE_THEN_COND) {
			preThenCondProcessors(statement);
		} else if (typeToAnalyse == StatementType.CONDITIONAL
				&& SpoonConditionalPredicate.INSTANCE.apply(statement)) {
			nopolProcessors.add(conditionalProcessor(statement));
		} else if (typeToAnalyse == StatementType.PRECONDITION
				&& SpoonStatementPredicate.INSTANCE.apply(statement)) {
			nopolProcessors.add(preConditionProcessor(statement));
		} else if (typeToAnalyse == StatementType.INTEGER_LITERAL &&
				SpoonIntegerStatement.INSTANCE.apply(statement) ||
				typeToAnalyse == StatementType.INTEGER_LITERAL &&
						SpoonBooleanStatement.INSTANCE.apply(statement) ||
				typeToAnalyse == StatementType.DOUBLE_LITERAL
						&& SpoonDoubleStatement.INSTANCE.apply(statement)) {
			nopolProcessors.add(literalProcessor(statement, typeToAnalyse));
		}
	}

	private NopolProcessor literalProcessor(CtStatement statement, StatementType typeToAnalyse) {
		if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
			return new LiteralReplacer(typeToAnalyse.getType(), statement, typeToAnalyse);
		}
		throw new UnsupportedOperationException("Wrong oracle for Literal replace : " + config.getOracle() + "should be symbolic");
	}

	private NopolProcessor preConditionProcessor(CtStatement statement) {
		if (config.getOracle() == Config.NopolOracle.ANGELIC) {
			return new ConditionalAdder(statement);
		} else if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
			return new SymbolicConditionalAdder(statement);
		}
		return null;
	}

	private NopolProcessor conditionalProcessor(CtStatement statement) {
		if (config.getOracle() == Config.NopolOracle.ANGELIC) {
			return new ConditionalReplacer(statement);
		} else if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
			return new SymbolicConditionalReplacer(statement);
		}
		return null;
	}

	private void preThenCondProcessors(CtStatement statement) {
		NopolProcessor nopolProcessor = null;
		if (SpoonConditionalPredicate.INSTANCE.apply(statement)) {
			if (config.getOracle() == Config.NopolOracle.ANGELIC) {
				nopolProcessor = new ConditionalReplacer(statement);
			} else if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
				nopolProcessor = new SymbolicConditionalReplacer(statement);
			}
			nopolProcessors.add(nopolProcessor);
		}

		if (SpoonStatementPredicate.INSTANCE.apply(statement)) {
			if (config.getOracle() == Config.NopolOracle.ANGELIC) {
				nopolProcessor = new ConditionalAdder(statement);
			} else if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
				nopolProcessor = new SymbolicConditionalAdder(statement);
			}
			nopolProcessors.add(nopolProcessor);
		}
	}
}
