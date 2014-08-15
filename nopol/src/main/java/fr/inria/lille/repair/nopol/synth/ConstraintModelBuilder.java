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

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.Processor;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.suite.TestSuiteExecution;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.commons.trace.SpecificationTestCasesListener;
import fr.inria.lille.commons.utils.Function;
import fr.inria.lille.repair.nopol.NoPol;
import fr.inria.lille.repair.nopol.SourceLocation;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConstraintModelBuilder {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private boolean viablePatch;
	private final BugKind type;
	private final int mapID;
	
	private final ClassLoader classLoader;
	private RuntimeValues runtimeValues;
	
	public ConstraintModelBuilder(final File sourceFolder, RuntimeValues runtimeValues, final SourceLocation sourceLocation,
			final Processor<?> processor, SpoonedProject spooner, final BugKind type) {
		this.type = type;
		mapID = ConditionalValueHolder.ID_Conditional;
		String qualifiedName = sourceLocation.getRootClassName();
		if ( NoPol.isOneBuild() ){
			ConditionalValueHolder.ID_Conditional++;
			classLoader = spooner.processedAndDumpedToClassLoader(qualifiedName, processor);
		} else {
			classLoader = spooner.forked(qualifiedName).processedAndDumpedToClassLoader(processor);
		}
		this.runtimeValues = runtimeValues;
	}

	/**
	 * @see fr.inria.lille.repair.nopol.synth.ConstraintModelBuilder#buildFor(java.net.URL[], java.lang.String[])
	 */
	public Collection<Specification<Boolean>> buildFor(final URL[] classpath, final String[] testClasses) {
		Collection<Specification<Boolean>> specifications = SetLibrary.newHashSet();
		if (NoPol.isOneBuild()) {
			ConditionalValueHolder.ID_Conditional = mapID;
			if (type == BugKind.CONDITIONAL || type == BugKind.PRECONDITION) {
				ConditionalValueHolder.enableNextCondition();
			}
		}
		SpecificationTestCasesListener<Boolean> listener = new SpecificationTestCasesListener<Boolean>(runtimeValues, outputForEachTrace());
		Result firstResult = TestSuiteExecution.runCasesIn(testClasses, classLoader, listener);
		if (firstResult == null) {
			return specifications;
		}
		ConditionalValueHolder.flip();
		Result secondResult = TestSuiteExecution.runCasesIn(testClasses, classLoader, listener);
		if (secondResult == null || firstResult.getFailureCount()==0 || secondResult.getFailureCount() == 0){
			return specifications;
		}
		determineViability(firstResult, secondResult);
		return listener.specifications();
	}

	private Function<Integer, Boolean> outputForEachTrace() {
		return new Function<Integer, Boolean>() {
			@Override
			public Boolean outputFor(Integer trace) {
				return ConditionalValueHolder.booleanValue;
			}
		};
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
	 * @see fr.inria.lille.repair.nopol.synth.ConstraintModelBuilder#isAViablePatch()
	 */
	public boolean isAViablePatch() {
		return viablePatch;
	}
}
