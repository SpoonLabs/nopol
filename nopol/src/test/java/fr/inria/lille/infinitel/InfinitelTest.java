package fr.inria.lille.infinitel;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.Pair;
import fr.inria.lille.commons.io.FileHandler;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.spoon.SpoonLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.infinitel.loop.CentralLoopMonitor;
import fr.inria.lille.infinitel.loop.FixableLoop;
import fr.inria.lille.infinitel.loop.MonitoringTestExecutor;

public class InfinitelTest {
	
	@Test
	public void loopNotProcessedInNonVoidReturningMethodLastStatement() {
		CentralLoopMonitor monitor = new CentralLoopMonitor(0);
		Infinitel infinitel = loopFixerForExample(1);
		Map<String, CtWhile> loops = loopsByMethodIn(infinitel.project().sourceFile(), 4);
		assertTrue(monitor.isToBeProcessed(loops.get("loopResult")));
		assertTrue(monitor.isToBeProcessed(loops.get("fixableInfiniteLoop")));
		assertFalse(monitor.isToBeProcessed(loops.get("unfixableInfiniteLoop")));
		assertFalse(monitor.isToBeProcessed(loops.get("otherUnfixableInfiniteLoop")));
	}
	
	private Map<String, CtWhile> loopsByMethodIn(File sourceFile, int numberOfLoops) {
		Factory model = SpoonLibrary.modelFor(sourceFile);
		TypeFilter<CtWhile> filter = new TypeFilter<>(CtWhile.class);
		List<CtWhile> elements = Query.getElements(model, filter);
		assertEquals(numberOfLoops, elements.size());
		Map<String, CtWhile> byMethod = MapLibrary.newHashMap();
		for (CtWhile loop : elements) {
			String methodName = loop.getParent(CtMethod.class).getSimpleName();
			byMethod.put(methodName, loop);
		}
		return byMethod;
	}
	
	@Test
	public void infinitelExample1() {
		Map<String, Integer> expected = expectedThresholdsMap(1, asList("test1", "test2", "test3", "test4", "testNegative"), asList(0, 1, 2, 3, 4));
		checkInfinitel(1, 8, 4, 1, expected);
	}
	
	@Test
	public void infinitelExample2() {
		Map<String, Integer> expected = expectedThresholdsMap(2, asList("infiniteLoop", "oneIteration"), asList(1, 1));
		checkInfinitel(2, 7, 1, 1, expected);
	}
	
	public void checkInfinitel(int infinitelExample, int infiniteLoopLine, int passingTests, int failingTests, Map<String, Integer> thresholdsMap) {
		Infinitel infinitel = loopFixerForExample(infinitelExample);
		MonitoringTestExecutor testExecutor = infinitel.newTestExecutor();
		Pair<SourcePosition, TestCasesListener> pair = checkInfiniteLoop(infinitel, testExecutor, infiniteLoopLine);
		SourcePosition loopPosition = pair.first();
		TestCasesListener listener = pair.second();
		Pair<Collection<TestCase>, Collection<TestCase>> checkedTests = checkTests(infinitel, testExecutor, loopPosition, listener, passingTests, failingTests);
		Collection<TestCase> passedTests = checkedTests.first();
		Collection<TestCase> failedTests = checkedTests.second();
		Map<TestCase, Integer> testsAndThresholds = checkThresholds(infinitel, testExecutor, loopPosition, passedTests, failedTests, thresholdsMap);
		checkSynthesisedFix(infinitel, testExecutor, testsAndThresholds, loopPosition);
	}
	
	private Infinitel loopFixerForExample(int exampleNumber) {
		String sourcePath = format("../test-projects/src/main/java/infinitel_examples/infinitel_example_%d/InfinitelExample.java", exampleNumber);
		String classPath = "../test-projects/target/classes/:../test-projects/target/test-classes/";
		String testClass = format("infinitel_examples.infinitel_example_%d.InfinitelExampleTest", exampleNumber);
		ProjectReference project = new ProjectReference(sourcePath, classPath, new String[] { testClass });
		return new Infinitel(project);
	}
	
	private Pair<SourcePosition, TestCasesListener> checkInfiniteLoop(Infinitel infinitel, MonitoringTestExecutor testExecutor, int line) {
		TestCasesListener listener = new TestCasesListener();
		Collection<SourcePosition> infiniteLoops = infinitel.infiniteLoops(testExecutor, listener);
		assertEquals(1, infiniteLoops.size());
		SourcePosition loopPosition = CollectionLibrary.any(infiniteLoops);
		assertTrue(FileHandler.isSameFile(infinitel.project().sourceFile(), loopPosition.getFile()));
		assertEquals(line, loopPosition.getLine());
		return new Pair<>(loopPosition, listener);
	}
	
	private Pair<Collection<TestCase>,Collection<TestCase>> checkTests(Infinitel infinitel, MonitoringTestExecutor testExecutor, SourcePosition loopPosition,
			TestCasesListener listener, int passingTests, int failingTests) {
		Collection<FixableLoop> fixableLoops = infinitel.fixableInfiniteLoops(testExecutor, asList(loopPosition), listener);
		assertEquals(1, fixableLoops.size());
		FixableLoop fixableLoop  = (FixableLoop) fixableLoops.toArray()[0];
		Collection<TestCase> passingTestsUsingLoop = fixableLoop.passingTests();
		Collection<TestCase> failingTestsUsingLoop = fixableLoop.failingTests();
		assertEquals(passingTests, passingTestsUsingLoop.size());
		assertEquals(failingTests, failingTestsUsingLoop.size());
		return new Pair<>(passingTestsUsingLoop, failingTestsUsingLoop);
	}
	
	private Map<TestCase, Integer> checkThresholds(Infinitel infinitel, MonitoringTestExecutor testExecutor, SourcePosition loopPosition, 
			Collection<TestCase> passedTests, Collection<TestCase> failedTests, Map<String, Integer> expected) {
		Map<TestCase, Integer> actual = infinitel.testsAndThresholds(loopPosition, testExecutor, failedTests, passedTests);
		Map<String, Integer> thresholdsByName = MapLibrary.toStringMap(actual);
		assertEquals(expected, thresholdsByName);
		return actual;
	}
	
	private void checkSynthesisedFix(Infinitel infinitel, MonitoringTestExecutor testExecutor, Map<TestCase, Integer> testsAndThresholds, SourcePosition loopPosition) {
		Collection<Specification<Boolean>> specifications = infinitel.testSpecifications(testsAndThresholds, testExecutor, loopPosition);
		CodeGenesis genesis = infinitel.synthesiseCodeFor(specifications);
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
