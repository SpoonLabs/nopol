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

import static fr.inria.lille.jefix.synth.Synthetizer.NO_OP_SYNTHETIZER;

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
import fr.inria.lille.jefix.synth.conditional.ConditionalSynthetizer;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class SynthetizerFactory {

	private final File sourceFolder;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final boolean debug = this.logger.isDebugEnabled();

	/**
	 * 
	 */
	public SynthetizerFactory(final File sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	public Synthetizer getFor(final SourceLocation statement) {
		if (this.isConditional(statement)) {
			return new ConditionalSynthetizer(this.sourceFolder, statement);
		}
		this.logger.debug("No synthetizer found for {}", statement);
		return NO_OP_SYNTHETIZER;
	}

	private boolean isConditional(final SourceLocation rc) {
		StandardEnvironment env = new StandardEnvironment();
		env.setDebug(this.debug);
		Factory factory = new Factory(new DefaultCoreFactory(), env);
		ProcessingManager processing = new QueueProcessingManager(factory);
		ConditionalDetector detector = new ConditionalDetector(rc.getSourceFile(this.sourceFolder), rc.getLineNumber());
		processing.addProcessor(detector);
		Builder builder = factory.getBuilder();
		try {
			builder.addInputSource(this.sourceFolder);
			builder.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		processing.process();
		return detector.isConditional();
	}
}
