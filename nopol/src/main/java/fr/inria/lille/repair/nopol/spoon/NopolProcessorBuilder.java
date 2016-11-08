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
	public void process(CtStatement element) {
		nopolProcessors = new ArrayList<>();
		StatementType typeToAnalyse = config.getType();
		if (typeToAnalyse == StatementType.PRE_THEN_COND) {
			if (SpoonConditionalPredicate.INSTANCE.apply(element)) {
				if (config.getOracle() == Config.NopolOracle.ANGELIC) {
					nopolProcessors.add(new ConditionalReplacer(element));
				} else if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
					nopolProcessors.add(new SymbolicConditionalReplacer(element));
				}
			} else if (SpoonStatementPredicate.INSTANCE.apply(element)) {
				if (config.getOracle() == Config.NopolOracle.ANGELIC) {
					nopolProcessors.add(new ConditionalAdder(element));
				} else if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
					nopolProcessors.add(new SymbolicConditionalAdder(element));
				}
			}
		} else if (typeToAnalyse == StatementType.CONDITIONAL
				&& SpoonConditionalPredicate.INSTANCE.apply(element)) {
			if (config.getOracle() == Config.NopolOracle.ANGELIC) {
				nopolProcessors.add(new ConditionalReplacer(element));
			} else if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
				nopolProcessors.add(new SymbolicConditionalReplacer(element));
			}
		} else if (typeToAnalyse == StatementType.PRECONDITION
				&& SpoonStatementPredicate.INSTANCE.apply(element)) {
			if (config.getOracle() == Config.NopolOracle.ANGELIC) {
				nopolProcessors.add(new ConditionalAdder(element));
			} else if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
				nopolProcessors.add(new SymbolicConditionalAdder(element));
			}
		} else if (typeToAnalyse == StatementType.INTEGER_LITERAL &&
				SpoonIntegerStatement.INSTANCE.apply(element) ||
				typeToAnalyse == StatementType.INTEGER_LITERAL &&
						SpoonBooleanStatement.INSTANCE.apply(element) ||
				typeToAnalyse == StatementType.DOUBLE_LITERAL
						&& SpoonDoubleStatement.INSTANCE.apply(element)) {
			if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
				final LiteralReplacer nopolProcessor = new LiteralReplacer(typeToAnalyse.getType(), element);
				nopolProcessors.add(nopolProcessor);
			}
		}
	}
}
