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
import fr.inria.lille.commons.synthesis.ConstraintBasedSynthesis;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.localization.DumbFaultLocalizerImpl;
import fr.inria.lille.localization.FaultLocalizer;
import fr.inria.lille.localization.GZoltarFaultLocalizer;
import fr.inria.lille.localization.OchiaiFaultLocalizer;
import fr.inria.lille.localization.TestResult;
import fr.inria.lille.repair.common.BottomTopURLClassLoader;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.finder.TestClassesFinder;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.patch.TestPatch;
import fr.inria.lille.repair.nopol.spoon.ConditionalLoggingInstrumenter;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.nopol.spoon.NopolProcessorBuilder;
import fr.inria.lille.repair.nopol.spoon.symbolic.AssertReplacer;
import fr.inria.lille.repair.nopol.spoon.symbolic.TestExecutorProcessor;
import fr.inria.lille.repair.nopol.synth.AngelicValue;
import fr.inria.lille.repair.nopol.synth.ConstraintModelBuilder;
import fr.inria.lille.repair.nopol.synth.JPFRunner;
import fr.inria.lille.repair.nopol.synth.SMTNopolSynthesizer;
import fr.inria.lille.repair.nopol.synth.Synthesizer;
import fr.inria.lille.repair.nopol.synth.SynthesizerFactory;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.Processor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import xxl.java.compiler.DynamicCompilationException;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
	private NopolContext nopolContext;
	private NopolResult nopolResult;



	public NoPol(NopolContext nopolContext) {
		this.startTime = System.currentTimeMillis();
		this.nopolContext = nopolContext;
		this.classpath = nopolContext.getProjectClasspath();
		this.sourceFiles = nopolContext.getProjectSources();
		this.nopolResult = new NopolResult(nopolContext, this.startTime);

		StatementType type = nopolContext.getType();
		logger.info("Source files: " + Arrays.toString(sourceFiles));
		logger.info("Classpath: " + Arrays.toString(classpath));
		logger.info("Statement type: " + type);
		logger.info("Args: " + Arrays.toString(nopolContext.getProjectTests()));
		logger.info("Config: " + nopolContext);
		this.logSystemInformation();

		this.spooner = new SpoonedProject(this.sourceFiles, nopolContext);
		this.testClasses = nopolContext.getProjectTests();
		this.testPatch = new TestPatch(this.sourceFiles[0], this.spooner, nopolContext);
	}

	/**
	 * This getter should only be used after an error on build() method (e.g. after a timeout), to get a partial result informations.
	 * @return
	 */
	public NopolResult getNopolResult() {
		return nopolResult;
	}

	public NopolResult build() {
		if (this.testClasses == null) {
			this.testClasses = new TestClassesFinder().findIn(classpath, false);
		}

		this.localizer = this.createLocalizer();

		nopolResult.setNbTests(this.testClasses.length);
		if (nopolContext.getOracle() == NopolContext.NopolOracle.SYMBOLIC) {
			try {
				SpoonedProject jpfSpoon = new SpoonedProject(this.sourceFiles, nopolContext);
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
		Map<SourceLocation, List<TestResult>> testListPerStatement = this.localizer.getTestListPerStatement();

		this.nopolResult.setNbStatements(testListPerStatement.keySet().size());
		solveWithMultipleBuild(testListPerStatement);

		this.logResultInfo(this.nopolResult.getPatches());

		this.nopolResult.setDurationInMilliseconds(System.currentTimeMillis()-this.startTime);

		NopolStatus status;
		if (nopolResult.getPatches().size() > 0) {
			status = NopolStatus.PATCH;
		} else {
			if (nopolResult.getNbAngelicValues() == 0) {
				status = NopolStatus.NO_ANGELIC_VALUE;
			} else {
				status = NopolStatus.NO_SYNTHESIS;
			}
		}

		nopolResult.setNopolStatus(status);

		return this.nopolResult;
	}

	private FaultLocalizer createLocalizer() {
		switch (this.nopolContext.getLocalizer()) {
			case GZOLTAR:
				try {
					return new GZoltarFaultLocalizer(this.nopolContext);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			case DUMB:
				return new DumbFaultLocalizerImpl(this.nopolContext);
			case COCOSPOON: // default
			default:
				return new OchiaiFaultLocalizer(this.nopolContext);
		}
	}

	/*
	 * First algorithm of Nopol,
	 * build the initial model
	 * apply only one modification
	 * build
	 * try to find patch
	 */
	private void solveWithMultipleBuild(Map<SourceLocation, List<TestResult>> testListPerStatement) {
		for (SourceLocation sourceLocation : testListPerStatement.keySet()) {
			runOnStatement(sourceLocation, testListPerStatement.get(sourceLocation));
			if (nopolContext.isOnlyOneSynthesisResult() && !this.nopolResult.getPatches().isEmpty()) {
				return;
			}
		}
	}

	private void runOnStatement(SourceLocation sourceLocation, List<TestResult> tests) {
		logger.debug("Analysing {} which is executed by {} tests", sourceLocation, tests.size());
		SpoonedClass spoonCl = spooner.forked(sourceLocation.getRootClassName());
		if (spoonCl == null || spoonCl.getSimpleType() == null) {
			return;
		}
		NopolProcessorBuilder builder = new NopolProcessorBuilder(spoonCl.getSimpleType().getPosition().getFile(), sourceLocation.getLineNumber(), nopolContext);
		try {
			spoonCl.process(builder);
		} catch (DynamicCompilationException ignored) {
			logger.debug("Aborting: dynamic compilation failed");
			return;
		}

		final List<NopolProcessor> nopolProcessors = builder.getNopolProcessors();
		for (NopolProcessor nopolProcessor : nopolProcessors) {
			SourcePosition position = nopolProcessor.getTarget().getPosition();
			sourceLocation.setSourceStart(position.getSourceStart());
			sourceLocation.setSourceEnd(position.getSourceEnd());

			List<Patch> patches = executeNopolProcessor(tests, sourceLocation, spoonCl, nopolProcessor);
			this.nopolResult.addPatches(patches);

			if (nopolContext.isOnlyOneSynthesisResult() && !patches.isEmpty()) {
				return;
			}
		}
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
			executor.shutdown();
			return (List) nopolExecution.get(nopolContext.getMaxTimeEachTypeOfFixInMinutes(), TimeUnit.MINUTES);
		} catch (ExecutionException | InterruptedException | TimeoutException exception) {
			LoggerFactory.getLogger(this.getClass()).error("Timeout: execution time > " + nopolContext.getMaxTimeEachTypeOfFixInMinutes() + " " + TimeUnit.MINUTES, exception);
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

		if (angelicValue != null) {
			this.nopolResult.incrementNbAngelicValues();
		}

		Synthesizer synth = SynthesizerFactory.build(sourceFiles, spooner, nopolContext, sourceLocation, nopolProcessor, angelicValue, spoonCl);
		if (synth == Synthesizer.NO_OP_SYNTHESIZER) {
			return patches;
		}
		Collection<TestCase> failingTest = reRunFailingTestCases(getFailingTestCase(tests), classpath);
		if (failingTest.isEmpty()) {
			return patches;
		}
		List<Patch> tmpPatches = synth.buildPatch(classpath, tests, failingTest, nopolContext.getMaxTimeBuildPatch());

		if (tmpPatches.size() > 0) {
			for (int i = 0; i < tmpPatches.size(); i++) {
				Patch patch = tmpPatches.get(i);
				if (nopolContext.isSkipRegressionStep() || isOk(patch, tests, synth.getProcessor())) {
					patches.add(patch);
					if (nopolContext.isOnlyOneSynthesisResult()) {
						return patches;
					}
				} else {
					logger.debug("Could not find a patch in {}", sourceLocation);
				}
			}
		}

		return patches;
	}

	private AngelicValue buildConstraintsModelBuilder(NopolProcessor nopolProcessor, SourceLocation statement, SpoonedFile spoonCl) {
		if (Boolean.class.equals(nopolContext.getType().getType())) {
			RuntimeValues<Boolean> runtimeValuesInstance = RuntimeValues.newInstance();
			switch (nopolContext.getOracle()) {
				case ANGELIC:
					Processor<CtStatement> processor = new ConditionalLoggingInstrumenter(runtimeValuesInstance, nopolProcessor);
					return new ConstraintModelBuilder(runtimeValuesInstance, statement, processor, spooner, nopolContext);
				case SYMBOLIC:
					return new JPFRunner<>(runtimeValuesInstance, statement, nopolProcessor, spoonCl, spooner, nopolContext);
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

	private Collection<TestCase> reRunFailingTestCases(String[] testClasses, URL[] deps) {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(testClasses, new BottomTopURLClassLoader(deps, Thread.currentThread().getContextClassLoader()), listener, this.nopolContext);
		return listener.failedTests();
	}

	public SpoonedProject getSpooner() {
		return spooner;
	}

	public FaultLocalizer getLocalizer() {
		return localizer;
	}

	private void logSystemInformation() {
		this.logger.info("Available processors (cores): " + Runtime.getRuntime().availableProcessors());

    	/* Total amount of free memory available to the JVM */
		this.logger.info("Free memory: " + FileUtils.byteCountToDisplaySize(Runtime.getRuntime().freeMemory()));

    	/* This will return Long.MAX_VALUE if there is no preset limit */
		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		this.logger.info("Maximum memory: " +
				(maxMemory == Long.MAX_VALUE ? "no limit" : FileUtils.byteCountToDisplaySize(maxMemory)));

    	/* Total memory currently available to the JVM */
		this.logger.info("Total memory available to JVM: " +
				FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory()));

		this.logger.info("Java version: " + Runtime.class.getPackage().getImplementationVersion());
		this.logger.info("JAVA_HOME: " + System.getenv("JAVA_HOME"));
		this.logger.info("PATH: " + System.getenv("PATH"));
	}

	private void logResultInfo(List<Patch> patches) {
		long durationTime = System.currentTimeMillis()-this.startTime;
		this.logger.info("----INFORMATION----");
		List<CtType<?>> allClasses = this.getSpooner().spoonFactory().Class().getAll();
		int nbMethod = 0;
		for (int i = 0; i < allClasses.size(); i++) {
			CtType<?> ctSimpleType = allClasses.get(i);
			if (ctSimpleType instanceof CtClass) {
				Set methods = ((CtClass) ctSimpleType).getMethods();
				nbMethod += methods.size();
			}
		}

		this.logger.info("Nb classes : " + allClasses.size());
		this.logger.info("Nb methods : " + nbMethod);
		if (NoPol.currentStatement != null) {
			BitSet coverage = NoPol.currentStatement.getCoverage();
			int countStatementSuccess = 0;
			int countStatementFailed = 0;
			int nextTest = coverage.nextSetBit(0);
			/*while (nextTest != -1) {
				TestResultImpl testResult = nopol.getgZoltar().getGzoltar().getTestResults().get(nextTest);
				if (testResult.wasSuccessful()) {
					countStatementSuccess += testResult.getCoveredComponents().size();
				} else {
					countStatementFailed += testResult.getCoveredComponents().size();
				}
				nextTest = coverage.nextSetBit(nextTest + 1);
			}*/

			this.logger.info("Nb statement executed by the passing tests of the patched line: " + countStatementSuccess);
			this.logger.info("Nb statement executed by the failing tests of the patched line: " + countStatementFailed);
		}

		this.logger.info("Nb Statements Analyzed : " + SynthesizerFactory.getNbStatementsAnalysed());
		this.logger.info("Nb Statements with Angelic Value Found : " + SMTNopolSynthesizer.getNbStatementsWithAngelicValue());
		if (nopolContext.getSynthesis() == NopolContext.NopolSynthesis.SMT) {
			this.logger.info("Nb inputs in SMT : " + SMTNopolSynthesizer.getDataSize());
			this.logger.info("Nb SMT level: " + ConstraintBasedSynthesis.level);
			if (ConstraintBasedSynthesis.operators != null) {
				this.logger.info("Nb SMT components: [" + ConstraintBasedSynthesis.operators.size() + "] " + ConstraintBasedSynthesis.operators);
				Iterator<Operator<?>> iterator = ConstraintBasedSynthesis.operators.iterator();
				Map<Class, Integer> mapType = new HashMap<>();
				while (iterator.hasNext()) {
					Operator<?> next = iterator.next();
					if (!mapType.containsKey(next.type())) {
						mapType.put(next.type(), 1);
					} else {
						mapType.put(next.type(), mapType.get(next.type()) + 1);
					}
				}
				for (Iterator<Class> patchIterator = mapType.keySet().iterator(); patchIterator.hasNext(); ) {
					Class next = patchIterator.next();
					this.logger.info("                  " + next + ": " + mapType.get(next));
				}
			}

			this.logger.info("Nb variables in SMT : " + SMTNopolSynthesizer.getNbVariables());
		}
		//this.logger.info("Nb run failing test  : " + nbFailingTestExecution);
		//this.logger.info("Nb run passing test : " + nbPassedTestExecution);

		this.logger.info("NoPol Execution time : " + durationTime + "ms");

		if (patches != null && !patches.isEmpty()) {
			this.logger.info("----PATCH FOUND----");
			for (int i = 0; i < patches.size(); i++) {
				Patch patch = patches.get(i);
				this.logger.info(patch.asString());
				this.logger.info("Nb test that executes the patch: " + this.getLocalizer().getTestListPerStatement().get(patch.getSourceLocation()).size());
				this.logger.info(String.format("%s:%d: %s", patch.getSourceLocation().getContainingClassName(), patch.getLineNumber(), patch.getType()));
				String diffPatch = patch.toDiff(this.getSpooner().spoonFactory(), nopolContext);
				this.logger.info(diffPatch);

				if (nopolContext.getOutputFolder() != null) {
					File patchLocation = new File(nopolContext.getOutputFolder() + "/patch_" + (i + 1) + ".diff");
					try{
						PrintWriter writer = new PrintWriter(patchLocation, "UTF-8");
						writer.print(diffPatch);
						writer.close();
					} catch (IOException e) {
						System.err.println("Unable to write the patch: " + e.getMessage());
					}
				}
			}
		}
		if (nopolContext.isJson()) {
			JSONObject output = new JSONObject();

			output.put("nb_classes", allClasses.size());
			output.put("nb_methods", nbMethod);
			output.put("nbStatement", SynthesizerFactory.getNbStatementsAnalysed());
			output.put("nbAngelicValue", SMTNopolSynthesizer.getNbStatementsWithAngelicValue());
			//output.put("nb_failing_test", nbFailingTestExecution);
			//output.put("nb_passing_test", nbPassedTestExecution);
			output.put("executionTime", durationTime);
			output.put("date", new Date());
			if (patches != null) {
				for (int i = 0; i < patches.size(); i++) {
					Patch patch = patches.get(i);

					JSONObject patchOutput = new JSONObject();

					JSONObject locationOutput = new JSONObject();
					locationOutput.put("class", patch.getSourceLocation().getContainingClassName());
					locationOutput.put("line", patch.getLineNumber());
					patchOutput.put("patchLocation", locationOutput);
					patchOutput.put("patchType", patch.getType());
					patchOutput.put("nb_test_that_execute_statement", this.getLocalizer().getTestListPerStatement().get(patch.getSourceLocation()).size());
					patchOutput.put("patch", patch.toDiff(this.getSpooner().spoonFactory(), nopolContext));

					output.append("patch", patchOutput);
				}
			}

			try (FileWriter writer = new FileWriter(nopolContext.getOutputFolder() + "/output.json")) {
				output.write(writer);
				writer.close();
			} catch (IOException ignore) {
			}
		}
	}
}
