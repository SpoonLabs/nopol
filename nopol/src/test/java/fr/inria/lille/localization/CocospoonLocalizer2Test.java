package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import java.util.*; //added by deheng
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.util.Properties;

import static gov.nasa.jpf.util.test.TestJPF.assertEquals;
import static gov.nasa.jpf.util.test.TestJPF.assertTrue;

/**
 * Modified by deheng on Feb 1, 2019.
 * This test aims to expose the java.lang.RuntimeException when running test cases in Math_58, a real bug from defects4j. But this test file (CocospoonLocalizerTest.java) is created based on the version of Nopol of March 2017.
 * Use `mvn clean package -DskipTests` to build nopol and use `mvn test -Dtest=CocospoonLocalizerTest` to run this test.
 */

// But this java.lang.RuntimeException seems to only happen when the Nopol and CoCospoon are built/installed in JDK 1.7. When Nopol and CoCoSpoon are built/installed in JDK 1.8, this test will pass.
public class CocospoonLocalizer2Test {

    @Test
    public void testOchiaiCoCoSpoonLocalizer() throws Exception {
	String bug_id="Math58";
	String mvn_option="";
	if(!new File(bug_id).exists()){
		String command = "mkdir " + bug_id +";\n cd " + bug_id + ";\n git init;\n git fetch https://github.com/Spirals-Team/defects4j-repair " + bug_id + ":" + bug_id + ";\n git checkout "+bug_id+";\n mvn -q test -DskipTests "+mvn_option+";\n mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt";
		//String command = "mkdir ../Math58 && cd ../Math58 &&  defects4j checkout -p Math -v 58b -w . && cd ../nopol"; // Travis does not support `defects4j` command.
		System.out.println("The command is:"+command);
		Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
		p.waitFor();
		if (p.exitValue() != 0) {
			System.out.println("defects4j download Math_58 failed.");
		}
	}
	/*Process p2 = Runtime.getRuntime().exec(new String[]{"sh", "-c", "cd ../Math_58 && defects4j test && cd ../nopol"});
        p2.waitFor();
        if (p2.exitValue() != 0) {
                System.out.println("defects4j test Math_58 failed.");
        }*/
        File[] sources = new File[]{new File("Math58/src/main/java")}; // This is the source file of Math_58 
	String tests="org.apache.commons.math.distribution.HypergeometricDistributionTest,org.apache.commons.math.optimization.fitting.GaussianFitterTest"; // The first test case(Hyper...) is the specified test that can expose the problem of cocospoon (RuntimeException).
        URL[] classpath = new URL[]{
                new File("Math58/target/classes/").toURI().toURL(),
                new File("Math58/target/test-classes/").toURI().toURL()
        }; // This corresponds to the paths of java classes files.
	String[] testClasses = tests.split(",");
        CocoSpoonBasedSpectrumBasedFaultLocalizer localizer = new CocoSpoonBasedSpectrumBasedFaultLocalizer(new NopolContext(sources, classpath, testClasses), new Ochiai());
        Map<SourceLocation, List<TestResult>> executedSourceLocationPerTest = localizer.getTestListPerStatement();

	for(SourceLocation location : executedSourceLocationPerTest.keySet()){
		System.out.println(location);
	}

	assertTrue(executedSourceLocationPerTest.size()>0);  //If the runtimeException does not occur when running the specified test case, it means this test pass.

        //SourceLocation sourceLocation1 = new SourceLocation("org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer", 741);
	//System.out.println("dale stmt 0:"+executedSourceLocationPerTest.size()+"   "+new ArrayList<>(executedSourceLocationPerTest.keySet()).get(0) +new ArrayList<>(executedSourceLocationPerTest.keySet()).get(0).getSuspiciousValue()  );
        //assertTrue(executedSourceLocationPerTest.keySet().contains(sourceLocation1));
        //List<SourceLocation> sortedStatements = new ArrayList<>(executedSourceLocationPerTest.keySet());
        //assertEquals(1, sortedStatements.get(0).getSuspiciousValue(), 10E-3);
    }

}
