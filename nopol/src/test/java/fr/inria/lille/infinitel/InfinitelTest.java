package fr.inria.lille.infinitel;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.Pair;
import fr.inria.lille.commons.io.FileHandler;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.trace.LoopIterativeValueCollectorListener;
import fr.inria.lille.infinitel.loop.LoopUnroller;

public class InfinitelTest {
	
	@Test
	public void example1LoopDetector() {
		int exampleNumber = 1;
		Infinitel infinitel = loopFixerForExample(exampleNumber);
		ClassLoader classLoader = infinitel.loaderWithInstrumentedClasses();
		Map<String, Integer> expected = expectedThresholdsMap(exampleNumber, asList("test1", "test2", "test3", "test4", "testNegative"), asList(0, 1, 2, 3, 4));
		
		Pair<SourcePosition, TestCasesListener> pair = checkInfiniteLoop(infinitel, classLoader, 8);
		SourcePosition loopPosition = pair.first();
		TestCasesListener listener = pair.second();
		Pair<Collection<TestCase>, Collection<TestCase>> checkedTests = checkTests(infinitel, classLoader, loopPosition, listener, 1, 4);
		Collection<TestCase> passingTests = checkedTests.first();
		Collection<TestCase> failingTests = checkedTests.second();
		LoopIterativeValueCollectorListener specificationListener = checkThresholds(infinitel, classLoader, loopPosition, passingTests, failingTests, expected);
		checkSynthesisedFix(infinitel, specificationListener);
	}
	
	private Infinitel loopFixerForExample(int exampleNumber) {
		String sourcePath = format("../test-projects/src/main/java/infinitel_examples/infinitel_example_%d/InfinitelExample.java", exampleNumber);
		File sourceFile = FileHandler.fileFrom(sourcePath);
		URL[] classPath = FileHandler.classpathFrom("../test-projects/target/classes/:../test-projects/target/test-classes/");
		return new Infinitel(sourceFile, classPath);
	}
	
	private Pair<SourcePosition, TestCasesListener> checkInfiniteLoop(Infinitel infinitel, ClassLoader classLoader, int line) {
		TestCasesListener listener = new TestCasesListener();
		Collection<SourcePosition> infiniteLoops = infinitel.infiniteLoopsRunningTests(classLoader, listener);
		assertEquals(1, infiniteLoops.size());
		SourcePosition loopPosition = CollectionLibrary.any(infiniteLoops);
		assertTrue(FileHandler.isSameFile(infinitel.project().sourceFile(), loopPosition.getFile()));
		assertEquals(line, loopPosition.getLine());
		return new Pair<>(loopPosition, listener);
	}
	
	private Pair<Collection<TestCase>,Collection<TestCase>> checkTests(Infinitel infinitel, ClassLoader classLoader, SourcePosition loopPosition,
			TestCasesListener listener, int failingTests, int passingTests) {
		LoopUnroller unroller = new LoopUnroller(infinitel.monitor(), classLoader, listener);
		Collection<TestCase> passingTestsUsingLoop = unroller.testsUsingLoop(loopPosition, listener.successfulTests());
		assertEquals(passingTests, passingTestsUsingLoop.size());
		Collection<TestCase> failingTestsUsingLoop = unroller.testsUsingLoop(loopPosition, listener.failedTests());
		assertEquals(failingTests, failingTestsUsingLoop.size());
		Collection<TestCase> allTestsUsingLoop = unroller.testsUsingLoop(loopPosition, listener.allTests());
		assertEquals(failingTests + passingTests, allTestsUsingLoop.size());
		return new Pair<>(passingTestsUsingLoop, failingTestsUsingLoop);
	}
	
	private LoopIterativeValueCollectorListener checkThresholds(Infinitel infinitel, ClassLoader classLoader, SourcePosition loopPosition, 
			Collection<TestCase> passingTests, Collection<TestCase> failingTests, Map<String, Integer> expected) {
		LoopIterativeValueCollectorListener specificationListener = new LoopIterativeValueCollectorListener();
		LoopUnroller unroller = new LoopUnroller(infinitel.monitor(), classLoader, specificationListener);
		Map<TestCase, Integer> thresholds = unroller.numberOfIterationsByTestIn(loopPosition, passingTests, failingTests);
		Map<String, Integer> thresholdsByName = MapLibrary.toStringMap(thresholds);
		assertEquals(expected, thresholdsByName);
		return specificationListener;
	}
	
	private void checkSynthesisedFix(Infinitel infinitel, LoopIterativeValueCollectorListener listener) {
		CodeGenesis genesis = infinitel.synthesiseCodeFor(listener.specifications());
		assertTrue(genesis.isSuccessful());
	}
	
	private Map<String, Integer> expectedThresholdsMap(int exampleNumber, List<String> testNames, List<Integer> thresholds) {
		String qualifiedName = format("infinitel_examples.infinitel_example_%d.InfinitelExampleTest#", exampleNumber);
		Map<String, Integer> expectedMap = MapLibrary.newHashMap();
		assertEquals(testNames.size(), thresholds.size());
		for (int i = 0; i < testNames.size(); i += 1) {
			expectedMap.put(qualifiedName + testNames.get(i), thresholds.get(i));
		}
		return expectedMap;
	}
}
