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
package fr.inria.lille.nopol.patch;

import static java.util.Arrays.asList;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.Processor;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoon.support.StandardEnvironment;
import fr.inria.lille.commons.spoon.SpoonClassLoaderBuilder;
import fr.inria.lille.commons.suite.TestSuiteExecution;
import fr.inria.lille.nopol.synth.BugKind;
import fr.inria.lille.nopol.synth.DelegatingProcessor;
import fr.inria.lille.nopol.synth.conditional.ConditionalReplacer;
import fr.inria.lille.nopol.synth.conditional.SpoonConditionalPredicate;
import fr.inria.lille.nopol.synth.precondition.ConditionalAdder;
import fr.inria.lille.nopol.synth.precondition.SpoonStatementPredicate;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class TestPatch {

	private static final String SPOON_DIRECTORY = File.separator + ".." + File.separator + "spooned";

	private final URL[] classpath;
	private final File sourceFolder;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public TestPatch(final File sourceFolder, final URL[] classpath) {
		this.sourceFolder = sourceFolder;
		this.classpath = classpath;
	}

	public static String getGeneratedPatchDirectorie(){
		return SPOON_DIRECTORY;
	}
	
	public boolean passesAllTests(final Patch patch, final String[] testClasses) {
		SpoonClassLoaderBuilder spooner = new SpoonClassLoaderBuilder(sourceFolder);
		logger.info("Applying patch: {}", patch);
		String qualifiedName = patch.getRootClassName();
		List<Processor<?>> processors = asList(null, null);
		processors.set(0, createProcessor(patch, patch.getFile(sourceFolder)));
		processors.set(1, new JavaOutputProcessor(new File(sourceFolder, SPOON_DIRECTORY), new DefaultJavaPrettyPrinter(new StandardEnvironment())));
		ClassLoader loader = spooner.buildSpooning(asList(qualifiedName), classpath, processors);
		Result result = TestSuiteExecution.runCasesIn(testClasses, loader);
		return result.wasSuccessful();
	}

	private DelegatingProcessor createProcessor(final Patch patch, final File sourceFile) {
		BugKind type = patch.getType();
		String patchAsString = patch.asString();
		int lineNumber = patch.getLineNumber();
		switch (type) {
		case CONDITIONAL:
			return new DelegatingProcessor(SpoonConditionalPredicate.INSTANCE, sourceFile, lineNumber)
			.addProcessor(new ConditionalReplacer(patchAsString));
		case PRECONDITION:
			return new DelegatingProcessor(SpoonStatementPredicate.INSTANCE, sourceFile, lineNumber)
			.addProcessor(new ConditionalAdder(patchAsString));
		default:
			throw new IllegalStateException("Unknown patch type " + type);
		}
	}
}
