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

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Sets.intersection;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.SpoonClassLoader;
import spoon.processing.Builder;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import fr.inria.lille.jefix.SourceLocation;
import fr.inria.lille.jefix.test.junit.JUnitRunner;
import fr.inria.lille.jefix.threads.ProvidedClassLoaderThreadFactory;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConstraintModelBuilder {

	/**
	 * XXX FIXME TODO should be a parameter
	 */
	private static final long TIMEOUT_IN_SECONDS = 60;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final boolean debug = logger.isDebugEnabled();
	private final ClassLoader spooner;
	private boolean viablePatch;

	public ConstraintModelBuilder(final File sourceFolder, final SourceLocation sourceLocation,
			final Processor<?> processor) {
		SpoonClassLoader scl = new SpoonClassLoader();
		scl.getEnvironment().setDebug(debug);
		ProcessingManager processingManager = scl.getProcessingManager();
		processingManager.addProcessor(processor);
		Builder builder = scl.getFactory().getBuilder();
		try {
			builder.addInputSource(sourceFolder);
			builder.build();
			// should be loaded by the spoon class loader
			scl.loadClass(sourceLocation.getContainingClassName());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		spooner = scl;
	}

	/**
	 * @see fr.inria.lille.jefix.synth.ConstraintModelBuilder#buildFor(java.net.URL[], java.lang.String[])
	 */
	public InputOutputValues buildFor(final URL[] classpath, final String[] testClasses) {
		InputOutputValues model = new InputOutputValues();
		ClassLoader cl = new URLClassLoader(classpath, spooner);
		// should use the url class loader
		ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(cl));
		try {
			Result firstResult = executor.submit(
					new JUnitRunner(testClasses, new ResultMatrixBuilderListener(model,
							ConditionalValueHolder.booleanValue))).get(TIMEOUT_IN_SECONDS, SECONDS);
			ConditionalValueHolder.flip();
			Result secondResult = executor.submit(
					new JUnitRunner(testClasses, new ResultMatrixBuilderListener(model,
							ConditionalValueHolder.booleanValue))).get(TIMEOUT_IN_SECONDS, SECONDS);
			determineViability(firstResult, secondResult);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			logger.warn("Timeout after {} seconds. Infinite loop?", TIMEOUT_IN_SECONDS);
			viablePatch = false;
		} finally {
			executor.shutdownNow();
		}
		return model;
	}

	private void determineViability(final Result firstResult, final Result secondResult) {
		Set<Description> firstFailures = copyOf(transform(firstResult.getFailures(), FailureToDescription.INSTANCE));
		Set<Description> secondFailures = copyOf(transform(secondResult.getFailures(), FailureToDescription.INSTANCE));
		Set<Description> failingTests = intersection(firstFailures, secondFailures);
		viablePatch = failingTests.isEmpty();
		if (!viablePatch) {
			logger.debug("Failing test(s): {}", failingTests);
			Logger testsOutput = LoggerFactory.getLogger("tests.output");
			testsOutput.debug("First set: \n{}", firstResult.getFailures());
			testsOutput.debug("Second set: \n{}", secondResult.getFailures());
		}
	}

	/**
	 * @see fr.inria.lille.jefix.synth.ConstraintModelBuilder#isAViablePatch()
	 */
	public boolean isAViablePatch() {
		return viablePatch;
	}
}
