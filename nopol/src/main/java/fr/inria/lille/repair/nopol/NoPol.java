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
import com.gzoltar.core.components.count.ComponentCount;
import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.localization.StatementExt;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.TestClassesFinder;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.patch.TestPatch;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.nopol.spoon.symbolic.AssertReplacer;
import fr.inria.lille.repair.nopol.spoon.symbolic.TestExecutorProcessor;
import fr.inria.lille.repair.nopol.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.repair.nopol.synth.Synthesizer;
import fr.inria.lille.repair.nopol.synth.SynthesizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static fr.inria.lille.repair.common.patch.Patch.NO_PATCH;

/**
 * @author Favio D. DeMarco
 */
public class NoPol {

	public static Statement currentStatement;
	private URL[] classpath;
	private GZoltarSuspiciousProgramStatements gZoltar;
	private final TestPatch testPatch;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonedProject spooner;
	private final File[] sourceFiles;
	private static boolean singlePatch = true;
	private String[] testClasses;
	public long startTime;
	private Config config;

	public NoPol(ProjectReference project, Config config) {
		this.config = config;
		this.classpath = project.classpath();
		this.sourceFiles = project.sourceFiles();
		this.testClasses = project.testClasses();

		// get all test classes of the current project
		/*if (this.testClasses == null || this.testClasses.length == 0) {
			this.testClasses = new TestClassesFinder().findIn(classpath, false);
		}*/
		spooner = new SpoonedProject(this.sourceFiles, classpath, config);
		testPatch = new TestPatch(sourceFiles[0], spooner, config);
		startTime = System.currentTimeMillis();
	}

	public NoPol(final File[] sourceFiles, final URL[] classpath, Config config) {
		this(new ProjectReference(sourceFiles, classpath), config);
	}

	public List<Patch> build() {
		this.testClasses = new TestClassesFinder().findIn(classpath, false);
		return build(testClasses);
	}

	public List<Patch> build(String[] testClasses) {
		gZoltar = GZoltarSuspiciousProgramStatements.create(this.classpath, testClasses);
		Collection<Statement> statements = gZoltar.sortBySuspiciousness(testClasses);
		if (statements.isEmpty()) {
			throw new RuntimeException("No suspicious statements found.");
		}

		Map<SourceLocation, List<TestResult>> testListPerStatement = getTestListPerStatement();

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

		return solveWithMultipleBuild(statements, testListPerStatement);
	}

	private Map<SourceLocation, List<TestResult>> getTestListPerStatement() {
		Map<SourceLocation, List<TestResult>> results = new HashMap<>();
		List<TestResult> testResults = gZoltar.getGzoltar().getTestResults();
		for (TestResult testResult : testResults) {
			List<ComponentCount> components = testResult.getCoveredComponents();
			for (ComponentCount component1 : components) {
				Statement component = (Statement) component1.getComponent();
				SourceLocation sourceLocation = new SourceLocation(component.getMethod().getParent().getLabel(), component.getLineNumber());
				if (!results.containsKey(sourceLocation)) {
					results.put(sourceLocation, new ArrayList<TestResult>());
				}
				results.get(sourceLocation).add(testResult);
			}
		}
		return results;
	}

	/*
	 * First algorithm of Nopol,
	 * build the initial model
	 * apply only one modification
	 * build
	 * try to find patch
	 */
	private List<Patch> solveWithMultipleBuild(Collection<Statement> statements, Map<SourceLocation, List<TestResult>> testListPerStatement) {
		List<Patch> patches = new ArrayList<>();
		for (Iterator<Statement> iterator = statements.iterator(); iterator.hasNext() &&
				// limit the execution time
				System.currentTimeMillis() - startTime <= TimeUnit.MINUTES.toMillis(config.getMaxTime()); ) {
			Statement statement = iterator.next();
			if (((StatementExt) statement).getEf() == 0) {
				continue;
			}
			if (((StatementExt) statement).getNf() != 0) {
				continue;
			}
			try {
				if (isInTest(statement))
					continue;
				NoPol.currentStatement = statement;
				logger.debug("Analysing {}", statement);
				SourceLocation sourceLocation = new SourceLocation(statement.getMethod().getParent().getName(), statement.getLineNumber());
				Synthesizer synth = new SynthesizerFactory(sourceFiles, spooner, config).getFor(sourceLocation);

				if (synth == Synthesizer.NO_OP_SYNTHESIZER) {
					continue;
				}

				List<TestResult> tests = testListPerStatement.get(sourceLocation);


				Set<String> failingClassTest = new HashSet<>();
				for (int i = 0; i < tests.size(); i++) {
					TestResult testResult = tests.get(i);
					if (!testResult.wasSuccessful()) {
						failingClassTest.add(testResult.getName().split("#")[0]);
					}
				}
				Collection<TestCase> failingTest = failingTests(failingClassTest.toArray(new String[0]), new URLClassLoader(classpath));

				if (failingTest.isEmpty()) {
					continue;
				}
				Patch patch = synth.buildPatch(classpath, tests, failingTest, config.getMaxTimeBuildPatch());
				if (isOk(patch, gZoltar.getGzoltar().getTestResults(), synth.getProcessor())) {
					patches.add(patch);
					if (isSinglePatch()) {
						break;
					}
				} else {
					logger.debug("Could not find a patch in {}", statement);
				}
			} catch (RuntimeException re) {
				re.printStackTrace();
			}
		}
		return patches;
	}

	private boolean isInTest(Statement statement) {
		return statement.getMethod().getParent().getName().contains("Test");
	}

	private boolean isOk(Patch newRepair, List<TestResult> testClasses, NopolProcessor processor) {
		if (newRepair == NO_PATCH) {
			return false;
		}
		logger.trace("Suggested patch: {}", newRepair);
		return testPatch.passesAllTests(newRepair, testClasses, processor);
	}

	public static boolean isSinglePatch() {
		return singlePatch;
	}

	public static boolean setSinglePatch(boolean singlePatch) {
		return NoPol.singlePatch = singlePatch;
	}

	/**
	 * returns the list of failing tests
	 *
	 * @param testClasses
	 * @return the list of failing tests
	 */
	private Collection<TestCase> failingTests(String[] testClasses,
											  ClassLoader testClassLoader) {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(testClasses, testClassLoader, listener, this.config);
		return listener.failedTests();
	}

	private Collection<TestCase> failingTests(String[] testClasses) {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(testClasses, spooner.dumpedToClassLoader(), listener, this.config);
		return listener.failedTests();
	}

	/**
	 * Add JPF library to class path
	 *
	 * @param clpath
	 */
	private URL[] addJPFLibraryToCassPath(URL[] clpath) {

		List<URL> classpath = new ArrayList<>();
		for (int i = 0; i < clpath.length; i++) {
			classpath.add(clpath[i]);
		}

		return classpath.toArray(new URL[]{});
	}

	public SpoonedProject getSpooner() {
		return spooner;
	}

	public GZoltarSuspiciousProgramStatements getgZoltar() {
		return gZoltar;
	}
}
