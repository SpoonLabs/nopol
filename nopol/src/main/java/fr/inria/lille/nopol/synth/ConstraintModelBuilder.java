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

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Sets.intersection;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.Processor;
import fr.inria.lille.commons.spoon.SpoonClassLoader;
import fr.inria.lille.commons.suite.TestSuiteExecution;
import fr.inria.lille.nopol.SourceLocation;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConstraintModelBuilder {

	public ConstraintModelBuilder(final File sourceFolder, final SourceLocation sourceLocation, final Processor<?> processor) {
		classCache = SpoonClassLoader.classesTransformedWith(processor, sourceFolder, sourceLocation.getRootClassName());
	}

	public InputOutputValues buildFor(final URL[] classpath, final String[] testClasses) {
		InputOutputValues model = new InputOutputValues();
		Result firstResult = runWithListener(model, testClasses, classpath);
		ConditionalValueHolder.flip();
		Result secondResult = runWithListener(model, testClasses, classpath);
		
		if (firstResult == null || secondResult == null) {
			viablePatch = false;
		} else if (firstResult.getFailureCount() == 0 || secondResult.getFailureCount() == 0){
			return new InputOutputValues(); // Return empty model because we don't want "true" or "false" as a solution
		}
		determineViability(firstResult, secondResult);
		return model;
	}
	
	private Result runWithListener(InputOutputValues model, String[] testClasses, URL[] classpath) {
		ResultMatrixBuilderListener listener = new ResultMatrixBuilderListener(model, ConditionalValueHolder.booleanValue);
		return TestSuiteExecution.runCasesIn(testClasses, classpath, classCache, listener);
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

	public boolean isAViablePatch() {
		return viablePatch;
	}
	
	private boolean viablePatch;
	private final Map<String, Class<?>> classCache;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
