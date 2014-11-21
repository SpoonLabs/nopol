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
package fr.inria.lille.repair.symbolic.synth;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xxl.java.junit.CompoundResult;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestSuiteExecution;
import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedFile;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.commons.trace.SpecificationTestCasesListener;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.symbolic.jpf.JPFUtil;
import fr.inria.lille.repair.symbolic.spoon.LoggingInstrumenter;
import fr.inria.lille.repair.symbolic.spoon.SymbolicProcessor;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.bytecode.IfInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.search.SearchListenerAdapter;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.VM;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class JPFRunner {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private RuntimeValues<Boolean> runtimeValues;
	private SourceLocation sourceLocation;
	private SpoonedFile spoon;
	private SymbolicProcessor processor;

	private final File outputSourceFile = new File("src-gen");
	private final File outputCompiledFile = new File("target-gen");
	private boolean find = false;

	public JPFRunner(RuntimeValues<Boolean> runtimeValues,
			SourceLocation sourceLocation, SymbolicProcessor processor,
			SpoonedFile spooner) {
		this.sourceLocation = sourceLocation;
		this.runtimeValues = runtimeValues;
		this.spoon = spooner;
		this.processor = processor;
	}

	public Collection<Specification<Boolean>> buildFor(URL[] classpath,
			final String[] testClasses, final Collection<TestCase> failures,
			final SpoonedProject cleanSpoon, String mainClass) {

		final SpecificationTestCasesListener<Boolean> listener = new SpecificationTestCasesListener<Boolean>(
				runtimeValues);

		this.spoon.process(processor);
		this.spoon.generateOutputFile(outputSourceFile);
		try {
			this.spoon.generateOutputCompiledFile(outputCompiledFile);
		} catch (IOException e) {
			throw new RuntimeException("Unable to write transformed test", e);
		}

		// execute jpf
		TestCase[] array = failures.toArray(new TestCase[0]);
		String stringClassPath = "";
		for (int i = 2; i < classpath.length; i++) {
			URL url = classpath[i];
			stringClassPath += url.getPath() + ":";
		}
		stringClassPath += outputCompiledFile.getAbsolutePath() + ":";

		final LoggingInstrumenter logging = new LoggingInstrumenter(
				runtimeValues, processor);
		SpoonedClass fork = cleanSpoon.forked(sourceLocation
				.getContainingClassName());
		final ClassLoader unitTestClassLoader = fork
				.processedAndDumpedToClassLoader(logging);

		List<TestCase> passedTest = new ArrayList<TestCase>(failures.size());
		for (final TestCase testCase : array) {
			System.out.println(testCase);
			String[] args = new String[1];
			args[0] = testCase.className() + "." + testCase.testName();

			Config conf = JPFUtil.createConfig(args, mainClass,
					stringClassPath, outputSourceFile.getAbsolutePath());
			JPF jpf = new JPF(conf);

			// executes JPF
			JPFListener jpfListener = new JPFListener();
			jpf.addSearchListener(jpfListener);
			jpf.run();

			// get the JPF result
			Object result = jpfListener.getResult();

			// collect runtime
			boolean passed = executeTestAndCollectRuntimeValues(result,
					testCase, unitTestClassLoader, listener);
			if (passed) {
				this.find = true;
				CompoundResult testsResulults = TestSuiteExecution
						.runTestCases(failures, unitTestClassLoader, listener);
				if (!passedTest.contains(testCase)) {
					passedTest.add(testCase);
				}
				if (passedTest.size() == failures.size()) {
					break;
				}
			}
		}
		if (this.find) {
			logging.disable();
			TestSuiteExecution.runCasesIn(testClasses, unitTestClassLoader,
					listener);
		}
		return listener.specifications();
	}

	private boolean executeTestAndCollectRuntimeValues(Object result,
			TestCase currentTest, ClassLoader unitTestClassLoader,
			SpecificationTestCasesListener<Boolean> listener) {

		LoggingInstrumenter.setValue(result);
		Result testResult = TestSuiteExecution.runTestCase(currentTest,
				unitTestClassLoader, listener);
		if (testResult.getFailureCount() == 0) {
			return true;
		}
		return false;
	}

	public boolean isAViablePatch() {
		return this.find;
	}

	private class JPFListener extends SearchListenerAdapter {

		private Object result = false;

		public JPFListener() {
			super();
		}

		@Override
		public void searchFinished(Search search) {
			exec("searchFinished", search);
		}

		private void exec(String name, Search search) {
			VM vm = search.getVM();
			PCChoiceGenerator choiceGenerator = vm
					.getLastChoiceGeneratorOfType(PCChoiceGenerator.class);
			if (choiceGenerator != null) {
				Instruction instruction = choiceGenerator.getInsn();
				if (instruction instanceof IfInstruction) {
					this.result = ((IfInstruction) instruction)
							.getConditionValue();
				}
			}

		}

		public Object getResult() {
			return this.result;
		}
	}
}
