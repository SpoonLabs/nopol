package fr.inria.lille.infinitel;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;
import static fr.inria.lille.infinitel.InfinitelConfiguration.iterationsThreshold;
import static java.lang.String.format;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.spoon.SpoonClassLoaderFactory;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.synthesis.ConstraintBasedSynthesis;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.infinitel.loop.CentralLoopMonitor;
import fr.inria.lille.infinitel.loop.FixableLoop;
import fr.inria.lille.infinitel.loop.FixableLoopSelection;
import fr.inria.lille.infinitel.loop.LoopSpecificationCollector;
import fr.inria.lille.infinitel.loop.LoopUnroller;
import fr.inria.lille.infinitel.loop.MonitoringTestExecutor;

/** Infinite Loops Repair */

public class Infinitel {

	public static void run(File sourceFile, URL[] classFolders) {
		Infinitel infiniteLoopFixer = new Infinitel(sourceFile, classFolders);
		infiniteLoopFixer.repair();
	}
	
	public Infinitel(File sourceFile, URL[] classpath) {
		this(new ProjectReference(sourceFile, classpath));
	}
	
	public Infinitel(ProjectReference project) {
		this.project = project;
	}
	
	public ProjectReference project() {
		return project;
	}
	
	public void repair() {
		MonitoringTestExecutor testExecutor = newTestExecutor();
		Collection<FixableLoop> infiniteLoops = fixableLoops(testExecutor);
		for (FixableLoop loop : infiniteLoops) {
			findRepairIn(loop, testExecutor);
		}
	}
	
	protected MonitoringTestExecutor newTestExecutor() {
		CentralLoopMonitor monitor = new CentralLoopMonitor(iterationsThreshold());
		ClassLoader classLoader = loaderWithInstrumentedClasses(monitor);
		MonitoringTestExecutor testExecutor = new MonitoringTestExecutor(classLoader, monitor);
		return testExecutor;
	}
	
	protected ClassLoader loaderWithInstrumentedClasses(CentralLoopMonitor monitor) {
		logDebug(logger, "# Instrumenting project classes");
		SpoonClassLoaderFactory spooner = new SpoonClassLoaderFactory(project().sourceFile(), monitor);
		ClassLoader loader = spooner.classLoaderProcessing(spooner.modelledClasses(), project().classpath());
		logDebug(logger, "# Classes were instrumented and compiled successfully");
		return loader;
	}

	protected Collection<FixableLoop> fixableLoops(MonitoringTestExecutor testExecutor) {
		TestCasesListener listener = new TestCasesListener();
		Collection<SourcePosition> loopsAboveThreshold = infiniteLoops(testExecutor, listener);
		Collection<FixableLoop> fixableLoops = fixableInfiniteLoops(testExecutor, loopsAboveThreshold, listener);
		return fixableLoops;
	}
	
	protected Collection<SourcePosition> infiniteLoops(MonitoringTestExecutor testExecutor, TestCasesListener listener) {
		String[] testClasses = project().testClasses();
		logDebug(logger, "# Running test cases to find infinite loops");
		Collection<SourcePosition> loopsAboveThreshold = testExecutor.loopsAboveThresholdFor(testClasses, listener);
		logDebug(logger, "# Number of infinite loops: " + loopsAboveThreshold.size());
		return loopsAboveThreshold;
	}
	
	protected Collection<FixableLoop> fixableInfiniteLoops(MonitoringTestExecutor testExecutor, Collection<SourcePosition> loopsAboveThreshold, TestCasesListener listener) {
		return FixableLoopSelection.selection(testExecutor, loopsAboveThreshold, listener.failedTests(), listener.successfulTests());
	}
	
	private void findRepairIn(FixableLoop loop, MonitoringTestExecutor testExecutor) {
		Map<TestCase, Integer> testsAndThresholds = testsAndThresholds(loop.position(), testExecutor, loop.failingTests(), loop.passingTests());
		Collection<Specification<Boolean>> testSpecifications = testSpecifications(testsAndThresholds, testExecutor, loop.position());
		synthesiseCodeFor(testSpecifications);
	}
	
	protected Map<TestCase, Integer> testsAndThresholds(SourcePosition loop, MonitoringTestExecutor executor, Collection<TestCase> failed, Collection<TestCase> passed) {
		logDebug(logger, "# Finding iteration thresholds for each test");
		LoopUnroller unroller = new LoopUnroller(executor);
		Map<TestCase, Integer> thresholds = unroller.correctIterationsByTestIn(loop, failed, passed);
		logDebug(logger, format("# Found thresholds for %d tests which use the loop (%s) only once", thresholds.size(), loop));
		return thresholds;
	}
	
	protected Collection<Specification<Boolean>> testSpecifications(Map<TestCase, Integer> testsAndThresholds, MonitoringTestExecutor testExecutor, SourcePosition loopPosition) {
		logDebug(logger, "# Running each test individually to colllect runtime values");
		LoopSpecificationCollector collector = new LoopSpecificationCollector(testExecutor);
		Collection<Specification<Boolean>> testSpecifications = collector.testSpecifications(testsAndThresholds, loopPosition);
		logDebug(logger, "# Finished runtime value collection");
		return testSpecifications;
	}
	
	protected CodeGenesis synthesiseCodeFor(Collection<Specification<Boolean>> specifications) {
		logDebug(logger, "# Code synthesis begins");
		ConstraintBasedSynthesis synthesis = new ConstraintBasedSynthesis();
		CodeGenesis synthesisedCode = synthesis.codesSynthesisedFrom(Boolean.class, specifications);
		if (synthesisedCode.isSuccessful()) {
			logDebug(logger, "# Code synthesis completed successfully", "A working looping condition is:", synthesisedCode.returnStatement());
		} else {
			logDebug(logger, "# Code synthesis failed");
		}
		return synthesisedCode;
	}
	
	private ProjectReference project;
	private static Logger logger = newLoggerFor(Infinitel.class);
}
