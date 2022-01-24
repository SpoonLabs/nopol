package fr.inria.lille.repair.nopol;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.synth.RepairType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static fr.inria.lille.repair.nopol.Defects4jUtils.FIVE_MINUTES_TIMEOUT;
import static fr.inria.lille.repair.nopol.Defects4jUtils.TEN_MINUTES_TIMEOUT;
import static fr.inria.lille.repair.nopol.Defects4jUtils.nopolConfigFor;
import static fr.inria.lille.repair.nopol.Defects4jUtils.testShouldBeRun;
import static org.junit.Assert.assertEquals;

// this is not run in CI because the memory is too small on Github actions
public class Defects4jEvaluationTest {

	@Test(timeout = FIVE_MINUTES_TIMEOUT)
	public void test_Lang44() throws Exception {
		// Defects4J Lang44 has been manually ported to Java 6 by Martin
		if (System.getenv("GITHUB_HEAD_REF") != null) { return; } // avoiding Java heap space error in GithubAction
		NopolContext nopolContext = nopolConfigFor("Lang44-Java6", "-Dproject.build.sourceEncoding=ISO-8859-1 -Dmaven.compile.source=1.6 -Dmaven.compile.testSource=1.6 -Dmaven.compile.target=1.6");
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

	@Test(timeout = FIVE_MINUTES_TIMEOUT)
	public void test_Lang51() throws Exception {
		// Defects4J Lang51 has been manually ported to Java 6 by Martin
		if (System.getenv("GITHUB_HEAD_REF") != null) { return; } // avoiding Java heap space error in GithubAction
		NopolContext nopolContext = nopolConfigFor("Lang51-Java6", "-Dproject.build.sourceEncoding=ISO-8859-1 -Dmaven.compile.source=1.6 -Dmaven.compile.testSource=1.6 -Dmaven.compile.target=1.6");
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

	@Test(timeout = FIVE_MINUTES_TIMEOUT)
	public void test_Lang53() throws Exception {
		// Defects4J Lang53 has been manually ported to Java 6 by Martin
		if (System.getenv("GITHUB_HEAD_REF") != null) { return; } // avoiding Java heap space error in GithubAction
		NopolContext nopolContext = nopolConfigFor("Lang53-Java6", "-Dproject.build.sourceEncoding=ISO-8859-1 -Dmaven.compile.source=1.6 -Dmaven.compile.testSource=1.6 -Dmaven.compile.target=1.6");
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}


	@Test(timeout = FIVE_MINUTES_TIMEOUT)
	public void test_Lang58() throws Exception {
		// Defects4J Lang58 has been manually ported to Java 6 by Martin
		if (System.getenv("GITHUB_HEAD_REF") != null) { return; } // Java Heap Space error in GithubAction
		// many resources on the internet say it's "maven.compiler.source", but it's actually maven.compile.source"
		NopolContext nopolContext = nopolConfigFor("Lang58-Java6", "-Dproject.build.sourceEncoding=ISO-8859-1 -Dmaven.compile.source=1.6 -Dmaven.compile.testSource=1.6 -Dmaven.compile.target=1.6");
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

	// we don't support Chart3 anymore because it is based on Ant, and
	// porting Ant to a newer Java version is useless
//	@Test(timeout = FIVE_MINUTES_TIMEOUT)
//	public void test_Chart3() throws Exception {
//		if (!testShouldBeRun()) { return; }
//		NopolContext nopolContext = nopolConfigFor("Chart3", "-Dproject.build.sourceEncoding=ISO-8859-1 -Dmaven.compile.source=1.6 -Dmaven.compile.testSource=1.6 -Dmaven.compile.target=1.6");
//		nopolContext.setLocalizer(NopolContext.NopolLocalizer.COCOSPOON);
//
//		// we take only the failing test case
//		nopolContext.setProjectTests(new String[]{"org.jfree.data.time.junit.TimeSeriesTests#testCreateCopy3"});
//
//		NopolResult result = new NoPol(nopolContext).build();
//		assertEquals(1, result.getPatches().size());
//	}

}




