package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static gov.nasa.jpf.util.test.TestJPF.assertEquals;
import static gov.nasa.jpf.util.test.TestJPF.assertTrue;

public class FlacocoLocalizerTest {

	@Test
	public void testOchiaiFlacocoLocalizer() throws Exception {

		/* test OchiaiCoCoSpoonLocalizer : the SourceLocation must be sorted following the Ochiai metric */

		File[] sources = new File[]{new File("../test-projects/src/main/java/nopol_examples/nopol_example_1/NopolExample.java")};
		URL[] classpath = new URL[]{
				new File("../test-projects/target/classes").toURI().toURL(),
				new File("../test-projects/target/test-classes").toURI().toURL()
		};
		String[] testClasses = new String[]{"nopol_examples.nopol_example_1.NopolExampleTest"};
		FlacocoFaultLocalizer localizer = new FlacocoFaultLocalizer(new NopolContext(sources, classpath, testClasses), new Ochiai());

		Map<SourceLocation, List<TestResult>> executedSourceLocationPerTest = localizer.getTestListPerStatement();
		assertEquals(10, executedSourceLocationPerTest.keySet().size());

		for (StatementSourceLocation sourceLocation : localizer.getStatements()) {
			System.out.println(sourceLocation);
		}

		SourceLocation sourceLocation1 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 27);
		SourceLocation sourceLocation2 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 16);
		SourceLocation sourceLocation3 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 15);
		SourceLocation sourceLocation4 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 18);
		SourceLocation sourceLocation5 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 12);
		SourceLocation sourceLocation6 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 23);
		SourceLocation sourceLocation7 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 25);
		SourceLocation sourceLocation8 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 13);
		SourceLocation sourceLocation9 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 24);
		SourceLocation sourceLocation10 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 21);

		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation1));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation2));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation3));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation4));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation5));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation6));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation7));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation8));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation9));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation10));

		List<? extends StatementSourceLocation> sortedStatements = localizer.getStatements();

		assertEquals(0.534, sortedStatements.get(0).getSuspiciousness(), 10E-3);
		assertEquals(0.5, sortedStatements.get(1).getSuspiciousness(), 10E-3);
		assertEquals(0.471, sortedStatements.get(2).getSuspiciousness(), 10E-3);
		assertEquals(0.471, sortedStatements.get(3).getSuspiciousness(), 10E-3);
		assertEquals(0.471, sortedStatements.get(4).getSuspiciousness(), 10E-3);
		assertEquals(0.471, sortedStatements.get(5).getSuspiciousness(), 10E-3);
		assertEquals(0.471, sortedStatements.get(6).getSuspiciousness(), 10E-3);
		assertEquals(0.471, sortedStatements.get(7).getSuspiciousness(), 10E-3);
		assertEquals(0.0, sortedStatements.get(8).getSuspiciousness(), 10E-3);
		assertEquals(0.0, sortedStatements.get(9).getSuspiciousness(), 10E-3);

		assertEquals(sourceLocation2, sortedStatements.get(0).getLocation());
	}
}
