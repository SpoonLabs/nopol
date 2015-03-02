/*
 * Copyright (C) 2014 INRIA
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import gov.nasa.jpf.Property;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.search.SearchListenerAdapter;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.VM;

/**
 * Execute
 * 
 * @author Thomas Durieux
 * 
 */
public final class JPFRunner<T> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private RuntimeValues<T> runtimeValues;
	private SourceLocation sourceLocation;
	private SpoonedFile spoon;
	private SymbolicProcessor processor;

	private final File outputSourceFile = new File("src-gen");
	private final File outputCompiledFile = new File("target-gen");
	private boolean find = false;

	public JPFRunner(RuntimeValues<T> runtimeValues,
			SourceLocation sourceLocation, SymbolicProcessor processor,
			SpoonedFile spooner) {
		this.sourceLocation = sourceLocation;
		this.runtimeValues = runtimeValues;
		this.spoon = spooner;
		this.processor = processor;
	}

	public Collection<Specification<T>> buildFor(URL[] classpath,
			final String[] testClasses, final Collection<TestCase> failures,
			final SpoonedProject cleanSpoon, String mainClass) {

		final SpecificationTestCasesListener<T> listener = new SpecificationTestCasesListener<T>(
				runtimeValues);
		// transforms
		try {
			this.spoon.process(processor);
		} catch (xxl.java.compiler.DynamicCompilationException e) {
			return listener.specifications();
		}
		try {
			this.spoon.generateOutputFile(outputSourceFile);
			this.spoon.generateOutputCompiledFile(outputCompiledFile);
		} catch (IOException e) {
			throw new RuntimeException("Unable to write transformed test", e);
		}

		// create the classpath for JPF
		TestCase[] array = failures.toArray(new TestCase[0]);
		String stringClassPath = outputCompiledFile.getAbsolutePath() + ":";
		for (int i = 2; i < classpath.length; i++) {
			URL url = classpath[i];
			stringClassPath += url.getPath() + ":";
		}

		final LoggingInstrumenter<T> logging = new LoggingInstrumenter<T>(
				runtimeValues, processor);
		SpoonedClass fork = cleanSpoon.forked(sourceLocation
				.getContainingClassName());
		final ClassLoader unitTestClassLoader;
		try {
			unitTestClassLoader = fork
				.processedAndDumpedToClassLoader(logging);
		}catch (Exception e) {
			logger.error(e.getMessage());
			return listener.specifications();
		}
		List<TestCase> passedTest = new ArrayList<TestCase>(failures.size());
		for (final TestCase testCase : array) {
			logger.debug("SYMBOLIC EXECUTION on " + sourceLocation + " Test "
					+ testCase);
			String[] args = new String[1];
			args[0] = testCase.className() + "." + testCase.testName();

			Config conf = JPFUtil.createConfig(args, mainClass,
					stringClassPath, outputSourceFile.getAbsolutePath());
			final JPF jpf = new JPF(conf);

			// executes JPF
			JPFListener jpfListener = new JPFListener();
			jpf.addSearchListener(jpfListener);

			ExecutorService executor = Executors.newFixedThreadPool(1);

			Future<?> future = executor.submit(new Runnable() {
				@Override
				public void run() {
					jpf.run();
				}
			});

			executor.shutdown();

			try {
				future.get(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				System.out.println("job was interrupted");
				continue;
			} catch (ExecutionException e) {
				e.printStackTrace();
				System.out.println("caught exception: " + e.getCause());
				continue;
			} catch (TimeoutException e) {
				future.cancel(true);
				System.out.println("timeout");
				continue;
			}

			// get the JPF result
			Object result = jpfListener.getResult();
			if (result == null) {
				continue;
			}
			logger.debug("SYMBOLIC VALUE on " + sourceLocation + " for Test "
					+ testCase + " Value: " + result);
			// collect runtime
			boolean passed = executeTestAndCollectRuntimeValues(result,
					testCase, unitTestClassLoader, listener);
			if (passed) {
				this.find = true;
				TestSuiteExecution.runTestCases(failures, unitTestClassLoader,
						listener);
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
			SpecificationTestCasesListener<T> listener) {

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

		private Object result = null;

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
				PathCondition pc = choiceGenerator.getCurrentPC();
				if (search.getErrors().size() < 0) {
					return;
				}
				Property property = search.getLastError().getProperty();
				if (!property.getErrorMessage().contains(
						AssertionError.class.getCanonicalName())) {
					pc.header = pc.header.not();
				}
				/*
				 * if (property instanceof NoUncaughtExceptionsProperty) {
				 * NoUncaughtExceptionsProperty noUncaughtExceptionsProperty =
				 * (NoUncaughtExceptionsProperty) property; String clName =
				 * noUncaughtExceptionsProperty
				 * .getUncaughtExceptionInfo().getCauseClassname();
				 * if(!clName.equals(AssertionError.class.getCanonicalName())) {
				 * 
				 * } System.out.println(clName); }
				 */
				//
				/*
				 * if (instruction instanceof IfInstruction) { if
				 * (((IfInstruction) instruction).getConditionValue()) {
				 * pc.solve();
				 * 
				 * } }
				 */
				pc.solve();
				Map<String, Object> varsVals = new HashMap<String, Object>();
				pc.header.getVarVals(varsVals);
				if (varsVals.containsKey("guess_fix")) {
					this.result = varsVals.get("guess_fix");
					if (processor.getType().equals(Boolean.class)) {
						this.result = this.result.equals(1);
					}
				}
				logger.debug("JPF Result " + this.result);
			}

		}

		public Object getResult() {
			return this.result;
		}
	}
}
