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
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import fr.inria.lille.commons.collections.Bag;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.Pair;
import fr.inria.lille.commons.spoon.util.SpoonElementLibrary;
import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.commons.utils.library.FileLibrary;
import fr.inria.lille.commons.utils.library.StringLibrary;
import fr.inria.lille.infinitel.instrumenting.CompoundLoopMonitorBuilder;
import fr.inria.lille.infinitel.loop.FixableLoop;
import fr.inria.lille.infinitel.loop.While;
import fr.inria.lille.infinitel.mining.MonitoringTestExecutor;
import fr.inria.lille.repair.ProjectReference;

public class InfinitelTest {
	
	@Test
	public void loopNotProcessedInNonVoidReturningMethodLastStatement() {
		Infinitel infinitel = loopFixerForExample(1);
		CompoundLoopMonitorBuilder monitorBuilder = new CompoundLoopMonitorBuilder(0);
		Map<String, CtWhile> loops = loopsByMethodIn(infinitel.project().sourceFile(), 4);
		assertTrue(monitorBuilder.isToBeProcessed(loops.get("loopResult")));
		assertTrue(monitorBuilder.isToBeProcessed(loops.get("fixableInfiniteLoop")));
		assertFalse(monitorBuilder.isToBeProcessed(loops.get("unfixableInfiniteLoop")));
		assertFalse(monitorBuilder.isToBeProcessed(loops.get("otherUnfixableInfiniteLoop")));
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
	
	@Test
	public void numberOfReturnsInExample1() {
		Infinitel infinitel = loopFixerForExample(1);
		MonitoringTestExecutor executor = infinitel.newTestExecutor();
		Map<Integer, Integer> numberOfReturns = MapLibrary.newHashMap(asList(8, 16), asList(0, 2));
		Collection<While> allLoops = executor.allLoops();
		assertEquals(numberOfReturns.size(), allLoops.size());
		assertEquals(1, executor.monitor().loopsWithReturn().size());
		for (While loop : allLoops) {
			assertEquals(numberOfReturns.get(loop.position().getLine()).intValue(), loop.returnStatements().size());
		}
	}
	
	@Test
	public void theBreakMustBeForTheWhile() {
		Infinitel infinitel = loopFixerForExample(2);
		MonitoringTestExecutor executor = infinitel.newTestExecutor();
		Map<Integer, Integer> numberOfBreaks = MapLibrary.newHashMap(asList(7, 14, 38, 50), asList(0, 1, 0, 0));
		Collection<While> allLoops = executor.allLoops();
		assertEquals(numberOfBreaks.size(), allLoops.size());
		assertEquals(1, executor.monitor().loopsWithBreak().size());
		for (While loop : allLoops) {
			assertEquals(numberOfBreaks.get(loop.position().getLine()).intValue(), loop.breakStatements().size());
		}
	}
	
	@Test
	public void theReturnMustBeForTheWhile() {
		Infinitel infinitel = loopFixerForExample(2);
		MonitoringTestExecutor executor = infinitel.newTestExecutor();
		Map<Integer, Integer> numberOfReturns = MapLibrary.newHashMap(asList(7, 14, 38, 50), asList(0, 2, 0, 0));
		Collection<While> allLoops = executor.allLoops();
		assertEquals(numberOfReturns.size(), allLoops.size());
		assertEquals(1, executor.monitor().loopsWithReturn().size());
		for (While loop : allLoops) {
			assertEquals(numberOfReturns.get(loop.position().getLine()).intValue(), loop.returnStatements().size());
		}
	}
	
	@Test
	public void numberOfBreaksInExample3() {
		Infinitel infinitel = loopFixerForExample(3);
		MonitoringTestExecutor executor = infinitel.newTestExecutor();
		Map<Integer, Integer> numberOfBreaks = MapLibrary.newHashMap(asList(7, 8), asList(1, 0));
		Collection<While> allLoops = executor.allLoops();
		assertEquals(numberOfBreaks.size(), allLoops.size());
		assertEquals(1, executor.monitor().loopsWithBreak().size());
		for (While loop : allLoops) {
			assertEquals(numberOfBreaks.get(loop.position().getLine()).intValue(), loop.breakStatements().size());
		}
	}
	
	@Test
	public void bookkeepingInLoopsOfExample3() {
		Infinitel infinitel = loopFixerForExample(3);
		ProjectReference project = infinitel.project();
		InfinitelDiagnostician diagnostician = new InfinitelDiagnostician(project);
		MonitoringTestExecutor testExecutor = diagnostician.newTestExecutor();
		testExecutor.execute(project.testClasses());
		int threshold = diagnostician.configuration().iterationsThreshold();
		Map<Integer, Bag<Integer>> records = MapLibrary.newHashMap();
		Bag<Integer> topLoopRecords = Bag.newHashBag(asList(1,threshold), asList(2, 1));
		Bag<Integer> nestedLoopRecords = Bag.newHashBag(asList(0, 1, 10), asList(threshold, 1, 1));
		records.put(7, topLoopRecords);
		records.put(8, nestedLoopRecords);
		assertEquals(2, testExecutor.allLoops().size());
		for (While loop : testExecutor.allLoops()) {
			assertEquals(records.get(loop.position().getLine()), testExecutor.monitor().exitRecordsOf(loop));
		}
	}
	
	private Map<String, CtWhile> loopsByMethodIn(File sourceFile, int numberOfLoops) {
		Factory model = SpoonModelLibrary.modelFor(sourceFile);
		Collection<CtPackage> allRoots = model.Package().getAllRoots();
		assertEquals(1, allRoots.size());
		Collection<CtWhile> elements = SpoonElementLibrary.allChildrenOf(CollectionLibrary.any(allRoots), CtWhile.class);
		assertEquals(numberOfLoops, elements.size());
		Map<String, CtWhile> byMethod = MapLibrary.newHashMap();
		for (CtWhile loop : elements) {
			String methodName = loop.getParent(CtMethod.class).getSimpleName();
			byMethod.put(methodName, loop);
		}
		return byMethod;
	}
	
	@Test
	public void infinitelExample4() {
		Infinitel infinitel = loopFixerForExample(4);
		MonitoringTestExecutor testExecutor = infinitel.newTestExecutor();
		assertEquals(1, testExecutor.allLoops().size());
		While loop = CollectionLibrary.any(testExecutor.allLoops());
		assertEquals(1, testExecutor.monitor().loopsWithBreak().size());
		assertEquals(1, testExecutor.monitor().loopsWithReturn().size());
		assertEquals(1, testExecutor.monitor().loopsWithBreakAndReturn().size());
		assertEquals(0, testExecutor.monitor().loopsWithoutBodyExit().size());
		TestCasesListener listener = new TestCasesListener();
		testExecutor.execute(infinitel.project().testClasses(), listener);
		assertEquals(2, testExecutor.monitor().numberOfReturnExitsIn(loop));
		assertEquals(Bag.newHashBag(1, 4), testExecutor.monitor().returnRecordsOf(loop));
		assertEquals(2, testExecutor.monitor().numberOfBreakExitsIn(loop));
		assertEquals(Bag.newHashBag(1, 3), testExecutor.monitor().breakRecordsOf(loop));
		assertEquals(6, testExecutor.monitor().numberOfRecordsIn(loop));
		assertEquals(Bag.newHashBag(0,1,1,3,4,6), testExecutor.monitor().exitRecordsOf(loop));
		List<String> testNames = asList("returnExitIn1", "returnExitIn4", "breakExitIn1", "breakExitIn3", "normalExitIn0", "normalExitIn6");
		Map<String, Integer> expected = expectedIterationsMap(4, testNames, asList(1, 4, 1, 3, 0, 6));
		checkIterations(infinitel, testExecutor, loop, listener.successfulTests(), listener.failedTests(), expected);
	}

	@Test
	public void infinitelExample1() {
		/** This test is very slow with some versions of CVC4 */
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
		Pair<While, TestCasesListener> pair = checkInfiniteLoop(infinitel, testExecutor, infiniteLoopLine);
		While loop = pair.first();
		TestCasesListener listener = pair.second();
		Pair<Collection<TestCase>, Collection<TestCase>> checkedTests = checkTests(infinitel, testExecutor, loop, listener, passingTests, failingTests);
		Collection<TestCase> passedTests = checkedTests.first();
		Collection<TestCase> failedTests = checkedTests.second();
		Map<TestCase, Integer> actualThresholdsByTest = checkIterations(infinitel, testExecutor, loop, passedTests, failedTests, thresholdsByTest);
		checkSynthesisedFix(infinitelExample, infinitel, testExecutor, actualThresholdsByTest, loop);
	}
	
	private Infinitel loopFixerForExample(int exampleNumber) {
		String sourcePath = format("../test-projects/src/main/java/infinitel_examples/infinitel_example_%d/InfinitelExample.java", exampleNumber);
		String classPath = "../test-projects/target/classes/:../test-projects/target/test-classes/";
		String testClass = format("infinitel_examples.infinitel_example_%d.InfinitelExampleTest", exampleNumber);
		ProjectReference project = new ProjectReference(sourcePath, classPath, new String[] { testClass });
		return new Infinitel(project);
	}
	
	private Pair<While, TestCasesListener> checkInfiniteLoop(Infinitel infinitel, MonitoringTestExecutor testExecutor, int line) {
		TestCasesListener listener = new TestCasesListener();
		Collection<While> infiniteLoops = infinitel.infiniteLoops(testExecutor, listener);
		assertEquals(1, infiniteLoops.size());
		While loop = CollectionLibrary.any(infiniteLoops);
		assertTrue(FileLibrary.isSameFile(infinitel.project().sourceFile(), loop.position().getFile()));
		assertEquals(line, loop.position().getLine());
		return Pair.from(loop, listener);
	}
	
	private Pair<Collection<TestCase>,Collection<TestCase>> checkTests(Infinitel infinitel, MonitoringTestExecutor testExecutor, While loop,
			TestCasesListener listener, int passingTests, int failingTests) {
		Collection<FixableLoop> fixableLoops = infinitel.fixableLoops(testExecutor, asList(loop), listener);
		assertEquals(1, fixableLoops.size());
		FixableLoop fixableLoop  = (FixableLoop) fixableLoops.toArray()[0];
		Collection<TestCase> passingTestsUsingLoop = fixableLoop.passingTests();
		Collection<TestCase> failingTestsUsingLoop = fixableLoop.failingTests();
		assertEquals(passingTests, passingTestsUsingLoop.size());
		assertEquals(failingTests, failingTestsUsingLoop.size());
		return Pair.from(passingTestsUsingLoop, failingTestsUsingLoop);
	}
	
	private Map<TestCase, Integer> checkIterations(Infinitel infinitel, MonitoringTestExecutor testExecutor, While loop, 
			Collection<TestCase> passedTests, Collection<TestCase> failedTests, Map<String, Integer> expected) {
		Map<TestCase, Integer> thresholdsByTest = infinitel.thresholdsByTest(loop, testExecutor, failedTests, passedTests);
		Map<String, Integer> thresholdByTestName = StringLibrary.toStringMap(thresholdsByTest);
		assertEquals(expected, thresholdByTestName);
		return thresholdsByTest;
	}
	
	private void checkSynthesisedFix(int test, Infinitel infinitel, MonitoringTestExecutor testExecutor, Map<TestCase, Integer> thresholdsByTest, While loop) {
		Collection<Specification<Boolean>> specifications = infinitel.testSpecifications(thresholdsByTest, testExecutor, loop);
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
