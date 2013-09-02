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
package fr.inria.lille.jefix.synth;

import static fr.inria.lille.jefix.synth.Synthesizer.NO_OP_SYNTHESIZER;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.Builder;
import spoon.processing.ProcessingManager;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;
import fr.inria.lille.jefix.SourceLocation;
import fr.inria.lille.jefix.synth.conditional.ConditionalReplacer;
import fr.inria.lille.jefix.synth.conditional.SpoonConditionalPredicate;
import fr.inria.lille.jefix.synth.precondition.ConditionalAdder;
import fr.inria.lille.jefix.synth.precondition.SpoonStatementPredicate;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class SynthesizerFactory {

	private final File sourceFolder;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final boolean debug = logger.isDebugEnabled();

	/**
	 * 
	 */
	public SynthesizerFactory(final File sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	public Synthesizer getFor(final SourceLocation statement) {
		DelegatingProcessor processor;

		Type type = getType(statement);
		switch (type) {
		case CONDITIONAL:
			processor = new DelegatingProcessor(SpoonConditionalPredicate.INSTANCE,
					statement.getSourceFile(sourceFolder), statement.getLineNumber());
			processor.addProcessor(new ConditionalReplacer(ConditionalValueHolder.VARIABLE_NAME)).addProcessor(
					new ConditionalLoggingInstrumenter());
			break;
		case PRECONDITION:
			processor = new DelegatingProcessor(SpoonStatementPredicate.INSTANCE,
					statement.getSourceFile(sourceFolder), statement.getLineNumber());
			processor.addProcessor(new ConditionalLoggingInstrumenter()).addProcessor(
					new ConditionalAdder(ConditionalValueHolder.VARIABLE_NAME));
			logger.debug("No synthetizer found for {}, trying a precondition.", statement);
			break;
		default:
			logger.debug("No synthetizer found for {}.", statement);
			return NO_OP_SYNTHESIZER;
		}

		ConstraintModelBuilder constraintModelBuilder = new ConstraintModelBuilder(sourceFolder, statement, processor);
		return new DefaultSynthesizer(constraintModelBuilder, statement, type);
	}

	private Type getType(final SourceLocation rc) {
		StandardEnvironment env = new StandardEnvironment();
		env.setDebug(debug);
		Factory factory = new Factory(new DefaultCoreFactory(), env);
		ProcessingManager processing = new QueueProcessingManager(factory);
		TypelDetector detector = new TypelDetector(rc.getSourceFile(sourceFolder), rc.getLineNumber());
		processing.addProcessor(detector);
		Builder builder = factory.getBuilder();
		try {
			builder.addInputSource(sourceFolder);
			builder.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		processing.process();
		return detector.getType();
	}
}
