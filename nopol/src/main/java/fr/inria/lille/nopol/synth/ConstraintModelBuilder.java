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

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.Processor;
import fr.inria.lille.commons.classes.CacheBasedClassLoader;
import fr.inria.lille.commons.spoon.SourceInstrumenter;
import fr.inria.lille.commons.suite.TestSuiteExecution;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.TestValuesCollectorListener;
import fr.inria.lille.nopol.SourceLocation;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConstraintModelBuilder {

	public ConstraintModelBuilder(final File sourceFolder, final SourceLocation sourceLocation, final Processor<?> processor) {
		classCache = new SourceInstrumenter(sourceFolder, new URL[] {}).instrumentedWith(processor, sourceLocation.getRootClassName());
	}

	public InputOutputValues buildFor(final URL[] classpath, final String[] testClasses) {
		InputOutputValues model = new InputOutputValues();
		Result firstResult = tracedExecutionResult(model, testClasses, classpath);
		GlobalBooleanVariable.flip();
		Result secondResult = tracedExecutionResult(model, testClasses, classpath);
		
		if (firstResult == null || secondResult == null) {
			viablePatch = false;
		} else if (firstResult.getFailureCount() == 0 || secondResult.getFailureCount() == 0){
			return new InputOutputValues(); // Return empty model because we don't want "true" or "false" as a solution
		}
		determineViability(firstResult, secondResult);
		return model;
	}
	
	private Result tracedExecutionResult(InputOutputValues model, String[] testClasses, URL[] classpath) {
		TestValuesCollectorListener listener = new TestValuesCollectorListener(model, GlobalBooleanVariable.value);
		ClassLoader cacheBasedClassLoader = new CacheBasedClassLoader(classpath, classCache);
		return TestSuiteExecution.runCasesIn(testClasses, cacheBasedClassLoader, listener);
	}

	private void determineViability(final Result firstResult, final Result secondResult) {
		Collection<Description> failingTests = TestSuiteExecution.collectDescription(firstResult.getFailures());
		Collection<Description> secondFailures = TestSuiteExecution.collectDescription(secondResult.getFailures());
		failingTests.retainAll(secondFailures);
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
