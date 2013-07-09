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
package fr.inria.lille.jsemfix.conditional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.SpoonClassLoader;
import spoon.processing.Builder;
import spoon.processing.ProcessingManager;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import fr.inria.lille.jsemfix.sps.SuspiciousStatement;
import fr.inria.lille.jsemfix.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.jsemfix.test.Test;

/**
 * @author Favio D. DeMarco
 * 
 */
final class ConditionalsMatrix {

	private final String rootPackage;

	private final File sourceFolder;
	private final URL[] classpath;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final boolean debug = this.logger.isDebugEnabled();

	/**
	 * @param rootPackage
	 * @param sourceFolder
	 */
	ConditionalsMatrix(final String rootPackage, final File sourceFolder, final URL[] paths) {
		this.rootPackage = checkNotNull(rootPackage);
		this.sourceFolder = checkNotNull(sourceFolder);
		this.classpath = checkNotNull(paths);
	}

	/**
	 * XXX FIXME TODO wtf!?
	 */
	private String[] testClasses;

	public Table<Test, Boolean, Result> build() {

		// A ranked list of potential bug root-cause.
		this.testClasses = this.findTestClasses();
		Iterable<SuspiciousStatement> statements = GZoltarSuspiciousProgramStatements.create(this.rootPackage,
				this.classpath, this.testClasses).sortBySuspiciousness();
		Table<Test, Boolean, Result> table = HashBasedTable.create();
		for (SuspiciousStatement rc : statements) {
			if (this.isConditional(rc)) {
				this.runFor(rc, true, table);
				this.runFor(rc, false, table);
			}
		}
		return table;
	}

	private String[] findTestClasses() {

		ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(
				new URLClassLoader(this.classpath)));

		String[] testClasses;
		try {
			testClasses = executor.submit(new TestClassesFinder(this.rootPackage)).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} finally {
			executor.shutdown();
		}
		return testClasses;
	}

	private File getSourceFile(final String problemClass) {
		String classPath = problemClass.replace('.', File.separatorChar);
		File sourceFile = new File(this.sourceFolder, classPath + ".java");
		checkState(sourceFile.exists(), "%s: does not exist.", sourceFile);
		return sourceFile;
	}

	private boolean isConditional(final SuspiciousStatement rc) {

		StandardEnvironment env = new StandardEnvironment();
		env.setDebug(this.debug);
		Factory factory = new Factory(new DefaultCoreFactory(), env);
		ProcessingManager processing = new QueueProcessingManager(factory);
		ConditionalDetector detector = new ConditionalDetector(this.getSourceFile(rc.getContainingClassName()),
				rc.getLineNumber());
		processing.addProcessor(detector);
		Builder builder = factory.getBuilder();
		try {
			builder.addInputSource(this.sourceFolder);
			builder.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		processing.process();
		return detector.isConditional();
	}

	private void runFor(final SuspiciousStatement rc, final boolean value, final Table<Test, Boolean, Result> table) {

		SpoonClassLoader ccl = new SpoonClassLoader();

		ccl.getEnvironment().setDebug(this.debug);

		ProcessingManager processingManager = ccl.getProcessingManager();
		File sourceFile = this.getSourceFile(rc.getContainingClassName());
		int lineNumber = rc.getLineNumber();
		processingManager.addProcessor(new ConditionalReplacer(sourceFile, lineNumber, value));
		processingManager.addProcessor(new IfConditionalReplacer(sourceFile, lineNumber, value));
		processingManager.addProcessor(new ConditionalLoggingInstrumenter(sourceFile, lineNumber, value));
		Builder builder = ccl.getFactory().getBuilder();

		ClassLoader cl = new URLClassLoader(this.classpath, ccl);

		try {

			builder.addInputSource(this.sourceFolder);
			builder.build();
			// should be loaded by the spoon class loader
			ccl.loadClass(rc.getContainingClassName());

			// should use the url class loader
			ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(cl));
			executor.execute(new JUnitRunner(new ResultMatrixBuilderListener(table, value), this.testClasses));
			executor.shutdown();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		System.err.println(ValuesCollector.getValues());
	}
}
