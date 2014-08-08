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
package fr.inria.lille.nopol.synth;

import static fr.inria.lille.nopol.synth.Synthesizer.NO_OP_SYNTHESIZER;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.Processor;
import fr.inria.lille.commons.spoon.SpoonClassLoaderBuilder;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.nopol.SourceLocation;
import fr.inria.lille.nopol.synth.conditional.ConditionalReplacer;
import fr.inria.lille.nopol.synth.conditional.SpoonConditionalPredicate;
import fr.inria.lille.nopol.synth.precondition.ConditionalAdder;
import fr.inria.lille.nopol.synth.precondition.SpoonStatementPredicate;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class SynthesizerFactory {

	private final File sourceFolder;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonClassLoaderBuilder spooner;
	private static int nbStatementsAnalysed = 0;
	public static RuntimeValues runtimeValues = RuntimeValues.newInstance();
	/**
	 * 
	 */
	public SynthesizerFactory(final File sourceFolder, final SpoonClassLoaderBuilder spooner) {
		this.sourceFolder = sourceFolder;
		this.spooner = spooner;
	}

	public Synthesizer getFor(final SourceLocation statement) {
		DelegatingProcessor processor;
		BugKind type = getType(statement);
		switch (type) {
		case CONDITIONAL:
			processor = new DelegatingProcessor(SpoonConditionalPredicate.INSTANCE,
					statement.getSourceFile(sourceFolder), statement.getLineNumber());
			processor.addProcessor((Processor) new ConditionalLoggingInstrumenter(runtimeValues));
			processor.addProcessor(new ConditionalReplacer(ConditionalValueHolder.VARIABLE_NAME));

			break;
		case PRECONDITION:
			processor = new DelegatingProcessor(SpoonStatementPredicate.INSTANCE,
					statement.getSourceFile(sourceFolder), statement.getLineNumber());
			processor.addProcessor((Processor) new ConditionalLoggingInstrumenter(runtimeValues));
			processor.addProcessor(new ConditionalAdder(ConditionalValueHolder.VARIABLE_NAME)); 
			logger.debug("No synthetizer found for {}, trying a precondition.", statement);
			break;
		default:
			logger.debug("No synthetizer found for {}.", statement);
			return NO_OP_SYNTHESIZER;
		}
		nbStatementsAnalysed++;
		ConstraintModelBuilder constraintModelBuilder = new ConstraintModelBuilder(sourceFolder, statement, processor, spooner, type);
		return new DefaultSynthesizer(constraintModelBuilder, statement, type, sourceFolder);
	}

	private BugKind getType(final SourceLocation rc) {
		BugKindDetector detector = new BugKindDetector(rc.getSourceFile(sourceFolder), rc.getLineNumber());
		spooner.processClass(rc.getRootClassName(), detector);
		return detector.getType();
	}
	
	public static int getNbStatementsAnalysed(){
		return nbStatementsAnalysed;
	}
}
