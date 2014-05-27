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
package fr.inria.lille.nopol.test.junit;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.processing.ProcessingManager;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import fr.inria.lille.commons.classes.CacheBasedClassLoader;
import fr.inria.lille.commons.classes.ProvidedClassLoaderThreadFactory;
import fr.inria.lille.nopol.SpoonClassLoader;
import fr.inria.lille.nopol.patch.Patch;
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
	private final boolean debug = logger.isDebugEnabled();
	private final SpoonClassLoader spooner;
	
	public TestPatch(final File sourceFolder, final URL[] classpath) {
		this.sourceFolder = sourceFolder;
		this.classpath = classpath;
		SpoonClassLoader scl = new SpoonClassLoader(sourceFolder);
		scl.getEnvironment().setDebug(debug);
		SpoonCompiler builder;
		try {
			builder = new Launcher().createCompiler(scl.getFactory());
			builder.addInputSource(sourceFolder);
		    builder.build();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		spooner = scl;
	}

	public static String getGeneratedPatchDirectorie(){
		return SPOON_DIRECTORY;
	}
	
	public boolean passesAllTests(final Patch patch, final String[] testClasses) {
		ProcessingManager processingManager = spooner.getProcessingManager();
		logger.info("Applying patch: {}", patch);
		File sourceFile = patch.getFile(sourceFolder);
		processingManager.addProcessor(createProcessor(patch, sourceFile));
		processingManager.addProcessor(new JavaOutputProcessor(new File(sourceFolder, SPOON_DIRECTORY), new DefaultJavaPrettyPrinter(spooner.getEnvironment())));
		try {
			spooner.loadClass(patch.getRootClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return wasSuccessful(testClasses);
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

	boolean wasSuccessful(final String[] testClasses) {
		ClassLoader cl = new CacheBasedClassLoader(classpath, spooner.getClasscache());
		ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(cl));
		Result result;
		try {
			result = executor.submit(new JUnitRunner(testClasses)).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		executor.shutdown();
		return result.wasSuccessful();
	}
}
