package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by bdanglot on 10/4/16.
 */
public class GzoltarLocalizerTest {

	@Test
	public void testGzoltarLocalizer() throws Exception {

		/* test GzoltarLocalizer : the SourceLocation must be sorted following the Ochiai metric (default metric)*/

		final Metric metric = new Ochiai();

		File[] sources = new File[]{new File("../test-projects/src/main/java/nopol_examples/nopol_example_1/NopolExample.java")};
		URL[] classpath = new URL[]{
				new File("../test-projects/target/classes").toURI().toURL(),
				new File("../test-projects/target/test-classes").toURI().toURL()
		};
		String[] testClasses = new String[]{"nopol_examples.nopol_example_1.NopolExampleTest"};

		NopolContext nopolContext = new NopolContext(sources, classpath, testClasses);
		GZoltarFaultLocalizer localizer = GZoltarFaultLocalizer.createInstance(nopolContext);
		Map<SourceLocation, List<TestResult>> executedSourceLocationPerTest = localizer.getTestListPerStatement();
		assertEquals(5, executedSourceLocationPerTest.keySet().size());//Gzoltar does not log in constructor: so there is only 5 logged statement

		SourceLocation sourceLocation1 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 18);
		SourceLocation sourceLocation2 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 13);
		SourceLocation sourceLocation3 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 12);
		SourceLocation sourceLocation4 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 15);
		SourceLocation sourceLocation5 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 16);

		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation1));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation2));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation3));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation4));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation5));

		List<? extends StatementSourceLocation> sortedStatements = localizer.getStatements();

		assertEquals(0.534, sortedStatements.get(0).getSuspiciousness(), 10E-3);
		assertEquals(0.5, sortedStatements.get(1).getSuspiciousness(), 10E-3);
		assertEquals(0.471, sortedStatements.get(2).getSuspiciousness(), 10E-3);
		assertEquals(0.0, sortedStatements.get(3).getSuspiciousness(), 10E-3);
		assertEquals(0.0, sortedStatements.get(4).getSuspiciousness(), 10E-3);

		//Rank 1
		assertEquals(sourceLocation5, sortedStatements.get(0).getLocation());
	}

	@Test
	public void testGzoltarLocalizer2() throws Exception {


		File[] sources = new File[]{new File("../test-projects/src/main/java/nopol_examples/nopol_example_1/NopolExample.java")};
		URL[] classpath = new URL[]{
				new File("../test-projects/target/classes").toURI().toURL(),
				new File("../test-projects/target/test-classes").toURI().toURL()
		};
		String[] testClasses = new String[]{"nopol_examples.nopol_example_1.NopolExampleTest#test1"};

		NopolContext nopolContext = new NopolContext(sources, classpath, testClasses);
		GZoltarFaultLocalizer localizer = GZoltarFaultLocalizer.createInstance(nopolContext);
		Map<SourceLocation, List<TestResult>> executedSourceLocationPerTest = localizer.getTestListPerStatement();
		assertEquals(5, executedSourceLocationPerTest.keySet().size());//Gzoltar does not log in constructor: so there is only 5 logged statement

		SourceLocation sourceLocation2 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 13);
		SourceLocation sourceLocation3 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 12);

		for (SourceLocation loc : executedSourceLocationPerTest.keySet()) {
			for (TestResult res : executedSourceLocationPerTest.get(loc)) {
				assertEquals("nopol_examples.nopol_example_1.NopolExampleTest#test1", res.getTestCase().toString());
			}
		}

		System.out.println(executedSourceLocationPerTest);
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation2));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation3));


	}

}
