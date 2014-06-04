package fr.inria.lille.infinitel;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.infinitel.loop.InfiniteLoopDetector;
import fr.inria.lille.infinitel.loop.LoopUnroller;

/** Infinite Loops Repair */

public class Infinitel {

	public static void run(File sourceFolder, Collection<URL> classFolders) {
		Infinitel infiniteLoopFixer = new Infinitel(sourceFolder, classFolders);
		infiniteLoopFixer.repair();
		infiniteLoopFixer.showSummary();
	}
	
	public Infinitel(File sourceFolder, Collection<URL> classpath) {
		project = new ProjectReference(sourceFolder, classpath);
	}

	public void repair() {
		log("Starting repair process: search of infinite loops");
		TestCasesListener listener = new TestCasesListener();
		Number threshold = InfinitelConfiguration.iterationsThreshold();
		Collection<SourcePosition> infiniteLoopPositions = infiniteLoopPositions(listener, threshold);
		LoopUnroller unroller = new LoopUnroller(project(), threshold);
		for (SourcePosition position : infiniteLoopPositions) {
			Map<TestCase, Integer> thresholds = unroller.thresholdForEach(listener.successfulTests(), listener.failedTests(), position);
			log(thresholds.toString());
		}
	}

	private Collection<SourcePosition> infiniteLoopPositions(TestCasesListener failListener, Number threshold) {
		InfiniteLoopDetector detector = new InfiniteLoopDetector(project(), threshold);
		Collection<SourcePosition> infiniteLoops = detector.detectedLoopsRunning(project().testClasses(), failListener);
		log("Infinite loop locations: " + infiniteLoops);
		return infiniteLoops;
	}

	public void showSummary() {
		log("<end>");
	}
	
	protected ProjectReference project() {
		return project;
	}
	
	protected static void log(String message) {
		logger.debug(message);
	}

	private ProjectReference project;
	private static Logger logger = LoggerFactory.getLogger(Infinitel.class);
}
