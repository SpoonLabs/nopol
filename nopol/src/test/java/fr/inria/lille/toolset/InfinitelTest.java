package fr.inria.lille.toolset;

import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.classes.CacheBasedClassLoader;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.spoon.SourceInstrumenter;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.suite.TestSuiteExecution;
import fr.inria.lille.infinitel.InfinitelConfiguration;
import fr.inria.lille.infinitel.loop.LoopStatementsMonitor;
import fr.inria.lille.infinitel.loop.LoopUnroller;

public class InfinitelTest {

	@Before
	public void setUp() {
		String example1SourceFile = "../test-projects/src/infinitel_examples/infinitel_example_1/InfinitelExample.java";
		String example1Classpath = "../test-projects/target/classes/";
		String[] example1TestClasses = new String[] {"infinitel_examples.infinitel_example_1.InfinitelExampleTest"};
		example1 = new ProjectReference(example1SourceFile, example1Classpath, example1TestClasses);
	}
	
	@Test
	public void example1LoopDetector() {
		Number threshold = InfinitelConfiguration.iterationsThreshold();
		SourceInstrumenter instrumenter = new SourceInstrumenter(example1());
		LoopStatementsMonitor monitor = new LoopStatementsMonitor(threshold);
		Map<String, Class<?>> processedClassCache = instrumenter.instrumentedWith(monitor);
		ClassLoader classLoaderForTestThread = new CacheBasedClassLoader(example1().classpath(), processedClassCache);
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(example1().testClasses(), classLoaderForTestThread, listener);
		Collection<SourcePosition> infiniteLoops = monitor.loopsAboveThreshold();
		Assert.assertEquals(1, listener.failedTests().size());
		Assert.assertEquals(4, listener.successfulTests().size());
		Assert.assertEquals(1,  infiniteLoops.size());
		SourcePosition loopPosition = (SourcePosition) infiniteLoops.toArray()[0];
		Assert.assertEquals(8, loopPosition.getLine());
		Assert.assertEquals("InfinitelExample.java", loopPosition.getFile().getName());
		LoopUnroller unroller = new LoopUnroller(monitor, classLoaderForTestThread);
		Map<TestCase, Integer> thresholds = unroller.thresholdForEach(listener.successfulTests(), listener.failedTests(), loopPosition);
		Map<String, Integer> thresholdsByName = MapLibrary.toStringMap(thresholds);
		String qualifiedName = "infinitel_examples.infinitel_example_1.InfinitelExampleTest";
		Assert.assertEquals(Integer.valueOf(0), thresholdsByName.get(qualifiedName + "#test1"));
		Assert.assertEquals(Integer.valueOf(1), thresholdsByName.get(qualifiedName + "#test2"));
		Assert.assertEquals(Integer.valueOf(2), thresholdsByName.get(qualifiedName + "#test3"));
		Assert.assertEquals(Integer.valueOf(3), thresholdsByName.get(qualifiedName + "#test4"));
		Assert.assertEquals(Integer.valueOf(4), thresholdsByName.get(qualifiedName + "#testNegative"));
	}

	private ProjectReference example1() {
		return example1;
	}
	
	private ProjectReference example1;
}
