package fr.inria.lille.repair.nopol.spoon;

import fr.inria.lille.repair.common.config.NopolContext;
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
    private final NopolContext nopolContext;

    private List<NopolProcessor> nopolProcessors;

    public NopolProcessorBuilder(final File file, final int line,
                                 final NopolContext nopolContext) {
        checkArgument(line > 0, "Line should be greater than 0: %s", line);
        this.file = checkNotNull(file);
        this.line = line;
        this.nopolContext = nopolContext;
        this.nopolProcessors = new ArrayList<>();
    }

    public List<NopolProcessor> getNopolProcessors() {
        return nopolProcessors;
    }

    @Override
    public boolean isToBeProcessed(CtStatement candidate) {
        SourcePosition position = candidate.getPosition();
        if (position == null || position == SourcePosition.NOPOSITION) {
            return false;
        }
        boolean isSameFile = FileLibrary.isSameFile(file, position.getFile());
        boolean isSameLine = position.getLine() == this.line;
        return isSameLine && isSameFile && super.isToBeProcessed(candidate);
    }

    @Override
    public void process(CtStatement statement) {
        StatementType typeToAnalyse = nopolContext.getType();
        if (typeToAnalyse == StatementType.PRE_THEN_COND) {

            if (SpoonPredicate.canBeRepairedByAddingPrecondition(statement)) {
                if (nopolContext.getOracle() == NopolContext.NopolOracle.ANGELIC) {
                    nopolProcessors.add(new ConditionalAdder(statement));
                } else if (nopolContext.getOracle() == NopolContext.NopolOracle.SYMBOLIC) {
                    nopolProcessors.add(new SymbolicConditionalAdder(statement));
                }
            }

            if (SpoonPredicate.canBeRepairedByChangingCondition(statement)) {
                if (nopolContext.getOracle() == NopolContext.NopolOracle.ANGELIC) {
                    nopolProcessors.add(new ConditionalReplacer(statement));
                } else if (nopolContext.getOracle() == NopolContext.NopolOracle.SYMBOLIC) {
                    nopolProcessors.add(new SymbolicConditionalReplacer(statement));
                }
            }

        } else if (typeToAnalyse == StatementType.CONDITIONAL
                && SpoonPredicate.canBeRepairedByChangingCondition(statement)) {
            if (nopolContext.getOracle() == NopolContext.NopolOracle.ANGELIC) {
                nopolProcessors.add(new ConditionalReplacer(statement));
            } else if (nopolContext.getOracle() == NopolContext.NopolOracle.SYMBOLIC) {
                nopolProcessors.add(new SymbolicConditionalReplacer(statement));
            }
        } else if (typeToAnalyse == StatementType.PRECONDITION
                && SpoonPredicate.canBeRepairedByAddingPrecondition(statement)) {
            if (nopolContext.getOracle() == NopolContext.NopolOracle.ANGELIC) {
                nopolProcessors.add(new ConditionalAdder(statement));
            } else if (nopolContext.getOracle() == NopolContext.NopolOracle.SYMBOLIC) {
                nopolProcessors.add(new SymbolicConditionalAdder(statement));
            }

        } else if (typeToAnalyse == StatementType.INTEGER_LITERAL &&
                SpoonIntegerStatement.INSTANCE.apply(statement) ||
                typeToAnalyse == StatementType.INTEGER_LITERAL &&
                        SpoonBooleanStatement.INSTANCE.apply(statement) ||
                typeToAnalyse == StatementType.DOUBLE_LITERAL
                        && SpoonDoubleStatement.INSTANCE.apply(statement)) {
            if (nopolContext.getOracle() == NopolContext.NopolOracle.SYMBOLIC) {
                if (nopolContext.getOracle() == NopolContext.NopolOracle.SYMBOLIC) {
                    nopolProcessors.add(new LiteralReplacer(typeToAnalyse.getType(), statement, typeToAnalyse));
                }
            }
        }
    }
}
