package fr.inria.lille.commons.trace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.config.NopolContext.NopolLocalizer;
import fr.inria.lille.repair.nopol.NoPol;
import fr.inria.lille.repair.nopol.NopolResult;

/**
 * 
 * These tests expose Nopol non-deterministic issues.
 * 
 * Test1: find_patch_with_junit_timeout_test: Some tests fail due to Junit
 * timeout but some succeed. No runtime value collected in failing tests.
 * 
 * Test2: find_patch_no_junit_timeout_test We removed Junit timeout. But the
 * results are still non-determined. Comparing the specifications we collected,
 * we found out same input value but with different outputs. This could the
 * reason that result in non-determine results of Nopol.
 *
 */
public class NonDeterministicSpecificationTest {

	/**
	 * In this test, we run Nopol against find_in_sorted program at most 20 times to
	 * find patches. Once we have two results that patch found and patch not found,
	 * the test stop.
	 */

	@Test
	public void find_patch_with_junit_timeout_test() throws MalformedURLException {
		String testClassesName = "nopol_examples.quixbug.FIND_IN_SORTED_TIMEOUT_TEST";
		Set<Boolean> patchResultSet = new HashSet<Boolean>();
		for (int i = 0; i < 20; i++) {
			if (patchResultSet.size() == 2) {
				break;
			}
			Boolean hasPatch = find_in_sorted_program(testClassesName);
			patchResultSet.add(hasPatch);
			System.out.println(hasPatch);
		}
		assertEquals(2, patchResultSet.size());
	}

	
	// Comment out this test because running this test requires long time
	/*
	 @Test
	 public void find_patch_no_junit_timeout_test() throws MalformedURLException {
	 String testClassesName = "nopol_examples.quixbug.FIND_IN_SORTED_TEST";
	   Set<Boolean> patchResultSet = new HashSet<Boolean>();
	 for (int i = 0; i < 20; i++) {
	 if (patchResultSet.size() == 2) {
	 break;
	 }
	 Boolean hasPatch = find_in_sorted_program(testClassesName);
	 patchResultSet.add(hasPatch);
	 System.out.println(hasPatch);
	 }
	 assertEquals(2, patchResultSet.size());
	 }
	 */

	private boolean find_in_sorted_program(String testClassesName) throws MalformedURLException {
		File[] sources = new File[] {
				new File("../test-projects/src/main/java/nopol_examples/quixbug/FIND_IN_SORTED.java") };
		URL[] classpath = new URL[] { new File("../test-projects/target/classes").toURI().toURL(),
				new File("../test-projects/target/test-classes").toURI().toURL() };
		String[] testClasses = new String[] { testClassesName };
		NopolContext nopolContext = new NopolContext(sources, classpath, testClasses);
		nopolContext.setLocalizer(NopolLocalizer.COCOSPOON);
		NoPol nopol = new NoPol(nopolContext);
		nopol.build();
		NopolResult result = nopol.getNopolResult();
		return result.getPatches().size() == 0 ? false : true;
	}


}
