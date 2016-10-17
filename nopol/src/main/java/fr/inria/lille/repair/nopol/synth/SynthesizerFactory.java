/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.repair.nopol.synth;

import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.synth.StatementTypeDetector;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.spoon.ConditionalLoggingInstrumenter;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.nopol.spoon.smt.ConditionalAdder;
import fr.inria.lille.repair.nopol.spoon.smt.ConditionalReplacer;
import fr.inria.lille.repair.nopol.spoon.symbolic.LiteralReplacer;
import fr.inria.lille.repair.nopol.spoon.symbolic.SymbolicConditionalAdder;
import fr.inria.lille.repair.nopol.spoon.symbolic.SymbolicConditionalReplacer;
import fr.inria.lille.repair.nopol.synth.dynamoth.DynamothSynthesizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.Processor;
import spoon.reflect.code.CtStatement;

import java.io.File;

import static fr.inria.lille.repair.nopol.synth.Synthesizer.NO_OP_SYNTHESIZER;

/**
 * @author Favio D. DeMarco
 */
public final class SynthesizerFactory {

    private final File[] sourceFolders;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SpoonedProject spooner;
    private final Config config;
    private static int nbStatementsAnalysed = 0;
    private static RuntimeValues<Boolean> runtimeValuesInstance = RuntimeValues.newInstance();//TODO remove this unused field

	/**
	 *
	 * @param sourceFolders
	 * @param spooner
	 * @param config
	 */
    public SynthesizerFactory(final File[] sourceFolders, final SpoonedProject spooner, Config config) {
        this.sourceFolders = sourceFolders;
        this.spooner = spooner;
        this.config = config;
    }

    public Synthesizer getFor(final SourceLocation statement)
	{
        nbStatementsAnalysed++;
        SpoonedClass spoonCl = spooner.forked(statement.getRootClassName());
        if (spoonCl == null) {
            return NO_OP_SYNTHESIZER;
        }
        if (spoonCl.getSimpleType() == null) {
            return NO_OP_SYNTHESIZER;
        }
        StatementTypeDetector detector = new StatementTypeDetector(spoonCl.getSimpleType().getPosition().getFile(), statement.getLineNumber(), config.getType());
        spoonCl.process(detector);

        NopolProcessor nopolProcessor;
        switch (detector.getType()) {
            case CONDITIONAL:
                switch (config.getOracle()) {
                    case ANGELIC:
                        nopolProcessor = new ConditionalReplacer(detector.statement());
                        break;
                    case SYMBOLIC:
                        nopolProcessor = new SymbolicConditionalReplacer(detector.statement());
                        break;
                    default:
                        return NO_OP_SYNTHESIZER;
                }
                break;
            case PRECONDITION:
                switch (config.getOracle()) {
                    case ANGELIC:
                        nopolProcessor = new ConditionalAdder(detector.statement());
                        break;
                    case SYMBOLIC:
                        nopolProcessor = new SymbolicConditionalAdder(detector.statement());
                        break;
                    default:
                        return NO_OP_SYNTHESIZER;
                }
                break;
            case INTEGER_LITERAL:
            case BOOLEAN_LITERAL:
            case DOUBLE_LITERAL:
                switch (config.getOracle()) {
                    case SYMBOLIC:
                        nopolProcessor = new LiteralReplacer(detector.getType().getType(), detector.statement());
                        break;
                    default:
                        return NO_OP_SYNTHESIZER;
                }
                break;
            default:
                logger.debug("No synthesizer found for {}.", statement);
                return NO_OP_SYNTHESIZER;
        }
        AngelicValue constraintModelBuilder = null;
        if (Boolean.class.equals(detector.getType().getType())) {
            RuntimeValues<Boolean> runtimeValuesInstance = RuntimeValues.newInstance();
            switch (config.getOracle()) {
                case ANGELIC:
                    Processor<CtStatement> processor = new ConditionalLoggingInstrumenter(runtimeValuesInstance, nopolProcessor);
                    constraintModelBuilder = new ConstraintModelBuilder(runtimeValuesInstance, statement, processor, spooner, config);
                    break;
                case SYMBOLIC:
                    constraintModelBuilder = new JPFRunner<>(runtimeValuesInstance, statement, nopolProcessor, spoonCl, spooner, config);
                    break;
                default:
                    return NO_OP_SYNTHESIZER;
            }
        }
        if (Integer.class.equals(detector.getType().getType())) {
            RuntimeValues<Integer> runtimeValuesInstance = RuntimeValues.newInstance();
            constraintModelBuilder = new JPFRunner<>(runtimeValuesInstance, statement, nopolProcessor, spoonCl, spooner, config);
            return new SMTNopolSynthesizer<>(spooner, constraintModelBuilder, statement, detector.getType(), nopolProcessor, config);
        }
        if (Double.class.equals(detector.getType().getType())) {
            RuntimeValues<Double> runtimeValuesInstance = RuntimeValues.newInstance();
            constraintModelBuilder = new JPFRunner<>(runtimeValuesInstance, statement, nopolProcessor, spoonCl, spooner, config);
            return new SMTNopolSynthesizer<>(spooner, constraintModelBuilder, statement, detector.getType(), nopolProcessor, config);
        }
        switch (config.getSynthesis()) {
            case SMT:
                return new SMTNopolSynthesizer(spooner, constraintModelBuilder, statement, detector.getType(), nopolProcessor, config);
            case DYNAMOTH:
                return new DynamothSynthesizer(constraintModelBuilder, sourceFolders, statement, detector.getType(), nopolProcessor, spooner, config);
        }
        return Synthesizer.NO_OP_SYNTHESIZER;
    }

    public static int getNbStatementsAnalysed() {
        return nbStatementsAnalysed;
    }
}
