package fr.inria.lille.repair.nopol;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.synth.RepairType;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TseEvaluationTest {

	private final static int TIMEOUT=5*60*1000;// 5 minutes in millisecs
	public boolean testShouldBeRun() {
		if (System.getenv("NOPOL_EVAL_TSE")==null || !new File("../nopol-experiments").exists()) {
			return false;
		}
		return true;
	}

	public void testTSEBug(String bug_id) throws Exception {
		String folder = "unknown";
		if (bug_id.startsWith("cm") || bug_id.startsWith("pm")) {
			folder = "math";
		}
		if (bug_id.startsWith("cl") || bug_id.startsWith("pl")) {
			folder = "lang";
		}

		JSONTokener tokener = new JSONTokener(new File("../nopol-experiments/data/projects/"+folder+"/bugs/" + bug_id + ".json").toURL().openStream());
		JSONObject root = new JSONObject(tokener);

		//JSONArray s =
		NopolContext nopolContext = new NopolContext();
		String src = "../nopol-experiments/dataset/" + bug_id + "/" + root.getJSONObject("path").getString("source");
		nopolContext.setProjectSourcePath(new File[]{new File(src)});

		// setting the Java version of the project to repair, required for TSE_CL1 for instance
		if (root.has("java")) {
			nopolContext.setComplianceLevel(Integer.parseInt(root.getJSONObject("java").getString("version").substring(2)));
		}

		URL[] cp = new URL[root.getJSONArray("dependencies").length()+2];
		cp[0] = new File("../nopol-experiments/dataset/"+bug_id+"/"+"target/classes/").toURL();
		cp[1] = new File("../nopol-experiments/dataset/"+bug_id+"/"+"target/test-classes/").toURL();
		for (int i = 0; i <root.getJSONArray("dependencies").length(); i++)
		{
			cp[i+2] = new File("../nopol-experiments/data/lib/"+root.getJSONArray("dependencies").getString(i)).toURL();
		}

		if (root.has("tests")) {
			String[] tests = new String[root.getJSONArray("tests").length()];
			for (int i = 0; i <root.getJSONArray("tests").length(); i++)
			{
				tests[i] = root.getJSONArray("tests").getString(i);
			}				;
			nopolContext.setProjectTests(tests);
		}

		nopolContext.setProjectClasspath(cp);
		//nopolContext.setLocalizer(NopolContext.NopolLocalizer.COCOSPOON);
		nopolContext.setType(RepairType.PRECONDITION);
		if ("condition".equals(root.getString("type"))) {
				nopolContext.setType(RepairType.CONDITIONAL);
		}
		SolverFactory.setSolver("z3", TestUtility.solverPath);
		NoPol nopol = new NoPol(nopolContext);
		NopolResult result = nopol.build();

		assertEquals(1, result.getPatches().size());
	}

	@Test(timeout = TIMEOUT)
	public void test_cm1() throws Exception {
		if (testShouldBeRun()) testTSEBug("cm1");
	}

	@Test(timeout = TIMEOUT)
	public void test_cm2() throws Exception {
		if (testShouldBeRun()) testTSEBug("cm2");
	}

	@Test(timeout = TIMEOUT)
	public void test_cm3() throws Exception {
		if (testShouldBeRun()) testTSEBug("cm3");
	}

	@Test(timeout = TIMEOUT)
	public void test_cm4() throws Exception {
		if (testShouldBeRun()) testTSEBug("cm4");
	}

	@Test(timeout = TIMEOUT)
	public void test_cm5() throws Exception {
		// ignored, there is a regression in Gzoltar which crashes with NPE
		// if (testShouldBeRun())
		// testTSEBug("cm5");
	}

	@Test(timeout = TIMEOUT)
	public void test_cm6() throws Exception {
		if (testShouldBeRun())
			testTSEBug("cm6");
	}

	@Test(timeout = TIMEOUT)
	public void test_cm7() throws Exception {
		if (testShouldBeRun())
			testTSEBug("cm7");
	}

	@Test(timeout = TIMEOUT)
	public void test_cm10() throws Exception {
		// note that this one has a "tests" configuration which limits the scope of the search
		if (testShouldBeRun()) testTSEBug("cm10");
	}

	@Test(timeout = TIMEOUT)
	public void test_cl1() throws Exception {
		if (testShouldBeRun())
			testTSEBug("cl1");
	}

	@Test(timeout = TIMEOUT)
	public void test_cl2() throws Exception {
		if (testShouldBeRun())
			testTSEBug("cl2");
	}

	@Test(timeout = TIMEOUT)
	public void test_cl3() throws Exception {
		if (testShouldBeRun())
			testTSEBug("cl3");
	}

	@Test(timeout = TIMEOUT)
	public void test_cl4() throws Exception {
		if (testShouldBeRun())
			testTSEBug("cl4");
	}

	@Test(timeout = TIMEOUT)
	public void test_cl5() throws Exception {
		if (testShouldBeRun())
		testTSEBug("cl5");
	}

	@Test(timeout = TIMEOUT)
	public void test_cl6() throws Exception {
		// CL6 is a bug that cannot be repaired in the TSE paper
		// in the paper we say timeout
		// we this was a mistake, it's actually an impossible synthesis
		//if (testShouldBeRun())
		//testTSEBug("cl6");
	}

	@Test(timeout = TIMEOUT)
	public void test_pl1() throws Exception {
		if (testShouldBeRun()) testTSEBug("pl1");
	}

	@Test(timeout = TIMEOUT)
	public void test_pl2() throws Exception {
		if (testShouldBeRun())
			testTSEBug("pl2");
	}

	@Test(timeout = TIMEOUT)
	public void test_pl3() throws Exception {
		// PL3 now works!!! (it was reported as unfixed in TSE)
		if (testShouldBeRun()) testTSEBug("pl3");
	}

	@Test(timeout = TIMEOUT)
	public void test_pl4() throws Exception {
		// there is only a Kali patch for PL4
		// and Nopol does not return Kali patches anymore
		//if (testShouldBeRun())
		//	testTSEBug("pl4");
	}

	@Test(timeout = TIMEOUT)
	public void test_pm1() throws Exception {
		// pm1 cannot be fixed in TSE
		//if (testShouldBeRun()) testTSEBug("pm1");
	}

	@Test(timeout = TIMEOUT)
	public void test_pm2() throws Exception {
		// PM2 in the longest in TSE, it takes more than one minutes
		// TSE: 84 sec
		// on Martin's machine 70sec
		if (testShouldBeRun()) testTSEBug("pm2");
	}
}
