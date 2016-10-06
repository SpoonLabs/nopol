package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static gov.nasa.jpf.util.test.TestJPF.assertEquals;
import static gov.nasa.jpf.util.test.TestJPF.assertTrue;

/**
 * Created by bdanglot on 10/4/16.
 */
public class OchiailocalizerTest {

	@Test
	public void testOchiaiCoCoSpoonLocalizer() throws Exception {

		/* test OchiaiCoCoSpoonLocalizer : the SourceLocation must be sorted following the Ochiai metric */

		final Metric metric = new Ochiai();

		File[] sources = new File[]{new File("../test-projects/src/main/java/nopol_examples/nopol_example_1/NopolExample.java")};
		URL[] classpath = new URL[]{
				new File("../test-projects/target/classes").toURI().toURL(),
				new File("../test-projects/target/test-classes").toURI().toURL()
		};
		String[] testClasses = new String[]{"nopol_examples.nopol_example_1.NopolExampleTest"};
		OchiaiFaultLocalizer localizer = new OchiaiFaultLocalizer(sources, classpath, testClasses, new Config());
		Map<SourceLocation, List<TestResult>> executedSourceLocationPerTest = localizer.getTestListPerStatement();
		assertEquals(8, executedSourceLocationPerTest.keySet().size());

		SourceLocation sourceLocation1 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 18);
		SourceLocation sourceLocation2 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 13);
		SourceLocation sourceLocation3 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 12);
		SourceLocation sourceLocation4 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 15);
		SourceLocation sourceLocation5 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 16);
		//CoCoSpoon is able to log in constructor: 3 SourceLocation more than GZoltar
		SourceLocation sourceLocation6 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 22);
		SourceLocation sourceLocation7 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 23);
		SourceLocation sourceLocation8 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 24);

		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation1));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation2));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation3));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation4));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation5));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation6));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation7));
		assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation8));

		List<AbstractStatement> sortedStatements = localizer.getStatements();

		assertEquals(0.534, metric.value(sortedStatements.get(0).getEf(), sortedStatements.get(0).getEp(), sortedStatements.get(0).getNf(), sortedStatements.get(0).getNp()), 10E1);
		assertEquals(0.5, metric.value(sortedStatements.get(1).getEf(), sortedStatements.get(1).getEp(), sortedStatements.get(1).getNf(), sortedStatements.get(1).getNp()), 10E1);
		assertEquals(0.471, metric.value(sortedStatements.get(2).getEf(), sortedStatements.get(2).getEp(), sortedStatements.get(2).getNf(), sortedStatements.get(2).getNp()), 10E1);
		assertEquals(0.471, metric.value(sortedStatements.get(3).getEf(), sortedStatements.get(3).getEp(), sortedStatements.get(3).getNf(), sortedStatements.get(3).getNp()), 10E1);
		assertEquals(0.471, metric.value(sortedStatements.get(4).getEf(), sortedStatements.get(4).getEp(), sortedStatements.get(4).getNf(), sortedStatements.get(4).getNp()), 10E1);
		assertEquals(0.471, metric.value(sortedStatements.get(5).getEf(), sortedStatements.get(5).getEp(), sortedStatements.get(5).getNf(), sortedStatements.get(5).getNp()), 10E1);
		assertEquals(0.0, metric.value(sortedStatements.get(6).getEf(), sortedStatements.get(6).getEp(), sortedStatements.get(6).getNf(), sortedStatements.get(6).getNp()), 10E1);
		assertEquals(0.0, metric.value(sortedStatements.get(7).getEf(), sortedStatements.get(7).getEp(), sortedStatements.get(7).getNf(), sortedStatements.get(7).getNp()), 10E1);

		assertEquals(sourceLocation5, ((StatementSourceLocation)sortedStatements.get(0)).getLocation());
	}
}
