package fr.inria.lille.infinitel;

import static fr.inria.lille.commons.string.StringLibrary.javaNewline;
import static fr.inria.lille.infinitel.InfinitelConfiguration.iterationsThreshold;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.classes.CacheBasedClassLoader;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.spoon.SourceInstrumenter;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.suite.TestSuiteExecution;
import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.synthesis.ConstraintBasedSynthesis;
import fr.inria.lille.commons.trace.LoopIterativeValueCollectorListener;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.infinitel.loop.LoopStatementsMonitor;
import fr.inria.lille.infinitel.loop.LoopUnroller;

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
		monitor = new LoopStatementsMonitor(iterationsThreshold());
		synthesis = new ConstraintBasedSynthesis();
	}
	
	public ProjectReference project() {
		return project;
	}
	
	public void repair() {
		log("# Starting repair process");
		ClassLoader classLoader = loaderWithInstrumentedClasses();
		TestCasesListener listener = new TestCasesListener();
		Collection<SourcePosition> infiniteLoops = infiniteLoopsRunningTests(classLoader, listener);
		for (SourcePosition loopPosition : infiniteLoops) {
			findRepairIn(loopPosition, classLoader, listener.successfulTests(), listener.failedTests());
		}
	}
	
	protected ClassLoader loaderWithInstrumentedClasses() {
		log("- Instrumenting project classes");
		SourceInstrumenter instrumenter = new SourceInstrumenter(project());
		Map<String, Class<?>> processedClassCache = instrumenter.instrumentedWith(monitor());
		return new CacheBasedClassLoader(project().classpath(), processedClassCache);
	}

	protected Collection<SourcePosition> infiniteLoopsRunningTests(ClassLoader classLoader, TestCasesListener listener) {
		log("- Running test cases to find infinite loops");
		TestSuiteExecution.runCasesIn(project().testClasses(), classLoader, listener);
		Collection<SourcePosition> loopsAboveThreshold = monitor().loopsAboveThreshold();
		log("-- Failing tests: " + listener.failedTests());
		log("-- Number of infinite loops: " + loopsAboveThreshold.size());
		return loopsAboveThreshold;
	}
	
	protected void findRepairIn(SourcePosition loopPosition, ClassLoader classLoader, Collection<TestCase> passedTests, Collection<TestCase> failures) {
		log("# Finding repair in " + loopPosition);
		LoopIterativeValueCollectorListener loopListener = new LoopIterativeValueCollectorListener();
		LoopUnroller unroller = new LoopUnroller(monitor(), classLoader, loopListener);
		Map<TestCase, Integer> thresholds = unroller.numberOfIterationsByTestIn(loopPosition, passedTests, failures);
		log("- Number of iterations for each test:" + javaNewline() + thresholds);
		synthesiseCodeFor(loopListener.specifications());
	}
	
	protected CodeGenesis synthesiseCodeFor(Collection<Specification<Boolean>> specifications) {
		log("- Code synthesis begins");
		CodeGenesis synthesisedCode = synthesis().codesSynthesisedFrom(Boolean.class, specifications);
		if (synthesisedCode.isSuccessful()) {
			log("-- Code synthesis completed successfully. A working looping condition is:");
			log(synthesisedCode.returnStatement());
		} else {
			log("-- Code synthesis failed");
		}
		return synthesisedCode;
	}

	protected LoopStatementsMonitor monitor() {
		return monitor;
	}
	
	private ConstraintBasedSynthesis synthesis() {
		return synthesis;
	}
	
	protected static void log(String message) {
		logger.debug(message);
	}
	
	private ProjectReference project;
	private LoopStatementsMonitor monitor;
	private ConstraintBasedSynthesis synthesis;
	private static Logger logger = LoggerFactory.getLogger(Infinitel.class);
}
