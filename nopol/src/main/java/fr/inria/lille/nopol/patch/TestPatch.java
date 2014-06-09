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

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.Processor;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import fr.inria.lille.commons.classes.CacheBasedClassLoader;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.spoon.SourceInstrumenter;
import fr.inria.lille.commons.spoon.SpoonLibrary;
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

	public TestPatch(final File sourceFolder, final URL[] classpath) {
		this.sourceFolder = sourceFolder;
		this.classpath = classpath;
	}

	public static String getGeneratedPatchDirectorie(){
		return SPOON_DIRECTORY;
	}
	
	public boolean passesAllTests(final Patch patch, final String[] testClasses) {
		logger.info("Applying patch: {}", patch);
		Collection<Processor<?>> processors = ListLibrary.newArrayList();
		processors.add(createProcessor(patch, patch.getFile(sourceFolder)));
		processors.add(new JavaOutputProcessor(new File(sourceFolder, SPOON_DIRECTORY), new DefaultJavaPrettyPrinter(SpoonLibrary.newEnvironment())));
		return wasSuccessful(patch.getRootClassName(), processors, testClasses);
	}

	private boolean wasSuccessful(String classWithPatch, Collection<Processor<?>> processors, String[] testClasses) {
		Map<String, Class<?>> classCache = new SourceInstrumenter(sourceFolder, classpath).instrumentedWith(processors, classWithPatch);
		ClassLoader cacheBasedClassLoader = new CacheBasedClassLoader(classpath, classCache);
		Result result = TestSuiteExecution.runCasesIn(testClasses, cacheBasedClassLoader);
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
	
	private final URL[] classpath;
	private final File sourceFolder;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String SPOON_DIRECTORY = File.separator + ".." + File.separator + "spooned";
}
