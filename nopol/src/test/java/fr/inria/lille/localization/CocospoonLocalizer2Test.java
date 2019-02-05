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

import java.util.*; //added by deheng

import static gov.nasa.jpf.util.test.TestJPF.assertEquals;
import static gov.nasa.jpf.util.test.TestJPF.assertTrue;

/**
 * Modified by deheng on Feb 1, 2019.
 * This test aims to expose the java.lang.RuntimeException when running test cases in Math_58, a real bug from defects4j. But this test file (CocospoonLocalizerTest.java) is created based on the version of Nopol of March 2017.
 * Use `mvn clean package -DskipTests` to build nopol and use `mvn test -Dtest=CocospoonLocalizerTest` to run this test.
 */
public class CocospoonLocalizerTest {

    @Test
    public void testOchiaiCoCoSpoonLocalizer() throws Exception {
        File[] sources = new File[]{new File("../Math_58/src/main/java")}; // This is the source file of Math_58 
	String tests="org.apache.commons.math.distribution.HypergeometricDistributionTest"; // This test is the targeted test that can expose the problem of cocospoon.
        URL[] classpath = new URL[]{
                new File("../Math_58/target/classes/").toURI().toURL(),
                new File("../Math_58/target/test-classes/").toURI().toURL()
        }; // This corresponds to the paths of java classes files.
	String[] testClasses = tests.split(", ");
        CocoSpoonBasedSpectrumBasedFaultLocalizer localizer = new CocoSpoonBasedSpectrumBasedFaultLocalizer(new NopolContext(sources, classpath, testClasses), new Ochiai());
        Map<SourceLocation, List<TestResult>> executedSourceLocationPerTest = localizer.getTestListPerStatement();

        SourceLocation sourceLocation1 = new SourceLocation("org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer", 741);

	//System.out.println("dale stmt 0:"+executedSourceLocationPerTest.size()+"   "+new ArrayList<>(executedSourceLocationPerTest.keySet()).get(0) +new ArrayList<>(executedSourceLocationPerTest.keySet()).get(0).getSuspiciousValue()  );
        assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation1));

        //List<SourceLocation> sortedStatements = new ArrayList<>(executedSourceLocationPerTest.keySet());

        //assertEquals(1, sortedStatements.get(0).getSuspiciousValue(), 10E-3);
    }

}
