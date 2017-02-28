package fr.inria.lille.repair.infinitel;

import fr.inria.lille.commons.spoon.util.SpoonElementLibrary;
import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;
import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.repair.Main;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.infinitel.loop.While;
import fr.inria.lille.repair.infinitel.loop.examination.LoopTestResult;
import fr.inria.lille.repair.infinitel.loop.implant.MonitoringTestExecutor;
import fr.inria.lille.repair.infinitel.loop.implant.ProjectMonitorImplanter;
import org.junit.Ignore;
import org.junit.Test;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import xxl.java.container.classic.MetaCollection;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.various.Bag;
import xxl.java.junit.TestCase;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InfinitelTest {
	
	@Ignore
	@Test
	public void realProject() {
		InfinitelConfiguration.setIterationsThreshold((int) 1E6);
		String pwd = "/Users/virtual/Desktop/data/projects/dataset-infinitel/fop_13984cc/";
		String src = pwd + "src/main/java/";
		String ab = pwd + "target/classes/";
		String ac = pwd + "target/test-classes/";
		String buildPath = ac + ":" + ab;
		String[] deps = new String[] { "ant-1.8.2.jar", "asm-3.1.jar", "avalon-framework-4.2.0.jar", "batik-all-trunk.jar", "commons-io-1.3.1.jar",
				"commons-logging-1.0.4.jar", "fontbox-1.8.4-patched.jar", "hamcrest.core-1.1.0.jar", "jaxen-1.1.1.jar", "junit-4.8.1.jar",
				"mockito-core-1.8.5.jar", "objenesis-1.0.0.jar", "pmd-4.2.5.jar", "qdox-1.12.jar", "serializer-2.7.0.jar", "servlet-2.2.jar",
				"xalan-2.7.0.jar", "xercesImpl-2.7.1.jar", "xml-apis-1.3.04.jar", "xml-apis-ext-1.3.04.jar", "xmlgraphics-commons-svn-trunk.jar",
				"xmlunit-1.2.jar", "../ant/fop-13984cc.jar" };
		for (String dep : deps) {
			buildPath += ":" + pwd + "lib/" + dep;
		}
		String z3Path = "/Users/virtual/Desktop/data/projects/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		Main.main(new String[] {"infinitel", src, buildPath, "z3", z3Path});
	}
	
	@Test
	public void unbreakableLoops() {
		Infinitel infinitel = infinitel(1);
		ProjectMonitorImplanter implanter = new ProjectMonitorImplanter(0);
		Map<String, CtWhile> loops = loopsByMethodIn(infinitel.getNopolContext().getProjectSources(), 5);
		assertFalse(implanter.isUnbreakable(loops.get("loopResult")));
		assertFalse(implanter.isUnbreakable(loops.get("fixableInfiniteLoop")));
		assertFalse(implanter.isUnbreakable(loops.get("binomialTest")));
		assertTrue(implanter.isUnbreakable(loops.get("unfixableInfiniteLoop")));
		assertTrue(implanter.isUnbreakable(loops.get("otherUnfixableInfiniteLoop")));
	}

	@Test
	public void nestedLoopIsNotInfiniteInExample3() {
		Infinitel infinitel = infinitel(3);
		MonitoringTestExecutor testExecutor = infinitel.newTestExecutor();
		LoopTestResult testResult = infinitel.newTestResult(testExecutor);
		assertEquals(2, testResult.numberOfLoops());
		Collection<While> loops = testResult.infiniteLoops();
		assertEquals(1, loops.size());
		While loop = MetaCollection.any(loops);
		assertEquals(7, loop.position().getLine());
	}

	@Test
	public void numberOfReturnsInExample1() {
		Infinitel infinitel = infinitel(1);
		MonitoringTestExecutor executor = infinitel.newTestExecutor();
		Map<Integer, Integer> numberOfReturns = MetaMap.newHashMap(asList(8, 16, 26, 37, 54), asList(0, 2, 2, 2, 0));
		Collection<While> allLoops = executor.monitor().allLoops();
		assertEquals(numberOfReturns.size(), allLoops.size());
		assertEquals(3, While.loopsWithReturn(allLoops).size());
		for (While loop : allLoops) {
			assertEquals(numberOfReturns.get(loop.position().getLine()).intValue(), loop.returnStatements().size());
		}
	}
	
	@Test
	public void theBreakMustBeForTheWhile() {
		Infinitel infinitel = infinitel(2);
		MonitoringTestExecutor executor = infinitel.newTestExecutor();
		Map<Integer, Integer> numberOfBreaks = MetaMap.newHashMap(asList(7, 14, 38, 50), asList(0, 1, 0, 0));
		Collection<While> allLoops = executor.monitor().allLoops();
		assertEquals(numberOfBreaks.size(), allLoops.size());
		assertEquals(1, While.loopsWithBreak(allLoops).size());
		for (While loop : allLoops) {
			assertEquals(numberOfBreaks.get(loop.position().getLine()).intValue(), loop.breakStatements().size());
		}
	}
	
	@Test
	public void theReturnMustBeForTheWhile() {
		Infinitel infinitel = infinitel(2);
		MonitoringTestExecutor executor = infinitel.newTestExecutor();
		Map<Integer, Integer> numberOfReturns = MetaMap.newHashMap(asList(7, 14, 38, 50), asList(0, 2, 0, 0));
		Collection<While> allLoops = executor.monitor().allLoops();
		assertEquals(numberOfReturns.size(), allLoops.size());
		assertEquals(1, While.loopsWithReturn(allLoops).size());
		for (While loop : allLoops) {
			assertEquals(numberOfReturns.get(loop.position().getLine()).intValue(), loop.returnStatements().size());
		}
	}
	
	@Test
	public void numberOfBreaksInExample3() {
		Infinitel infinitel = infinitel(3);
		MonitoringTestExecutor executor = infinitel.newTestExecutor();
		Map<Integer, Integer> numberOfBreaks = MetaMap.newHashMap(asList(7, 8), asList(1, 0));
		Collection<While> allLoops = executor.monitor().allLoops();
		assertEquals(numberOfBreaks.size(), allLoops.size());
		assertEquals(1, While.loopsWithBreak(allLoops).size());
		for (While loop : allLoops) {
			assertEquals(numberOfBreaks.get(loop.position().getLine()).intValue(), loop.breakStatements().size());
		}
	}
	
	@Test
	public void bookkeepingInLoopsOfExample3() {
		Infinitel infinitel = infinitel(3);
		MonitoringTestExecutor testExecutor = infinitel.newTestExecutor();
		LoopTestResult testResult = infinitel.newTestResult(testExecutor);
		int threshold = infinitel.configuration().iterationsThreshold();
		Map<Integer, Bag<Integer>> records = MetaMap.newHashMap();
		Bag<Integer> topLoopRecords = Bag.newHashBag(asList(1,threshold), asList(2, 1));
		Bag<Integer> nestedLoopRecords = Bag.newHashBag(asList(0, 1, 10), asList(threshold, 1, 1));
		records.put(7, topLoopRecords);
		records.put(8, nestedLoopRecords);
		assertEquals(2, testExecutor.monitor().allLoops().size());
		for (While loop : testExecutor.monitor().allLoops()) {
			assertEquals(records.get(loop.position().getLine()), testResult.aggregatedExitRecordsOf(loop));
		}
	}
	
	private Map<String, CtWhile> loopsByMethodIn(File[] sourceFiles, int numberOfLoops) {
		Factory model = SpoonModelLibrary.modelFor(sourceFiles);
		CtPackage root = model.Package().getRootPackage();
		Collection<CtWhile> elements = SpoonElementLibrary.allChildrenOf(root, CtWhile.class);
		assertEquals(numberOfLoops, elements.size());
		Map<String, CtWhile> byMethod = MetaMap.newHashMap();
		for (CtWhile loop : elements) {
			String methodName = loop.getParent(CtMethod.class).getSimpleName();
			byMethod.put(methodName, loop);
		}
		return byMethod;
	}
	
	@Test
	public void infinitelExample4() {
		InfiniteLoopFixer fixer = infiniteLoopFixerForExample(4);
		LoopTestResult testResult = fixer.testResult();
		Collection<While> allLoops = fixer.executor().monitor().allLoops();
		assertEquals(1, allLoops.size());
		While loop = MetaCollection.any(allLoops);
		assertEquals(1, While.loopsWithBreak(allLoops).size());
		assertEquals(1, While.loopsWithReturn(allLoops).size());
		assertEquals(1, While.loopsWithBreakAndReturn(allLoops).size());
		assertEquals(0, While.loopsWithoutBodyExit(allLoops).size());
		assertEquals(2, testResult.aggregatedNumberOfReturnExits(loop));
		assertEquals(Bag.newHashBag(1, 4), testResult.aggregatedReturnRecordsOf(loop));
		assertEquals(2,  testResult.aggregatedNumberOfBreakExits(loop));
		assertEquals(Bag.newHashBag(1, 3), testResult.aggregatedBreakRecordsOf(loop));
		assertEquals(6, testResult.aggregatedNumberOfRecords(loop));
		assertEquals(Bag.newHashBag(0,1,1,3,4,6), testResult.aggregatedExitRecordsOf(loop));
		List<String> testNames = asList("returnExitIn1", "returnExitIn4", "breakExitIn1", "breakExitIn3", "normalExitIn0", "normalExitIn6");
		Map<String, Integer> expected = expectedIterationsMap(4, testNames, asList(1, 4, 1, 3, 0, 6));
		for (String testName : expected.keySet()) {
			TestCase testCase = testWithName(testName, testResult);
			assertEquals((long) expected.get(testName), testResult.numberOfIterationsIn(loop, testCase));
		}
	}

	@Ignore
	@Test
	public void infinitelExample1() {
		/** This test is very slow with some versions of CVC4 */
		Map<String, Integer> expected = expectedIterationsMap(1, asList("testNegative"), asList(4));
		CodeGenesis fix = checkInfinitel(1, 8, 4, 1, expected);
		checkFix(fix, asList("(b != a)&&(((a)<=((1)+(1)))||((b)<(a)))",
							 "(b != a)&&((((-1)+(a))<=(1))||(!((a)<(b))))",
							 "((!(((a)-(1))<(b)))||(((a)-(1))<=(1)))&&(b != a)",
							 "((b != a)&&(((1)+(1))!=((a)-(1))))||((b)<=((a)-(1)))",
				             "(b != a)&&(((b)<(a))||((a)<=(((0)-(-1))+((0)-(-1)))))",
				             "(b != a) && ((b < a) || (a <= (0) - (-1) + (0) - (-1)))",
							 "!((((a)+(-1))<(b))&&((((1)-((a)+(-1)))<=(-1))||((a)==(b))))",
								"(b <= a + -1) || ((!(1 < a + -1)) && (b != a))"));
	}
	
	@Test
	public void infinitelExample2() {
		Map<String, Integer> expected = expectedIterationsMap(2, asList("infiniteLoop"), asList(1));
		CodeGenesis fix = checkInfinitel(2, 7, 1, 1, expected);
		checkFix(fix, asList("(a == 0)"));
	}

	@Ignore
	@Test
	public void infinitelExample3() {
		Map<String, Integer> expected = expectedIterationsMap(3, asList("doesNotReachZeroReturnCopy"), asList(0));
		CodeGenesis fix = checkInfinitel(3, 7, 3, 0, expected);
		checkFix(fix, asList("(-1)<(aCopy)", "(1)<=(a)", "(0)<(a)", "0 < a","(a == 0)","1 <= a"));
	}
	
	@Test
	public void infinitelExample5() {
		Map<String, Integer> expected = expectedIterationsMap(5, asList("infiniteLoop"), asList(37));
		CodeGenesis fix = checkInfinitel(5, 11, 2, 1, expected);
		checkFix(fix, asList("(infinitel_examples.infinitel_example_5.InfinitelExample.this.consumer.getSize())!="+
								"(infinitel_examples.infinitel_example_5.InfinitelExample.this.consumer.getConsumed())",
				"(infinitel_examples.infinitel_example_5.InfinitelExample.this.consumer.getSize()) !=" +
						" (infinitel_examples.infinitel_example_5.InfinitelExample.this.consumer.getConsumed())",
				"infinitel_examples.infinitel_example_5.InfinitelExample.this.consumer.getConsumed() < " +
						"infinitel_examples.infinitel_example_5.InfinitelExample.this.consumer.getSize()"));
	}
	
	private CodeGenesis checkInfinitel(int infinitelExample, int infiniteLoopLine, int passingTests, int failingTests, Map<String, Integer> testThresholds) {
		InfiniteLoopFixer fixer = infiniteLoopFixerForExample(infinitelExample);
		LoopTestResult testResult = fixer.testResult();
		assertEquals(failingTests, testResult.numberOfFailedTests());
		assertEquals(passingTests, testResult.numberOfSuccessfulTests());
		Collection<While> infiniteLoops = testResult.infiniteLoops();
		assertEquals(1, infiniteLoops.size());
		While infiniteLoop = MetaCollection.any(infiniteLoops);
		assertEquals(infiniteLoopLine, infiniteLoop.position().getLine());
		checkSuccessfulIterations(testThresholds, infiniteLoop, fixer, testResult);
		return fixer.fixInfiniteLoop(infiniteLoop);
	}
	
	private void checkFix(CodeGenesis fix, Collection<String> fixes) {
		assertTrue(fix.isSuccessful());
		System.out.println(fix.returnStatement());
		assertTrue(fixes.contains(fix.returnStatement()));
	}
	
	private void checkSuccessfulIterations(Map<String, Integer> expected, While loop, InfiniteLoopFixer fixer, LoopTestResult testResult) {
		Map<TestCase, Integer> nonHaltingTests = testResult.nonHaltingTestsOf(loop);
		for (String testName : expected.keySet()) {
			TestCase testCase = testWithName(testName, testResult);
			int actual = fixer.firstSuccessfulIteration(loop, testCase, nonHaltingTests.get(testCase));
			assertEquals((long) expected.get(testName), actual);
		}
	}
	
	private Infinitel infinitel(int exampleNumber) {
		String sourcePath = absolutePathOf(exampleNumber);
		String classPath = "../test-projects/target/classes/:../test-projects/target/test-classes/";
		String testClass = format("infinitel_examples.infinitel_example_%d.InfinitelExampleTest", exampleNumber);
		NopolContext project = new NopolContext(sourcePath, JavaLibrary.classpathFrom(classPath), new String[] { testClass });
		return new Infinitel(project);
	}
	
	private InfiniteLoopFixer infiniteLoopFixerForExample(int exampleNumber) {
		Infinitel infinitel = infinitel(exampleNumber);
		MonitoringTestExecutor testExecutor = infinitel.newTestExecutor();
		LoopTestResult testResult = infinitel.newTestResult(testExecutor);
		return new InfiniteLoopFixer(testResult, testExecutor);
	}

	private Map<String, Integer> expectedIterationsMap(int exampleNumber, List<String> testNames, List<Integer> iterations) {
		Map<String, Integer> expectedMap = MetaMap.newHashMap();
		assertEquals(testNames.size(), iterations.size());
		for (int i = 0; i < testNames.size(); i += 1) {
			expectedMap.put(testNames.get(i), iterations.get(i));
		}
		return expectedMap;
	}
	
	private TestCase testWithName(String testName, LoopTestResult testResult) {
		for (TestCase testCase : testResult.testCases()) {
			if (testCase.testName().equals(testName)) {
				return testCase;
			}
		}
		throw new RuntimeException("Test case not found: " + testName);
	}
	
	public static String absolutePathOf(int exampleNumber) {
		return format("../test-projects/src/main/java/infinitel_examples/infinitel_example_%d/InfinitelExample.java", exampleNumber);
	}
}
