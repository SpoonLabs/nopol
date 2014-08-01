package fr.inria.lille.infinitel;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;
import static java.lang.String.format;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.synthesis.ConstraintBasedSynthesis;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.infinitel.loop.FixableLoop;
import fr.inria.lille.infinitel.loop.FixableLoopBuilder;
import fr.inria.lille.infinitel.loop.While;
import fr.inria.lille.infinitel.mining.LoopTestThresholdFinder;
import fr.inria.lille.infinitel.mining.MonitoringTestExecutor;
import fr.inria.lille.infinitel.mining.MonitoringTestExecutorBuilder;
import fr.inria.lille.infinitel.synthesis.LoopSpecificationCollector;

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
		Collection<FixableLoop> fixableInfiniteLoops = fixableInfiniteLoops(testExecutor);
		for (FixableLoop loop : fixableInfiniteLoops) {
			findRepairIn(loop, testExecutor);
		}
	}
	
	protected MonitoringTestExecutor newTestExecutor() {
		return MonitoringTestExecutorBuilder.buildFor(project(), configuration());
	}
	
	protected Collection<FixableLoop> fixableInfiniteLoops(MonitoringTestExecutor testExecutor) {
		TestCasesListener listener = new TestCasesListener();
		Collection<While> infiniteLoops = infiniteLoops(testExecutor, listener);
		Collection<FixableLoop> fixableLoops = fixableLoops(testExecutor, infiniteLoops, listener);
		return fixableLoops;
	}
	
	protected Collection<While> infiniteLoops(MonitoringTestExecutor testExecutor, TestCasesListener listener) {
		String[] testClasses = project().testClasses();
		logDebug(logger, "# Running test cases to find infinite loops");
		Collection<While> loopsReachingThreshold = testExecutor.loopsReachingThresholdFor(testClasses, listener);
		logDebug(logger, "# Number of infinite loops: " + loopsReachingThreshold.size());
		return loopsReachingThreshold;
	}
	
	protected Collection<FixableLoop> fixableLoops(MonitoringTestExecutor testExecutor, Collection<While> loops, TestCasesListener listener) {
		return FixableLoopBuilder.buildFrom(loops, listener.failedTests(), listener.successfulTests(), testExecutor);
	}
	
	private void findRepairIn(FixableLoop loop, MonitoringTestExecutor testExecutor) {
		Map<TestCase, Integer> thresholdsByTest = thresholdsByTest(loop, testExecutor, loop.failingTests(), loop.passingTests());
		Collection<Specification<Boolean>> testSpecifications = testSpecifications(thresholdsByTest, testExecutor, loop);
		synthesiseCodeFor(testSpecifications);
	}
	
	protected Map<TestCase, Integer> thresholdsByTest(While loop, MonitoringTestExecutor executor, Collection<TestCase> failed, Collection<TestCase> passed) {
		logDebug(logger, "# Finding test thresholds");
		LoopTestThresholdFinder thresholdFinder = new LoopTestThresholdFinder(executor);
		Map<TestCase, Integer> thresholdsByTest = thresholdFinder.thresholdsByTest(loop, failed, passed);
		logDebug(logger, format("# Found test thresholds for %d tests which invoke the loop (%s) only once", thresholdsByTest.size(), loop));
		return thresholdsByTest;
	}
	
	protected Collection<Specification<Boolean>> testSpecifications(Map<TestCase, Integer> thresholdsByTest, MonitoringTestExecutor executor, While loop) {
		logDebug(logger, "# Running each test individually to colllect runtime values");
		LoopSpecificationCollector collector = new LoopSpecificationCollector(executor);
		Collection<Specification<Boolean>> testSpecifications = collector.testSpecifications(thresholdsByTest, loop);
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

	protected InfinitelConfiguration configuration() {
		return InfinitelConfiguration.instance();
	}
	
	private ProjectReference project;
	protected static Logger logger = newLoggerFor(Infinitel.class);
}
