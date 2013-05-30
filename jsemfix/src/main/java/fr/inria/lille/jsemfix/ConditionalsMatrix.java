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
package fr.inria.lille.jsemfix;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import spoon.SpoonClassLoader;
import spoon.processing.AbstractProcessor;
import spoon.processing.Builder;
import spoon.processing.ProcessingManager;
import spoon.reflect.Factory;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.support.DefaultCoreFactory;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.code.CtLiteralImpl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import fr.inria.lille.jsemfix.sps.SuspiciousStatement;
import fr.inria.lille.jsemfix.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.jsemfix.test.Test;
import fr.inria.lille.jsemfix.test.TestClassesFinder;
import fr.inria.lille.jsemfix.test.junit.JUnitTest;

/**
 * @author Favio D. DeMarco
 * 
 */
final class ConditionalsMatrix {

	private static final class ConditionalDetector extends AbstractProcessor<CtExpression<Boolean>> {

		boolean answer;

		final File file;
		final int line;

		/**
		 * @param file
		 * @param line
		 */
		ConditionalDetector(final File file, final int line) {
			this.file = file;
			this.line = line;
		}

		/**
		 * @return the answer
		 */
		boolean isConditional() {
			return this.answer;
		}

		@Override
		public void process(final CtExpression<Boolean> element) {
			SourcePosition position = element.getPosition();
			if (position.getLine() == this.line && position.getFile().equals(this.file)) {
				CtElement parent = element.getParent();
				if (parent instanceof CtConditional || parent instanceof CtIf) {
					this.answer = true;
				}
			}
		}
	}

	/**
	 * XXX FIXME TODO duplicated code {@code IfCoConditionalReplacer}
	 * 
	 * @author Favio D. DeMarco
	 */
	private static final class ConditionalReplacer extends AbstractProcessor<CtConditional<Object>> {

		final File file;
		final int line;
		final boolean value;

		/**
		 * @param file
		 * @param line
		 */
		ConditionalReplacer(final File file, final int line, final boolean value) {
			this.file = file;
			this.line = line;
			this.value = value;
		}

		@Override
		public void process(final CtConditional<Object> element) {
			SourcePosition position = element.getPosition();
			if (position.getLine() == this.line && position.getFile().equals(this.file)) {
				// we declare a new snippet of code to be inserted
				CtLiteral<Boolean> snippet = new CtLiteralImpl<>();
				snippet.setFactory(this.getFactory());
				snippet.setValue(this.value);
				element.getCondition().replace(snippet);
			}
		}
	}

	/**
	 * XXX FIXME TODO duplicated code {@code ConditionalReplacer}
	 * 
	 * @author Favio D. DeMarco
	 */
	private static final class IfCoConditionalReplacer extends AbstractProcessor<CtIf> {

		final File file;
		final int line;
		final boolean value;

		/**
		 * @param file
		 * @param line
		 */
		IfCoConditionalReplacer(final File file, final int line, final boolean value) {
			this.file = file;
			this.line = line;
			this.value = value;
		}

		@Override
		public void process(final CtIf element) {

			SourcePosition position = element.getPosition();
			if (position.getLine() == this.line && position.getFile().equals(this.file)) {
				// we declare a new snippet of code to be inserted
				CtLiteral<Boolean> snippet = new CtLiteralImpl<>();
				snippet.setFactory(this.getFactory());
				snippet.setValue(this.value);
				element.getCondition().replace(snippet);
			}
		}
	}

	private enum Result {
		FAIL, OK;
	}

	private static final class ResultMatrixBuilderListener extends RunListener {

		final Table<Test, Boolean, Result> matrix;

		final boolean value;

		/**
		 * @param matrix
		 */
		ResultMatrixBuilderListener(final Table<Test, Boolean, Result> matrix, final boolean value) {
			this.matrix = matrix;
			this.value = value;
		}

		/**
		 * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
		 */
		@Override
		public void testFailure(final Failure failure) throws Exception {
			this.matrix.put(new JUnitTest(failure.getDescription()), this.value, Result.FAIL);
		}

		/**
		 * @see org.junit.runner.notification.RunListener#testFinished(org.junit.runner.Description)
		 */
		@Override
		public void testFinished(final Description description) throws Exception {
			JUnitTest desc = new JUnitTest(description);
			if (null == this.matrix.get(desc, this.value)) {
				this.matrix.put(desc, this.value, Result.OK);
			}
		}
	}

	private final Package rootPackage;

	private final File sourceFolder;

	/**
	 * @param rootPackage
	 * @param sourceFolder
	 */
	ConditionalsMatrix(final Package rootPackage, final File sourceFolder) {
		this.rootPackage = checkNotNull(rootPackage);
		this.sourceFolder = checkNotNull(sourceFolder);
	}

	public Table<Test, Boolean, Result> build() {

		// A ranked list of potential bug root-cause.
		Class<?>[] testClasses = this.findTestClasses();
		Iterable<SuspiciousStatement> statements = GZoltarSuspiciousProgramStatements.createWithPackageAndTestClasses(
				this.rootPackage, testClasses).sortBySuspiciousness();
		Table<Test, Boolean, Result> table = HashBasedTable.create();
		for (SuspiciousStatement rc : statements) {
			if (this.isConditional(rc)) {
				this.runFor(rc, true, table);
				this.runFor(rc, false, table);
			}
		}
		return table;
	}

	private Class<?>[] findTestClasses() {
		return new TestClassesFinder(this.rootPackage).find().toArray(new Class[] {});
	}

	private File getSourceFile(final Class<?> problemClass) {
		String classPath = problemClass.getName().replace('.', File.separatorChar);
		File sourceFile = new File(this.sourceFolder, classPath + ".java");
		checkState(sourceFile.exists(), "%s: does not exist.", sourceFile);
		return sourceFile;
	}

	private boolean isConditional(final SuspiciousStatement rc) {

		StandardEnvironment env = new StandardEnvironment();
		Factory factory = new Factory(new DefaultCoreFactory(), env);
		ProcessingManager processing = new QueueProcessingManager(factory);
		ConditionalDetector detector = new ConditionalDetector(this.getSourceFile(rc.getContainingClass()),
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
		ProcessingManager processingManager = ccl.getProcessingManager();
		processingManager.addProcessor(new ConditionalReplacer(this.getSourceFile(rc.getContainingClass()), rc
				.getLineNumber(), value));
		processingManager.addProcessor(new IfCoConditionalReplacer(this.getSourceFile(rc.getContainingClass()), rc
				.getLineNumber(), value));
		Builder builder = ccl.getFactory().getBuilder();

		JUnitCore executor;

		try {
			builder.addInputSource(this.sourceFolder);
			builder.build();

			// fragile...
			ccl.loadClass("fr.inria.lille.jsemfix.examples.bool.constant.Neg");

			@SuppressWarnings("unchecked")
			Class<JUnitCore> junitCoreClass = (Class<JUnitCore>) ccl.loadClass(JUnitCore.class.getName());

			executor = junitCoreClass.getConstructor().newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		executor.addListener(new ResultMatrixBuilderListener(table, value));
		Class<?>[] testClasses = this.findTestClasses();
		executor.run(testClasses);
	}
}
