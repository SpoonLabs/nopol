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
package fr.inria.lille.repair.nopol;

import com.gzoltar.core.components.Statement;
import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedFile;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.localization.FaultLocalizer;
import fr.inria.lille.localization.TestResult;
import fr.inria.lille.repair.Main;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.TestClassesFinder;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.patch.TestPatch;
import fr.inria.lille.repair.nopol.spoon.ConditionalLoggingInstrumenter;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.nopol.spoon.NopolProcessorBuilder;
import fr.inria.lille.repair.nopol.spoon.symbolic.AssertReplacer;
import fr.inria.lille.repair.nopol.spoon.symbolic.TestExecutorProcessor;
import fr.inria.lille.repair.nopol.synth.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.Processor;
import spoon.reflect.code.CtStatement;
import xxl.java.compiler.DynamicCompilationException;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.*;


/**
 * @author Favio D. DeMarco
 */
public class NoPol {

	private FaultLocalizer localizer;

	public static Statement currentStatement;
	private URL[] classpath;
	private final TestPatch testPatch;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonedProject spooner;
	private final File[] sourceFiles;
	private String[] testClasses;
	public long startTime;
	private Config config;


	public NoPol(ProjectReference project, Config config) {
		this.config = config;
		this.classpath = project.classpath();
		this.sourceFiles = project.sourceFiles();
		this.spooner = new SpoonedProject(this.sourceFiles, this.classpath, config);
		if (project.testClasses() != null) {
			this.testClasses = project.testClasses();
		}
		this.testPatch = new TestPatch(this.sourceFiles[0], this.spooner, config);
		this.startTime = System.currentTimeMillis();
	}

	public NoPol(final File[] sourceFiles, final URL[] classpath, Config config) {
		this(new ProjectReference(sourceFiles, classpath), config);
	}

	public List<Patch> build() {
		this.testClasses = new TestClassesFinder().findIn(classpath, false);
		return build(this.testClasses);
	}

	public List<Patch> build(String[] testClasses) {
		this.localizer = config.getLocalizer(this.sourceFiles, this.classpath, testClasses);
		if (config.getOracle() == Config.NopolOracle.SYMBOLIC) {
			try {
				SpoonedProject jpfSpoon = new SpoonedProject(this.sourceFiles, classpath, config);
				String mainClass = "nopol.repair.NopolTestRunner";
				TestExecutorProcessor.createMainTestClass(jpfSpoon, mainClass);
				jpfSpoon.process(new AssertReplacer());

				final File outputSourceFile = new File("src-gen");
				final File outputCompiledFile = new File("target-gen");
				// generate the output file
				jpfSpoon.generateOutputFile(outputSourceFile);
				jpfSpoon.generateOutputCompiledFile(outputCompiledFile);
			} catch (IOException e) {
				throw new RuntimeException("Unable to write transformed test", e);
			}
		}
		return solveWithMultipleBuild(this.localizer.getTestListPerStatement());
	}


	/*
	 * First algorithm of Nopol,
	 * build the initial model
	 * apply only one modification
	 * build
	 * try to find patch
	 */
	private List<Patch> solveWithMultipleBuild(Map<SourceLocation, List<TestResult>> testListPerStatement) {
		List<Patch> patches = new ArrayList<>();
		for (SourceLocation sourceLocation : testListPerStatement.keySet()) {
			patches.addAll(runOnStatement(sourceLocation, testListPerStatement.get(sourceLocation)));
			if (config.isOnlyOneSynthesisResult() && !patches.isEmpty()) {
				return patches;
			}
		}
		return patches;
	}

	private List<Patch> runOnStatement(SourceLocation sourceLocation, List<TestResult> tests) {
		List<Patch> patches = new ArrayList<>();
		logger.debug("Analysing {}", sourceLocation);
		SpoonedClass spoonCl = spooner.forked(sourceLocation.getRootClassName());
		if (spoonCl == null || spoonCl.getSimpleType() == null) {
			return patches;
		}
		NopolProcessorBuilder builder = new NopolProcessorBuilder(spoonCl.getSimpleType().getPosition().getFile(), sourceLocation.getLineNumber(), config);
		try {
			spoonCl.process(builder);
		} catch (DynamicCompilationException ignored) {
			logger.debug("Aborting: dynamic compilation failed");
			return patches;
		}
		final List<NopolProcessor> nopolProcessors = builder.getNopolProcessors();
		for (NopolProcessor nopolProcessor : nopolProcessors) {
			patches.addAll(executeNopolProcessor(tests, sourceLocation, spoonCl, nopolProcessor));
			if (config.isOnlyOneSynthesisResult() && !patches.isEmpty()) {
				return patches;
			}
		}
		return patches;
	}

