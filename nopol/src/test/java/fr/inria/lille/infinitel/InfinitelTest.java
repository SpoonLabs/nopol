package fr.inria.lille.infinitel;

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
import fr.inria.lille.infinitel.loop.LoopStatementsMonitor;
import fr.inria.lille.infinitel.loop.LoopUnroller;

public class InfinitelTest {

	@Before
	public void setup() {
		String example1SourceFile = "src/test/resources/infinitel_examples/src/infinitel_example1/InfinitelExample1.java";
		String example1Classpath = "src/test/resources/infinitel_examples/bin/";
		String[] example1TestClasses = new String[] {"infinitel_example1.InfinitelExample1Test"};
		example1Project = new ProjectReference(example1SourceFile, example1Classpath, example1TestClasses);
	}
	
	@Test
	public void example1LoopDetector() {
		Number threshold = InfinitelConfiguration.iterationsThreshold();
		SourceInstrumenter instrumenter = new SourceInstrumenter(example1Project());
		LoopStatementsMonitor monitor = new LoopStatementsMonitor(threshold);
		Map<String, Class<?>> processedClassCache = instrumenter.instrumentedWith(monitor);
		ClassLoader classLoaderForTestThread = new CacheBasedClassLoader(example1Project().classpath(), processedClassCache);
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(example1Project().testClasses(), classLoaderForTestThread, listener);
		Collection<SourcePosition> infiniteLoops = monitor.loopsAboveThreshold();
		Assert.assertEquals(1, listener.failedTests().size());
		Assert.assertEquals(4, listener.successfulTests().size());
		Assert.assertEquals(1,  infiniteLoops.size());
		SourcePosition loopPosition = (SourcePosition) infiniteLoops.toArray()[0];
		Assert.assertEquals(10, loopPosition.getLine());
		Assert.assertEquals("InfinitelExample1.java", loopPosition.getFile().getName());
		LoopUnroller unroller = new LoopUnroller(monitor, classLoaderForTestThread);
		Map<TestCase, Integer> thresholds = unroller.thresholdForEach(listener.successfulTests(), listener.failedTests(), loopPosition);
		Map<String, Integer> thresholdsByName = MapLibrary.toStringMap(thresholds);
		Assert.assertEquals(Integer.valueOf(0), thresholdsByName.get("infinitel_example1.InfinitelExample1Test#test1"));
		Assert.assertEquals(Integer.valueOf(1), thresholdsByName.get("infinitel_example1.InfinitelExample1Test#test2"));
		Assert.assertEquals(Integer.valueOf(2), thresholdsByName.get("infinitel_example1.InfinitelExample1Test#test3"));
		Assert.assertEquals(Integer.valueOf(3), thresholdsByName.get("infinitel_example1.InfinitelExample1Test#test4"));
		Assert.assertEquals(Integer.valueOf(4), thresholdsByName.get("infinitel_example1.InfinitelExample1Test#testNegative"));
	}

	private ProjectReference example1Project() {
		return example1Project;
	}
	
	private ProjectReference example1Project;
}
