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

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.Processor;
import fr.inria.lille.commons.spoon.SpoonClassLoaderBuilder;
import fr.inria.lille.commons.suite.TestSuiteExecution;
import fr.inria.lille.nopol.NoPol;
import fr.inria.lille.nopol.SourceLocation;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConstraintModelBuilder {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonClassLoaderBuilder spooner;
	private boolean viablePatch;
	private final SourceLocation sourceLocation;
	private final BugKind type;
	private final int mapID;
	
	private Processor<?> processor;
	
	public ConstraintModelBuilder(final File sourceFolder, final SourceLocation sourceLocation,
			final Processor<?> processor, SpoonClassLoaderBuilder spooner, final BugKind type) {
		this.sourceLocation = sourceLocation;
		this.type = type;
		this.processor = processor;
		mapID = ConditionalValueHolder.ID_Conditional;
		
		if ( NoPol.isOneBuild() ){
			ConditionalValueHolder.ID_Conditional++;
		} else {
			spooner = new SpoonClassLoaderBuilder(sourceFolder);
		}
		
		this.spooner = spooner;
	}

	/**
	 * @see fr.inria.lille.nopol.synth.ConstraintModelBuilder#buildFor(java.net.URL[], java.lang.String[])
	 */
	public InputOutputValues buildFor(final URL[] classpath, final String[] testClasses) {
		if (NoPol.isOneBuild()) {
			ConditionalValueHolder.ID_Conditional = mapID;
			if (type == BugKind.CONDITIONAL || type == BugKind.PRECONDITION) {
				ConditionalValueHolder.enableNextCondition();
			}
		}
		ClassLoader loader = spooner.buildSpooning(sourceLocation.getRootClassName(), classpath, processor);
		
		InputOutputValues model = new InputOutputValues();
		Result firstResult = TestSuiteExecution.runCasesIn(testClasses, loader, new ResultMatrixBuilderListener(model, ConditionalValueHolder.booleanValue, mapID));
		ConditionalValueHolder.flip();
		Result secondResult = TestSuiteExecution.runCasesIn(testClasses, loader, new ResultMatrixBuilderListener(model, ConditionalValueHolder.booleanValue, mapID));
		if ( firstResult.getFailureCount()==0 || secondResult.getFailureCount() == 0){
			/*
			 * Return empty model because we don't want "true" or "false" as a solution
			 */
			return new InputOutputValues();
		}
		determineViability(firstResult, secondResult);
		return model;
	}

	private void determineViability(final Result firstResult, final Result secondResult) {
		Collection<Description> firstFailures = TestSuiteExecution.collectDescription(firstResult.getFailures());
		Collection<Description> secondFailures = TestSuiteExecution.collectDescription(secondResult.getFailures());
		firstFailures.retainAll(secondFailures);
		viablePatch = firstFailures.isEmpty();
		if (!viablePatch) {
			logger.debug("Failing test(s): {}", firstFailures);
			Logger testsOutput = LoggerFactory.getLogger("tests.output");
			testsOutput.debug("First set: \n{}", firstResult.getFailures());
			testsOutput.debug("Second set: \n{}", secondResult.getFailures());
		}
	}

	/**
	 * @see fr.inria.lille.nopol.synth.ConstraintModelBuilder#isAViablePatch()
	 */
	public boolean isAViablePatch() {
		return viablePatch;
	}
}