	/**
	 * Method used as proxy for runNopolProcessor to handle timeout
	 */
	private List<Patch> executeNopolProcessor(final List<TestResult> tests, final SourceLocation sourceLocation, final SpoonedClass spoonCl, final NopolProcessor nopolProcessor) {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future nopolExecution = executor.submit(
				new Callable() {
					@Override
					public Object call() throws Exception {
						return runNopolProcessor(tests, sourceLocation, spoonCl, nopolProcessor);
					}
				});
		try {
			return (List) nopolExecution.get(config.getMaxTimeEachTypeOfFixInMinutes(), TimeUnit.MINUTES);
		} catch (ExecutionException | InterruptedException | TimeoutException exception) {
			LoggerFactory.getLogger(Main.class).error("Timeout: execution time > " + config.getMaxTimeInMinutes() + " " + TimeUnit.MINUTES, exception);
			return Collections.emptyList();
		}
	}

	private List<Patch> runNopolProcessor(List<TestResult> tests, SourceLocation sourceLocation, SpoonedClass spoonCl, NopolProcessor nopolProcessor) {
		AngelicValue angelicValue;
		List<Patch> patches = new ArrayList<>();
		try {
			angelicValue = buildConstraintsModelBuilder(nopolProcessor, sourceLocation, spooner);
		} catch (UnsupportedOperationException | DynamicCompilationException ignored) {
			return patches;
		}
		Synthesizer synth = SynthesizerFactory.build(sourceFiles, spooner, config, sourceLocation, nopolProcessor, angelicValue, spoonCl);
		if (synth == Synthesizer.NO_OP_SYNTHESIZER) {
			return patches;
		}
		Collection<TestCase> failingTest = reRunFailingTestCases(getFailingTestCase(tests), new URLClassLoader(classpath));
		if (failingTest.isEmpty()) {
			return patches;
		}
		List<Patch> tmpPatches = synth.buildPatch(classpath, tests, failingTest, config.getMaxTimeBuildPatch());
		for (int i = 0; i < tmpPatches.size(); i++) {
			Patch patch = tmpPatches.get(i);
			if (isOk(patch, tests, synth.getProcessor())) {
				patches.add(patch);
				if (config.isOnlyOneSynthesisResult()) {
					return patches;
				}
			} else {
				logger.debug("Could not find a patch in {}", sourceLocation);
			}
		}
		return patches;
	}

	private AngelicValue buildConstraintsModelBuilder(NopolProcessor nopolProcessor, SourceLocation statement, SpoonedFile spoonCl) {
		if (Boolean.class.equals(config.getType().getType())) {
			RuntimeValues<Boolean> runtimeValuesInstance = RuntimeValues.newInstance();
			switch (config.getOracle()) {
				case ANGELIC:
					Processor<CtStatement> processor = new ConditionalLoggingInstrumenter(runtimeValuesInstance, nopolProcessor);
					return new ConstraintModelBuilder(runtimeValuesInstance, statement, processor, spooner, config);
				case SYMBOLIC:
					return new JPFRunner<>(runtimeValuesInstance, statement, nopolProcessor, spoonCl, spooner, config);
			}
		}
		throw new UnsupportedOperationException();
	}

	private boolean isOk(Patch newRepair, List<TestResult> testClasses, NopolProcessor processor) {
		logger.trace("Suggested patch: {}", newRepair);
		try {
			return testPatch.passesAllTests(newRepair, testClasses, processor);
		} catch (DynamicCompilationException e) {
			logger.error("Patch malformed " + newRepair.asString(), e);
			return false;
		}
	}

	private String[] getFailingTestCase(List<TestResult> tests) {
		Set<String> failingClassTest = new HashSet<>();
		for (int i = 0; i < tests.size(); i++) {
			TestResult testResult = tests.get(i);
			if (!testResult.isSuccessful()) {
				failingClassTest.add(testResult.getTestCase().className());
			}
		}
		return failingClassTest.toArray(new String[0]);
	}

	private Collection<TestCase> reRunFailingTestCases(String[] testClasses, ClassLoader testClassLoader) {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(testClasses, testClassLoader, listener, this.config);
		return listener.failedTests();
	}

	public SpoonedProject getSpooner() {
		return spooner;
	}

}
