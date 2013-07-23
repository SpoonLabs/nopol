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
package fr.inria.lille.jefix.synth.conditional;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import spoon.SpoonClassLoader;
import spoon.processing.Builder;
import spoon.processing.ProcessingManager;
import fr.inria.lille.jefix.SourceLocation;
import fr.inria.lille.jefix.synth.InputOutputValues;
import fr.inria.lille.jefix.threads.ProvidedClassLoaderThreadFactory;

/**
 * @author Favio D. DeMarco
 * 
 */
final class ConditionalsConstraintModelBuilder {

	private static final long TIME_OUT_SECONDS = 1800L;

	private final ConditionalReplacer conditionalReplacer;
	private final boolean debug = LoggerFactory.getLogger(this.getClass()).isDebugEnabled();
	private final ClassLoader spooner;

	ConditionalsConstraintModelBuilder(final File sourceFolder, final SourceLocation sourceLocation, final boolean value) {
		SpoonClassLoader scl = new SpoonClassLoader();
		scl.getEnvironment().setDebug(this.debug);
		ProcessingManager processingManager = scl.getProcessingManager();
		File sourceFile = sourceLocation.getSourceFile(sourceFolder);
		int lineNumber = sourceLocation.getLineNumber();
		this.conditionalReplacer = new ConditionalReplacer(sourceFile, lineNumber, value);
		processingManager.addProcessor(this.conditionalReplacer);
		processingManager.addProcessor(new ConditionalLoggingInstrumenter(sourceFile, lineNumber));
		Builder builder;
		builder = scl.getFactory().getBuilder();
		try {
			builder.addInputSource(sourceFolder);
			builder.build();
			// should be loaded by the spoon class loader
			scl.loadClass(sourceLocation.getContainingClassName());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		this.spooner = scl;
	}

	InputOutputValues buildFor(final URL[] classpath, final String[] testClasses, final InputOutputValues model) {
		ClassLoader cl = new URLClassLoader(classpath, this.spooner);
		// should use the url class loader
		ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(cl));
		try {
			executor.execute(new JUnitRunner(new ResultMatrixBuilderListener(model, true), testClasses));

			this.shutdownAndWait(executor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		return model;
	}

	/**
	 * @param executor
	 * @throws InterruptedException
	 */
	private void shutdownAndWait(final ExecutorService executor) throws InterruptedException {
		executor.shutdown();
		executor.awaitTermination(TIME_OUT_SECONDS, TimeUnit.SECONDS);
	}
}
