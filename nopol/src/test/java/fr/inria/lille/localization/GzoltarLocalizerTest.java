package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.*;

import static gov.nasa.jpf.util.test.TestJPF.assertEquals;
import static gov.nasa.jpf.util.test.TestJPF.assertFalse;

/**
 * Created by bdanglot on 10/4/16.
 */
public class GzoltarLocalizerTest {

	@Test
	public void testGzoltarLocalizer() throws Exception {

		/* test GzoltarLocalizer : the SourceLocation must be sorted following the Ochiai metric */

		final Metric metric = new Ochiai();

		File[] sources = new File[]{new File("../test-projects/src/main/java/nopol_examples/nopol_example_1/NopolExample.java")};
		URL[] classpath = new URL[]{
				new File("../test-projects/target/classes").toURI().toURL(),
				new File("../test-projects/target/test-classes").toURI().toURL()
		};
		String[] testClasses = new String[]{"nopol_examples.nopol_example_1.NopolExampleTest"};
		Config config = new Config();
		config.setLocalizer(Config.NopolLocalizer.OCHIAI);
		GZoltarFaultLocalizer localizer = new GZoltarFaultLocalizer(classpath, Collections.EMPTY_LIST, testClasses, metric);
		Map<SourceLocation, List<TestResult>> executedSourceLocationPerTest = localizer.getTestListPerStatement();
		assertEquals(8, executedSourceLocationPerTest.keySet().size());

		List<AbstractStatement> sortedStatements = localizer.getStatements();
		List<AbstractStatement> unsortedStatements = localizer.getStatements();

		Collections.shuffle(unsortedStatements);
		assertFalse(sortedStatements.equals(unsortedStatements));

		Collections.sort(unsortedStatements, new Comparator<AbstractStatement>() {
					@Override
					public int compare(AbstractStatement o1, AbstractStatement o2) {
						return Double.compare(metric.value(o1.getEf(), o1.getEp(), o1.getNf(), o1.getNp()),
								metric.value(o2.getEf(), o2.getEp(), o2.getNf(), o2.getNp()));
					}
				}
		);

		//Since some metric are equals, the order can not be same, but the suspiciousness is.
		for (int i = 0; i < sortedStatements.size(); i++) {
			assertEquals(metric.value(sortedStatements.get(i).getEf(), sortedStatements.get(i).getEp(), sortedStatements.get(i).getNf(), sortedStatements.get(i).getNp()),
					metric.value(unsortedStatements.get(i).getEf(), unsortedStatements.get(i).getEp(), unsortedStatements.get(i).getNf(), unsortedStatements.get(i).getNp())
			);
		}
	}

}
