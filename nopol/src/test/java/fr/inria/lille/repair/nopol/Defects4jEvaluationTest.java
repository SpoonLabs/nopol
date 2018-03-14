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

// to be run on Travis should be in less than 45 minutes
public class Defects4jEvaluationTest {

	@Test(timeout = FIVE_MINUTES_TIMEOUT)
	public void test_Lang44() throws Exception {
		if (!testShouldBeRun()) { return; }
		NopolContext nopolContext = nopolConfigFor("Lang44");
		nopolContext.setComplianceLevel(4);
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

	@Test(timeout = FIVE_MINUTES_TIMEOUT)
	public void test_Lang51() throws Exception {
		if (!testShouldBeRun()) { return; }
		NopolContext nopolContext = nopolConfigFor("Lang51");
		nopolContext.setComplianceLevel(4);
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

	@Test(timeout = FIVE_MINUTES_TIMEOUT)
	public void test_Lang53() throws Exception {
		if (!testShouldBeRun()) { return; }
		NopolContext nopolContext = nopolConfigFor("Lang53");
		nopolContext.setComplianceLevel(4);
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}


	@Test(timeout = FIVE_MINUTES_TIMEOUT)
	public void test_Lang58() throws Exception {
		if (!testShouldBeRun()) { return; }
		// many resources on the internet say it's "maven.compiler.source", but it's actually maven.compile.source"
		NopolContext nopolContext = nopolConfigFor("Lang58", "-Dproject.build.sourceEncoding=ISO-8859-1 -Dmaven.compile.source=1.4 -Dmaven.compile.testSource=1.4");
		nopolContext.setComplianceLevel(4);
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}
	
}




