package fr.inria.lille.localization;

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
public class DumbLocalizerTest {

	@Test
	public void testDumbLocalizer() throws Exception {

		/* test that the localizer return executed statement */

		File[] sources = new File[]{new File("../test-projects/src/main/java/nopol_examples/nopol_example_1/NopolExample.java")};
		URL[] classpath = new URL[]{
				new File("../test-projects/target/classes").toURI().toURL(),
				new File("../test-projects/target/test-classes").toURI().toURL()
		};
		String [] testClasses = new String[] {"nopol_examples.nopol_example_1.NopolExampleTest"};
		NopolContext nopolContext = new NopolContext(sources, classpath, testClasses);
		DumbFaultLocalizerImpl localizer = new DumbFaultLocalizerImpl(nopolContext);

		Map<SourceLocation, List<TestResult>> executedSourceLocationPerTest = localizer.getTestListPerStatement();
		assertEquals(8, executedSourceLocationPerTest.keySet().size());

		assertTrue(executedSourceLocationPerTest.keySet().contains(new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 16)));
	}
}
