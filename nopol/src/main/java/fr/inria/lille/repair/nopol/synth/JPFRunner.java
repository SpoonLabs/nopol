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
package fr.inria.lille.repair.nopol.synth;

import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedFile;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.commons.trace.SpecificationTestCasesListener;
import fr.inria.lille.localization.TestResult;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.jpf.JPFUtil;
import fr.inria.lille.repair.nopol.spoon.LoggingInstrumenter;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.search.SearchListenerAdapter;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.VM;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxl.java.compiler.DynamicCompilationException;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestSuiteExecution;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

/**
 * Execute
 *
 * @author Thomas Durieux
 */
public final class JPFRunner<T> implements AngelicValue<T> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final RuntimeValues<T> runtimeValues;
	private final SourceLocation sourceLocation;
	private final SpoonedFile spoon;
	private final NopolProcessor processor;

	private final File outputSourceFile = new File("src-gen");
	private final File outputCompiledFile = new File("target-gen");
	private final SpoonedProject cleanSpoon;
	private boolean find = false;
	private NopolContext nopolContext;

	public JPFRunner(RuntimeValues<T> runtimeValues,
					 SourceLocation sourceLocation, NopolProcessor processor,
					 SpoonedFile spooner, final SpoonedProject cleanSpoon, NopolContext nopolContext) {
		this.nopolContext = nopolContext;
		this.sourceLocation = sourceLocation;
		this.runtimeValues = runtimeValues;
		this.spoon = spooner;
		this.processor = processor;
		this.cleanSpoon = cleanSpoon;
	}

	@Override
	public Collection<Specification<T>> collectSpecifications(final URL[] classpath, final List<TestResult> testClasses, final Collection<TestCase> failures) {
		SpoonedClass fork = cleanSpoon.forked(sourceLocation.getContainingClassName());
		final LoggingInstrumenter<T> logging = createLoggingInstrumenter();
		final ClassLoader unitTestClassLoader = fork.processedAndDumpedToClassLoader(logging);
		final SpecificationTestCasesListener<T> listener = run(classpath, failures, unitTestClassLoader);

		if (this.find) {
			logging.disable();
			TestSuiteExecution.runTestResult(testClasses, unitTestClassLoader, listener, nopolContext);
		}
		return listener.specifications();
	}

	@Override
	public Collection<Specification<T>> collectSpecifications(final URL[] classpath, final String[] testClasses, final Collection<TestCase> failures) {
		final LoggingInstrumenter<T> logging = createLoggingInstrumenter();
		SpoonedClass fork = cleanSpoon.forked(sourceLocation.getContainingClassName());
		final ClassLoader unitTestClassLoader = fork.processedAndDumpedToClassLoader(logging);

		final SpecificationTestCasesListener<T> listener = run(classpath, failures, unitTestClassLoader);

		if (this.find) {
			logging.disable();
			TestSuiteExecution.runCasesIn(testClasses, unitTestClassLoader, listener, nopolContext);
		}
		return listener.specifications();
	}

	private LoggingInstrumenter<T> createLoggingInstrumenter() {
		// transforms
		try {
			this.spoon.process(processor);
		} catch (DynamicCompilationException e) {
			throw new RuntimeException("Unable to compile the project", e);
		}
		try {
			this.spoon.generateOutputFile(outputSourceFile);
			this.spoon.generateOutputCompiledFile(outputCompiledFile);
		} catch (IOException e) {
			throw new RuntimeException("Unable to write transformed test", e);
		}

		return new LoggingInstrumenter<>(runtimeValues, processor);
	}

	private String createClassPath(final URL[] classpath) {
		String stringClassPath = outputCompiledFile.getAbsolutePath() + File.pathSeparatorChar;
		for (int i = 0; i < classpath.length; i++) {
			URL url = classpath[i];
			stringClassPath += url.getPath() + File.pathSeparatorChar;
		}
		return stringClassPath;
	}

	private SpecificationTestCasesListener<T> run(final URL[] classpath, final Collection<TestCase> failures, ClassLoader unitTestClassLoader) {
		final SpecificationTestCasesListener<T> listener = new SpecificationTestCasesListener<>(runtimeValues);

		// create the classpath for JPF
		String stringClassPath = createClassPath(classpath);

		String mainClass = "nopol.repair.NopolTestRunner";
		//TestExecutorProcessor.createMainTestClass(spoon, mainClass);

		List<TestCase> passedTest = new ArrayList<>(failures.size());
		Iterator<TestCase> iterator = failures.iterator();
		while (iterator.hasNext()) {
			TestCase testCase = iterator.next();
			logger.debug("SYMBOLIC EXECUTION on " + sourceLocation + " Test " + testCase);
			String[] args = new String[1];
			args[0] = testCase.className() + "." + testCase.testName();


			gov.nasa.jpf.Config conf = JPFUtil.createConfig(args, mainClass, stringClassPath, outputSourceFile.getAbsolutePath());
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
				continue;
			} catch (ExecutionException e) {
				e.printStackTrace();
				continue;
			} catch (TimeoutException e) {
				future.cancel(true);
				continue;
			}

			// get the JPF result
			Object result = jpfListener.getResult();
			if (result == null) {
				continue;
			}
			logger.debug("SYMBOLIC VALUE on " + sourceLocation + " for Test " + testCase + " Value: " + result);
			// collect runtime
			boolean passed = executeTestAndCollectRuntimeValues(result, testCase, unitTestClassLoader, listener);
			if (passed) {
				this.find = true;
				TestSuiteExecution.runTestCases(failures, unitTestClassLoader, listener, nopolContext);
				if (!passedTest.contains(testCase)) {
					passedTest.add(testCase);
				}
				if (passedTest.size() == failures.size()) {
					break;
				}
			}
		}

		return listener;
	}

	private boolean executeTestAndCollectRuntimeValues(Object result,
													   TestCase currentTest, ClassLoader unitTestClassLoader,
													   SpecificationTestCasesListener<T> listener) {

		LoggingInstrumenter.setValue(result);
		Result testResult = TestSuiteExecution.runTestCase(currentTest,
				unitTestClassLoader, listener, nopolContext);
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
