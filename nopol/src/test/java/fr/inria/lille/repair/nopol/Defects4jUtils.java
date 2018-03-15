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

import static org.junit.Assert.assertEquals;

public class Defects4jUtils {
	private Defects4jUtils(){} // only static methods

	public final static int FIVE_MINUTES_TIMEOUT =5*60*1000;// 5 minutes in millisecs
	public final static int TEN_MINUTES_TIMEOUT =10*60*1000;// 10 minutes in millisecs
	public final static int FIFTEEN_MINUTES_TIMEOUT =15*60*1000;// 15 minutes in millisecs

	public static boolean testShouldBeRun() {
		if (System.getenv("NOPOL_EVAL_DEFECTS4J")==null) {
			return false;
		}
		return true;
	}

	public static NopolContext nopolConfigFor(String bug_id) throws Exception {
		return nopolConfigFor(bug_id, "");
	}
	public static NopolContext nopolConfigFor(String bug_id, String mvn_option) throws Exception {
		String folder = "unknown";
		if (!new File(bug_id).exists()) {
			String command = "mkdir " + bug_id +";\n cd " + bug_id + ";\n git init;\n git fetch https://github.com/Spirals-Team/defects4j-repair " + bug_id + ":" + bug_id + ";\n git checkout "+bug_id+";\n mvn -q test -DskipTests "+mvn_option+";\n mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt";
			System.out.println(command);
			Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
			p.waitFor();
			String output = IOUtils.toString(p.getInputStream());
			String errorOutput = IOUtils.toString(p.getErrorStream());
			System.out.println(output);
			System.err.println(errorOutput);
		}

		Properties prop = new Properties();
		prop.load(new FileInputStream(bug_id+"/defects4j.build.properties"));

		NopolContext nopolContext = new NopolContext();
		nopolContext.setIdentifier(bug_id);
		String src = bug_id+"/"+prop.get("d4j.dir.src.classes");
		nopolContext.setProjectSourcePath(new File[]{new File(src)});

		// getting the classpath from Maven
		List<URL> cp = new ArrayList<>();
		for (String entry : FileUtils.readFileToString(new File(bug_id+"/cp.txt")).split(new String(new char[]{File.pathSeparatorChar}))) {
			cp.add(new File(entry).toURL());
		}
		cp.add(new File(bug_id+"/target/classes").toURL());
		cp.add(new File(bug_id+"/target/test-classes").toURL());
		System.out.println(cp);
//
		nopolContext.setProjectClasspath(cp.toArray(new URL[0]));

		nopolContext.setType(RepairType.PRE_THEN_COND);

		SolverFactory.setSolver("z3", TestUtility.solverPath);

		return nopolContext;
	}
}



