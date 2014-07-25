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
	
	@Test
	public void nonFixableNestedLoopInExample3() {
		Infinitel infinitel = loopFixerForExample(3);
		InfinitelDiagnostician diagnostician = new InfinitelDiagnostician(infinitel.project());
		MonitoringTestExecutor testExecutor = diagnostician.newTestExecutor();
		assertEquals(2, testExecutor.allLoops().size());
		Collection<FixableLoop> loopsInvokedOnlyOnce = diagnostician.loopsInvokedOnlyOnce(testExecutor);
		assertEquals(1, loopsInvokedOnlyOnce.size());
		FixableLoop loop = CollectionLibrary.any(loopsInvokedOnlyOnce);
		assertEquals(7, loop.position().getLine());
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
		Map<String, Integer> expected = expectedIterationsMap(1, asList("test1", "test2", "test3", "test4", "testNegative"), asList(0, 1, 2, 3, 4));
		checkInfinitel(1, 8, 4, 1, expected);
	}
	
	@Test
	public void infinitelExample2() {
		Map<String, Integer> expected = expectedIterationsMap(2, asList("infiniteLoop", "oneIteration"), asList(1, 1));
		checkInfinitel(2, 7, 1, 1, expected);
	}
	
	@Test
	public void infinitelExample3() {
		Map<String, Integer> expected = expectedIterationsMap(3, asList("reachesZeroInOneIteration", "reachesZeroInTenIterations", "doesNotReachZeroReturnCopy"), asList(1, 1, 0));
		checkInfinitel(3, 7, 3, 0, expected);
	}
	
	public void checkInfinitel(int infinitelExample, int infiniteLoopLine, int passingTests, int failingTests, Map<String, Integer> thresholdsByTest) {
		Infinitel infinitel = loopFixerForExample(infinitelExample);
		MonitoringTestExecutor testExecutor = infinitel.newTestExecutor();
		Pair<SourcePosition, TestCasesListener> pair = checkInfiniteLoop(infinitel, testExecutor, infiniteLoopLine);
		SourcePosition loopPosition = pair.first();
		TestCasesListener listener = pair.second();
		Pair<Collection<TestCase>, Collection<TestCase>> checkedTests = checkTests(infinitel, testExecutor, loopPosition, listener, passingTests, failingTests);
		Collection<TestCase> passedTests = checkedTests.first();
		Collection<TestCase> failedTests = checkedTests.second();
		Map<TestCase, Integer> actualThresholdsByTest = checkIterations(infinitel, testExecutor, loopPosition, passedTests, failedTests, thresholdsByTest);
		checkSynthesisedFix(infinitelExample, infinitel, testExecutor, actualThresholdsByTest, loopPosition);
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
		Collection<FixableLoop> fixableLoops = infinitel.fixableLoops(testExecutor, asList(loopPosition), listener);
		assertEquals(1, fixableLoops.size());
		FixableLoop fixableLoop  = (FixableLoop) fixableLoops.toArray()[0];
		Collection<TestCase> passingTestsUsingLoop = fixableLoop.passingTests();
		Collection<TestCase> failingTestsUsingLoop = fixableLoop.failingTests();
		assertEquals(passingTests, passingTestsUsingLoop.size());
		assertEquals(failingTests, failingTestsUsingLoop.size());
		return new Pair<>(passingTestsUsingLoop, failingTestsUsingLoop);
	}
	
	private Map<TestCase, Integer> checkIterations(Infinitel infinitel, MonitoringTestExecutor testExecutor, SourcePosition loopPosition, 
			Collection<TestCase> passedTests, Collection<TestCase> failedTests, Map<String, Integer> expected) {
		Map<TestCase, Integer> thresholdsByTest = infinitel.thresholdsByTest(loopPosition, testExecutor, failedTests, passedTests);
		Map<String, Integer> thresholdByTestName = MapLibrary.toStringMap(thresholdsByTest);
		assertEquals(expected, thresholdByTestName);
		return thresholdsByTest;
	}
	
	private void checkSynthesisedFix(int test, Infinitel infinitel, MonitoringTestExecutor testExecutor, Map<TestCase, Integer> thresholdsByTest, SourcePosition loopPosition) {
		Collection<Specification<Boolean>> specifications = infinitel.testSpecifications(thresholdsByTest, testExecutor, loopPosition);
		CodeGenesis genesis = infinitel.synthesiseCodeFor(specifications);
		assertTrue(genesis.isSuccessful());
		System.out.println(String.format("Patch for infinitel example %d: %s", test, genesis.returnStatement()));
	}
	
	private Map<String, Integer> expectedIterationsMap(int exampleNumber, List<String> testNames, List<Integer> iterations) {
		String qualifiedName = format("infinitel_examples.infinitel_example_%d.InfinitelExampleTest#", exampleNumber);
		Map<String, Integer> expectedMap = MapLibrary.newHashMap();
		assertEquals(testNames.size(), iterations.size());
		for (int i = 0; i < testNames.size(); i += 1) {
			expectedMap.put(qualifiedName + testNames.get(i), iterations.get(i));
		}
		return expectedMap;
	}
}
