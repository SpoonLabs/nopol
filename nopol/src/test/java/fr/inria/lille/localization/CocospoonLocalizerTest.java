package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.junit.Assert;
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
public class CocospoonLocalizerTest {

    @Test
    public void testOchiaiCoCoSpoonLocalizer() throws Exception {

		/* test OchiaiCoCoSpoonLocalizer : the SourceLocation must be sorted following the Ochiai metric */

        File[] sources = new File[]{new File("../test-projects/src/main/java/nopol_examples/nopol_example_1/NopolExample.java")};
        URL[] classpath = new URL[]{
                new File("../test-projects/target/classes").toURI().toURL(),
                new File("../test-projects/target/test-classes").toURI().toURL()
        };
        String[] testClasses = new String[]{"nopol_examples.nopol_example_1.NopolExampleTest"};
        CocoSpoonBasedSpectrumBasedFaultLocalizer localizer = new CocoSpoonBasedSpectrumBasedFaultLocalizer(new NopolContext(sources, classpath, testClasses), new Ochiai());
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

        List<? extends StatementSourceLocation> sortedStatements = localizer.getStatements();

        assertEquals(0.534, sortedStatements.get(0).getSuspiciousness(), 10E-3);
        assertEquals(0.5, sortedStatements.get(1).getSuspiciousness(), 10E-3);
        assertEquals(0.471, sortedStatements.get(2).getSuspiciousness(), 10E-3);
        assertEquals(0.471, sortedStatements.get(3).getSuspiciousness(), 10E-3);
        assertEquals(0.471, sortedStatements.get(4).getSuspiciousness(), 10E-3);
        assertEquals(0.471, sortedStatements.get(5).getSuspiciousness(), 10E-3);
        assertEquals(0.0, sortedStatements.get(6).getSuspiciousness(), 10E-3);
        assertEquals(0.0, sortedStatements.get(7).getSuspiciousness(), 10E-3);

        assertEquals(sourceLocation5, sortedStatements.get(0).getLocation());
    }

    @Test
    public void test2() throws Exception {

        // specifc tests (eg #test2) works

        File[] sources = new File[]{new File("../test-projects/src/main/java/nopol_examples/nopol_example_1/NopolExample.java")};
        URL[] classpath = new URL[]{
                new File("../test-projects/target/classes").toURI().toURL(),
                new File("../test-projects/target/test-classes").toURI().toURL()
        };
        String[] testClasses = new String[]{"nopol_examples.nopol_example_1.NopolExampleTest#test1"};

        NopolContext nopolContext = new NopolContext(sources, classpath, testClasses);
        FaultLocalizer localizer = new CocoSpoonBasedSpectrumBasedFaultLocalizer(nopolContext);
        Map<SourceLocation, List<TestResult>> executedSourceLocationPerTest = localizer.getTestListPerStatement();
        Assert.assertEquals(5, executedSourceLocationPerTest.keySet().size());

        SourceLocation sourceLocation2 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 13);
        SourceLocation sourceLocation3 = new SourceLocation("nopol_examples.nopol_example_1.NopolExample", 12);

        for (SourceLocation loc : executedSourceLocationPerTest.keySet()) {
            for (TestResult res : executedSourceLocationPerTest.get(loc)) {
                Assert.assertEquals("nopol_examples.nopol_example_1.NopolExampleTest#test1", res.getTestCase().toString());
            }
        }

        System.out.println(executedSourceLocationPerTest);
        Assert.assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation2));
        Assert.assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation3));


    }

}
