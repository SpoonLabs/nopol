package fr.inria.lille.repair.nopol;

import fr.inria.lille.repair.common.config.NopolContext;
import org.junit.Test;

import static fr.inria.lille.repair.nopol.Defects4jUtils.FIFTEEN_MINUTES_TIMEOUT;
import static fr.inria.lille.repair.nopol.Defects4jUtils.FIVE_MINUTES_TIMEOUT;
import static fr.inria.lille.repair.nopol.Defects4jUtils.TEN_MINUTES_TIMEOUT;
import static fr.inria.lille.repair.nopol.Defects4jUtils.nopolConfigFor;
import static fr.inria.lille.repair.nopol.Defects4jUtils.testShouldBeRun;
import static org.junit.Assert.assertEquals;

public class Defects4jEvaluationMathTest {

	@Test(timeout = FIVE_MINUTES_TIMEOUT *2)
	public void test_Math32() throws Exception {
		// On Travis 454sec
		if (!testShouldBeRun()) { return; }
		NopolContext nopolContext = nopolConfigFor("Math32");
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

	@Test(timeout = FIVE_MINUTES_TIMEOUT *2)
	public void test_Math33() throws Exception {
		// on Travis: 458sec
		if (!testShouldBeRun()) { return; }
		NopolContext nopolContext = nopolConfigFor("Math33");
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

	// Math40 Timeout on Travis on March 12
//	@Test(timeout = FIVE_MINUTES_TIMEOUT*2)
//	public void test_Math40() throws Exception {
//		if (!testShouldBeRun()) { return; }
//		NopolContext nopolContext = nopolConfigFor("Math40");
//		NopolResult result = new NoPol(nopolContext).build();
//		assertEquals(1, result.getPatches().size());
//	}

	@Test(timeout = FIVE_MINUTES_TIMEOUT *2)
	public void test_Math42() throws Exception {
		// on Travis: 215sec
		if (!testShouldBeRun()) { return; }
		NopolContext nopolContext = nopolConfigFor("Math42");
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

	@Test(timeout = TEN_MINUTES_TIMEOUT)
	public void test_Math49() throws Exception {
		// on Travis: 179sec
		if (!testShouldBeRun()) { return; }
		NopolContext nopolContext = nopolConfigFor("Math49");
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

	@Test(timeout = FIFTEEN_MINUTES_TIMEOUT)
	public void test_Math50() throws Exception {
		// on Travis: 8 minutes, sometimes more than 10 minutes
		if (!testShouldBeRun()) { return; }
		NopolContext nopolContext = nopolConfigFor("Math50");
		NopolResult result = new NoPol(nopolContext).build();
		assertEquals(1, result.getPatches().size());
	}

//	@Test(timeout = FIVE_MINUTES_TIMEOUT *2)
//	public void test_Math57() throws Exception {
//		if (!testShouldBeRun()) { return; }
//		NopolContext nopolContext = nopolConfigFor("Math57");
//		NopolResult result = new NoPol(nopolContext).build();
//		assertEquals(1, result.getPatches().size());
//	}

//	@Test(timeout = FIVE_MINUTES_TIMEOUT *2)
//	public void test_Math58() throws Exception {
//		// on Travis: 169sec
//		// it seems to crash
//		if (!testShouldBeRun()) { return; }
//		NopolContext nopolContext = nopolConfigFor("Math58");
//		NopolResult result = new NoPol(nopolContext).build();
//		assertEquals(1, result.getPatches().size());
//	}

//	@Test(timeout = FIVE_MINUTES_TIMEOUT *2)
//	public void test_Math69() throws Exception {
//		// on Travis: 22sec
//		if (!testShouldBeRun()) { return; }
//		NopolContext nopolContext = nopolConfigFor("Math69");
//		NopolResult result = new NoPol(nopolContext).build();
//		assertEquals(1, result.getPatches().size());
//	}
//
//	@Test(timeout = FIVE_MINUTES_TIMEOUT *2)
//	public void test_Math71() throws Exception {
//		if (!testShouldBeRun()) { return; }
//		NopolContext nopolContext = nopolConfigFor("Math71");
//		NopolResult result = new NoPol(nopolContext).build();
//		assertEquals(1, result.getPatches().size());
//	}
//
//	@Test(timeout = FIVE_MINUTES_TIMEOUT *2)
//	public void test_Math73() throws Exception {
//		if (!testShouldBeRun()) { return; }
//		NopolContext nopolContext = nopolConfigFor("Math73");
//		NopolResult result = new NoPol(nopolContext).build();
//		assertEquals(1, result.getPatches().size());
//	}

//	@Test(timeout = FIVE_MINUTES_TIMEOUT *2)
//	public void test_Math78() throws Exception {
//		if (!testShouldBeRun()) { return; }
//		NopolContext nopolContext = nopolConfigFor("Math78");
//		NopolResult result = new NoPol(nopolContext).build();
//		assertEquals(1, result.getPatches().size());
//	}

}



