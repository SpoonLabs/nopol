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

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Sets.intersection;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.slf4j.LoggerFactory;

import spoon.SpoonClassLoader;
import spoon.processing.Builder;
import spoon.processing.ProcessingManager;

import com.google.common.collect.ImmutableSet;

import fr.inria.lille.jefix.SourceLocation;
import fr.inria.lille.jefix.synth.InputOutputValues;
import fr.inria.lille.jefix.test.junit.JUnitRunner;
import fr.inria.lille.jefix.threads.ProvidedClassLoaderThreadFactory;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConditionalsConstraintModelBuilder {

	/**
	 * Optimist...
	 */
	public static volatile boolean booleanValue = true;

	private final boolean debug = LoggerFactory.getLogger(this.getClass()).isDebugEnabled();
	private final ClassLoader spooner;
	private boolean viablePatch;

	ConditionalsConstraintModelBuilder(final File sourceFolder, final SourceLocation sourceLocation) {
		SpoonClassLoader scl = new SpoonClassLoader();
		scl.getEnvironment().setDebug(this.debug);
		ProcessingManager processingManager = scl.getProcessingManager();
		File sourceFile = sourceLocation.getSourceFile(sourceFolder);
		int lineNumber = sourceLocation.getLineNumber();
		processingManager.addProcessor(new ConditionalReplacer(sourceFile, lineNumber, this.getClass().getName()
				+ ".booleanValue"));
		processingManager.addProcessor(new ConditionalLoggingInstrumenter(sourceFile, lineNumber));
		Builder builder = scl.getFactory().getBuilder();
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

	InputOutputValues buildFor(final URL[] classpath, final String[] testClasses) {
		InputOutputValues model = new InputOutputValues();
		ClassLoader cl = new URLClassLoader(classpath, this.spooner);
		// should use the url class loader
		ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(cl));
		try {
			Result firstResult = executor.submit(
					new JUnitRunner(testClasses, new ResultMatrixBuilderListener(model, booleanValue))).get();
			booleanValue = !booleanValue;
			Result secondResult = executor.submit(
					new JUnitRunner(testClasses, new ResultMatrixBuilderListener(model, booleanValue))).get();
			this.determineViability(firstResult, secondResult);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		executor.shutdown();
		return model;
	}

	private void determineViability(final Result firstResult, final Result secondResult) {
		ImmutableSet<Description> firstFailures = copyOf(transform(firstResult.getFailures(),
				FailureToDescription.INSTANCE));
		ImmutableSet<Description> secondFailures = copyOf(transform(secondResult.getFailures(),
				FailureToDescription.INSTANCE));
		this.viablePatch = intersection(firstFailures, secondFailures).isEmpty();
	}

	/**
	 * @return the viablePatch
	 */
	boolean isAViablePatch() {
		return this.viablePatch;
	}
}
