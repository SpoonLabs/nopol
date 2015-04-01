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

import static fr.inria.lille.repair.nopol.synth.Synthesizer.NO_OP_SYNTHESIZER;

import java.io.File;

import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.synth.StatementTypeDetector;
import fr.inria.lille.repair.nopol.synth.brutpol.BrutSynthesizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.Processor;
import spoon.reflect.code.CtStatement;
import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.spoon.ConditionalAdder;
import fr.inria.lille.repair.nopol.spoon.ConditionalLoggingInstrumenter;
import fr.inria.lille.repair.nopol.spoon.ConditionalProcessor;
import fr.inria.lille.repair.nopol.spoon.ConditionalReplacer;
import fr.inria.lille.repair.common.synth.StatementType;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class SynthesizerFactory {

	private final File sourceFolder;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonedProject spooner;
	private StatementType type;
	private static int nbStatementsAnalysed = 0;
	private static RuntimeValues<Boolean> runtimeValuesInstance = RuntimeValues.newInstance();
	
	/**
	 * @param type 
	 * 
	 */
	public SynthesizerFactory(final File sourceFolder, final SpoonedProject spooner, StatementType type) {
		this.sourceFolder = sourceFolder;
		this.spooner = spooner;
		this.type = type;
	}

	public Synthesizer getFor(final SourceLocation statement) {
		nbStatementsAnalysed++;
		ConditionalProcessor conditional;
		RuntimeValues<Boolean> runtimeValues = runtimeValuesInstance;
		SpoonedClass spoonCl = spooner.forked(statement.getRootClassName());

        StatementTypeDetector detector = new StatementTypeDetector(spoonCl.getSimpleType().getPosition().getFile(), statement.getLineNumber(), type);
		spoonCl.process(detector);

		switch (detector.getType()) {
			case CONDITIONAL:
				conditional = new ConditionalReplacer(detector.statement());
				break;
			case PRECONDITION:
				conditional = new ConditionalAdder(detector.statement());
				break;
			default:
				logger.debug("No synthesizer found for {}.", statement);
				return NO_OP_SYNTHESIZER;
		}
		switch (Config.INSTANCE.getSynthesis()) {
			case SMT:
				Processor<CtStatement> processor = new ConditionalLoggingInstrumenter(runtimeValuesInstance, conditional);
				ConstraintModelBuilder constraintModelBuilder = new ConstraintModelBuilder(sourceFolder, runtimeValues, statement, processor, spooner);
				return new DefaultSynthesizer(constraintModelBuilder, statement, detector.getType(), sourceFolder, conditional);
			case BRUTPOL:
				return new BrutSynthesizer(sourceFolder, statement, detector.getType(), conditional, spooner);
		}
		throw new RuntimeException("Unknown synthesizer");
	}

	public static int getNbStatementsAnalysed(){
		return nbStatementsAnalysed;
	}
}
