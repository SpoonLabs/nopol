package fr.inria.lille.repair.nopol;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import xxl.java.container.classic.MetaSet;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;
import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.Main;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.nopol.patch.Patch;
import fr.inria.lille.repair.symbolic.synth.StatementType;

public class NopolTest {

	@Ignore
	@Test
	public void math309() { 
		String rootFolder = "/Users/virtual/Desktop/data/projects/dataset-nopol/309/";
		String srcFolder = rootFolder + "src/main/java";
		String classpath = rootFolder + "target/test-classes/" + ":" + rootFolder + "target/classes/";
		String solver = "cvc4";
		String solverPath = "/Users/virtual/Desktop/data/projects/nopol/nopol/lib/cvc4-1.4.2/cvc4_for_mac";
		Main.main(new String[] {"nopol", srcFolder, classpath, solver, solverPath });
		/* PATCH: CONDITIONAL (mean)<=(0) */
	}

	@Ignore
	@Test
	public void lang_6ed8e576c4e13ac3ea05a3c5422236ea3affb799() {
		String rootFolder = "/Users/virtual/Desktop/data/projects/dataset-nopol/lang-6ed8e576c4e13ac3ea05a3c5422236ea3affb799/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";
		String dependency = rootFolder + "lib/junit-3.8.jar";
		String classpath = binFolder + ":" + dependency;
		String solver = "z3";
		String solverPath = "/Users/virtual/Desktop/data/projects/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		String testClass = "org.apache.commons.lang.StringUtilsSubstringTest";
		Main.main(new String[] {"nopol", srcFolder, classpath, solver, solverPath, testClass });
		/* PATCH: CONDITIONAL (pos > (str.length()))||((len)<=(0)) */
	}
	
	@Ignore
	@Test
	public void percentile() { 
		String rootFolder = "/Users/virtual/Desktop/data/projects/dataset-nopol/Percentile/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";
		String dependencyA = rootFolder + "lib/commons-beanutils-1.7.0.jar";
		String dependencyB = rootFolder + "lib/commons-collections-2.0.jar";
		String dependencyC = rootFolder + "lib/commons-discovery-0.4.jar";
		String dependencyD = rootFolder + "lib/commons-lang-2.1.jar";
		String dependencyE = rootFolder + "lib/commons-logging-1.1.1.jar";
		String dependencyF = rootFolder + "lib/junit-3.8.jar";
		String classpath = binFolder + ":" + dependencyA + ":" + dependencyB + ":" + dependencyC + ":" + dependencyD + ":" + dependencyE + ":" + dependencyF;
		String solver = "z3";
		String solverPath = "/Users/virtual/Desktop/data/projects/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		Main.main(new String[] {"nopol", srcFolder, classpath, solver, solverPath });
		/* PATCH: CONDITIONAL (intPos)==(sorted.length), (fpos)==(n) */
	}
	
	@Test
	public void exampleNopolMain() {
		SolverFactory solverFactory = SolverFactory.instance();
		String solverName = solverFactory.solverName();
		String solverPath = solverFactory.solverPath();
		NoPolLauncher.main(new String[] {"condition", solverName, solverPath });
	}
	
	@Test
	public void example1Fix() {
		Collection<String> failedTests = asList("test5", "test6");
		Patch patch = test(1, 12, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(index)<=(0)", "(index)<(1)", "(index)<=(-1)");
	}
	
	@Test
	public void example2Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test4", "test5", "test6", "test7");
		Patch patch = test(2, 11, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(a)<=(b)", "(a)<(b)", "(1)<=((b - a))", "(0)<=((b - a))", "(1)<((b - a))", "(0)<((b - a))");
	}
	
	@Test
	public void example3Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9");
		Patch patch = test(3, 11, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(tmp)==(0)", "(0)==(tmp)");
	}
	
	@Test
	public void example4Fix() {
		Collection<String> failedTests = asList("test5");
		test(4, 23, StatementType.PRECONDITION, failedTests);
	}
	
	@Test
	public void example5Fix() {
		Collection<String> failedTests = asList("test4", "test5");
		Patch patch = test(5, 20, StatementType.PRECONDITION, failedTests);
		fixComparison(patch, "(-1)<=(a)", "(1)<=(a)", "(r)<=(a)", "(-1)<(a)", "(0)<=(a)");
	}
	
	@Test
	public void example6Fix() {
		Collection<String> failedTests = asList("test1", "test2", "test3", "test4", "test6");
		Patch patch = test(6, 7, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(a)<(b)", "(a)<=(b)");
	}
	
	@Test
	public void example7Fix() {
		Collection<String> failedTests = asList("test1");
		Patch patch = test(7, 21, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "(intermediaire == 0)&&((1)<=((-1)+((a)-(1))))", 
				"(intermediaire == 0)&&((!(((a)+(-1))<=(1)))||((((a)+(-1))-(-1))==(intermediaire)))",
				"((1)<=((1)-(a)))||((intermediaire == 0)&&((intermediaire)!=(((1)-(a))+(1))))",
				"(intermediaire == 0)&&((((1)-((a)+(0)))<(-1))||(((a)+(0))!=((a)+(0))))",
				"!((((a)+(-1))<=(1))||((0)!=(intermediaire)))",
				"(!(((1)==(intermediaire))||(((a)+(-1))<=(1))))&&(!(((1)==(intermediaire))||(((a)+(-1))<=(1))))",
				"!(((intermediaire)!=(0))||(((1)-(-1))==(a)))");
	}
	
	@Test
	public void example8Fix() {
		Collection<String> failedTests = asList("test_2");
		Patch patch = test(8, 12, StatementType.CONDITIONAL, failedTests);
		fixComparison(patch, "((a * b))<=(100)");
	}
	
	
	@Test
	public void CM1() {
		String rootFolder = "../../Nopol_dataset/cm1/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";
		String dependencyB = libFolder + "commons-beanutils-1.7.0.jar";
		String dependencyC = libFolder + "commons-collections-2.0.jar";
		String dependencyD = libFolder + "commons-discovery-0.4.jar";
		String dependencyE = libFolder + "commons-lang-2.1.jar";
		String dependencyF = libFolder + "commons-logging-1.1.1.jar";

		String classpath = binFolder + ":" + dependencyA + ":" + dependencyB
				+ ":" + dependencyC + ":" + dependencyD + ":" + dependencyE
				+ ":" + dependencyF;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";

		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher
				.launch(FileLibrary.openFrom(srcFolder),
						JavaLibrary.classpathFrom(classpath),
						StatementType.CONDITIONAL,
						new String[] { "org.apache.commons.math.stat.univariate.rank.PercentileTest" });
		fixComparison(patch.get(0), "(intPos)==(sorted.length), (fpos)==(n)",
				"(dif)==(begin)", "(intPos)==(3)");
		/* PATCH: CONDITIONAL (intPos)==(sorted.length), (fpos)==(n) */
	}

	@Test
	public void CM2() {
		String rootFolder = "../../Nopol_dataset/cm2/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";
		String dependencyB = libFolder + "commons-beanutils-1.5.jar";
		String dependencyC = libFolder + "commons-collections-3.0.jar";
		String dependencyD = libFolder + "commons-discovery-SNAPSHOT.jar";
		String dependencyE = libFolder + "commons-lang-2.0.jar";
		String dependencyF = libFolder + "commons-logging-1.0.3.jar";

		String classpath = binFolder + ":" + dependencyA + ":" + dependencyB
				+ ":" + dependencyC + ":" + dependencyD + ":" + dependencyE
				+ ":" + dependencyF;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.PRECONDITION,
				new String[] { "org.apache.commons.math.util.MathUtilsTest" });
		fixComparison(patch.get(0), "(ZB)!=(n)");

		/* PATCH: PRECONDITIONAL (ZB)!=(n) */
	}

	@Test
	public void CM3() {
		String rootFolder = "../../Nopol_dataset/cm3/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";
		String dependencyB = libFolder + "commons-discovery-SNAPSHOT.jar";
		String dependencyC = libFolder + "commons-logging-1.0.3.jar";

		String classpath = binFolder + ":" + dependencyA + ":" + dependencyB
				+ ":" + dependencyC;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.PRECONDITION,
				new String[] { "org.apache.commons.math.util.MathUtilsTest" });
		fixComparison(patch.get(0), "(ns)!=(n)");
		/* PATCH: PRECONDITIONAL (ns)==(n) */
	}

	@Test
	public void CM4() {
		String rootFolder = "../../Nopol_dataset/cm4/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";
		String dependencyB = libFolder + "commons-discovery-SNAPSHOT.jar";
		String dependencyC = libFolder + "commons-logging-1.0.3.jar";

		String classpath = binFolder + ":" + dependencyA + ":" + dependencyB
				+ ":" + dependencyC;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL, new String[] {});
		fixComparison(patch.get(0), "(ns)!=(n)");
		/* PATCH: PRECONDITIONAL (ZB)!=(n) */
	}

	@Test
	public void CM5() {
		String rootFolder = "../../Nopol_dataset/cm5/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA ;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL, new String[] {"org.apache.commons.math.util.MathUtilsTest"});
		fixComparison(patch.get(0), "(ns)!=(n)");
		
		/* PATCH: PRECONDITIONAL (ns)==(n) */
	}
	
	@Test
	public void CM6() {
		String rootFolder = "../../Nopol_dataset/cm6/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";

		String dependencyA = "misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.PRECONDITION,
				new String[] {});
		fixComparison(patch.get(0), "(initial)==(2)");
	}
	
	@Test
	public void CM7() {
		String rootFolder = "../../Nopol_dataset/cm7/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";

		String dependencyA = "misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL,
				new String[] {});
		fixComparison(patch.get(0), "(mean)<(1)");
	}

	@Test
	public void CM8() {
		String rootFolder = "../../Nopol_dataset/cm8/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";

		String dependencyA = "misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL,
				new String[] {"org.apache.commons.math3.fraction.FractionTest"});
		fixComparison(patch.get(0), "(ns)!=(n)");

		/* PATCH: PRECONDITIONAL (ns)==(n) */
	}

	@Test
	public void CM9() {
		String rootFolder = "../../Nopol_dataset/cm9/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";

		String dependencyA = "misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL,
				new String[] {});
		fixComparison(patch.get(0), "(ns)!=(n)");

		/* PATCH: PRECONDITIONAL (ns)==(n) */
	}

	@Test
	public void CM10() {
		String rootFolder = "../../Nopol_dataset/cm10/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";

		String dependencyA = "misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL,
				new String[] { "org.apache.commons.math3.stat.correlation.CovarianceTest" });
		fixComparison(patch.get(0), "org.apache.commons.math3.stat.correlation.Covariance.this.covarianceMatrix!=null");

		/* PATCH: PRECONDITIONAL (ns)==(n) */
	}

	@Test
	public void CL1() {
		String rootFolder = "../../Nopol_dataset/cl1/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL,
				new String[] { "org.apache.commons.lang.StringUtilsTest" });
		fixComparison(patch.get(0), "(text.length())==(3)");
	}

	@Test
	public void CL2() {
		String rootFolder = "../../Nopol_dataset/cl2/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL,
				new String[] { "org.apache.commons.lang.StringUtilsTest" });
		fixComparison(patch.get(0), "(lastIdx)<(org.apache.commons.lang.StringUtils.blanks.length())");
		/* PATCH: PRECONDITIONAL (ZB)!=(n) */
	}

	@Test
	public void CL3() {
		String rootFolder = "../../Nopol_dataset/cl3/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = libFolder + "junit-3.8.jar";

		String classpath = binFolder + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher
				.launch(FileLibrary.openFrom(srcFolder),
						JavaLibrary.classpathFrom(classpath),
						StatementType.CONDITIONAL,
						new String[] { "org.apache.commons.lang.StringUtilsSubstringTest" });
		fixComparison(patch.get(0), "(!((0)<=(len)))||((5)<(pos))");
		/* PATCH: PRECONDITIONAL (ZB)!=(n) */
	}

	@Test
	public void CL4() {
		String rootFolder = "/Users/thomas/github/Nopol_dataset/cl4/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "bin/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL,
				new String[] { "org.apache.commons.lang.text.StrBuilderTest" });
		fixComparison(patch.get(0), "((!(str!=null))||(startIndex >= (size)))&&((!(str!=null))||(startIndex >= (size)))");
		/* PATCH: PRECONDITIONAL (ZB)!=(n) */
	}
	
	@Test
	public void CL5() {
		String rootFolder = "../../Nopol_dataset/cl5/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL, new String[] {});
		fixComparison(patch.get(0), "(specific)!=(null)");
		/* PATCH: PRECONDITIONAL specific!=null */
	}
	@Ignore
	@Test
	public void CL6() {
		String rootFolder = "../../Nopol_dataset/cl6/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";
		String dependencyB = libFolder + "easymock-2.5.2.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA + ":" + dependencyB;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.CONDITIONAL, new String[] {});
		fixComparison(patch.get(0), "(specific)!=(null)");
		/* PATCH: PRECONDITIONAL specific!=null */
	}

	@Test
	public void PM1() {
		String rootFolder = "../../Nopol_dataset/pm1/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";
		String dependencyB = libFolder + "commons-discovery-0.4.jar";
		String dependencyC = libFolder + "commons-logging-1.1.1.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA + ":" + dependencyB + ":"
				+ dependencyC;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.PRECONDITION, new String[] {});
		fixComparison(patch.get(0), "(specific)!=(null)");
		/* PATCH: PRECONDITIONAL specific!=null */
	}

	@Test
	public void PM2() {
		String rootFolder = "../../Nopol_dataset/pm2/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher
				.launch(FileLibrary.openFrom(srcFolder),
						JavaLibrary.classpathFrom(classpath),
						StatementType.PRECONDITION,
						new String[] { "org.apache.commons.math.exception.util.MessageFactoryTest" });
		fixComparison(patch.get(0), "specific!=null");
		/* PATCH: PRECONDITIONAL specific!=null */
	}		
	
	@Test
	public void PL1() {
		String rootFolder = "../../Nopol_dataset/pl1/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.PRECONDITION, new String[] {"org.apache.commons.lang.time.StopWatchTest"});
		fixComparison(patch.get(0), "(org.apache.commons.lang.time.StopWatch.STATE_RUNNING)==(org.apache.commons.lang.time.StopWatch.this.runningState)");
		/* PATCH: PRECONDITIONAL specific!=null */
	}
	
	@Test
	public void PL2() {
		String rootFolder = "../../Nopol_dataset/pl2/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.PRECONDITION, new String[] {"org.apache.commons.lang.StringEscapeUtilsTest"});
		fixComparison(patch.get(0), "escapeForwardSlash");
		/* PATCH: PRECONDITIONAL specific!=null */
	}
	
	@Test
	public void PL3() {
		String rootFolder = "../../Nopol_dataset/pl3/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.PRECONDITION, new String[] {"org.apache.commons.lang.WordUtilsTest"});
		fixComparison(patch.get(0), "lower > str.length()");
		/* PATCH: PRECONDITIONAL specific!=null */
	}
	
	
	@Test
	public void PL4() {
		String rootFolder = "../../Nopol_dataset/pl4/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.PRECONDITION, new String[] {"org.apache.commons.lang3.text.translate.NumericEntityUnescaperTest"});
		fixComparison(patch.get(0), "start == seqEnd");
		/* PATCH: PRECONDITIONAL specific!=null */
	}
	
	@Test
	public void AM1() {
		String rootFolder = "../../Nopol_dataset/am1/";
		String srcFolder = rootFolder + "src/";
		String binFolder = rootFolder + "target/";
		String libFolder = rootFolder + "lib/";

		String dependencyA = "/Users/thomas/github/nopol/nopol/misc/nopol-example/junit-4.11.jar";

		String classpath = binFolder + "classes" + ":" + binFolder
				+ "test-classes" + ":" + dependencyA;
		String solver = "z3";
		String solverPath = "/Users/thomas/github/nopol/nopol/lib/z3-4.3.2/z3_for_mac";
		SolverFactory.setSolver(solver, solverPath);
		List<Patch> patch = NoPolLauncher.launch(
				FileLibrary.openFrom(srcFolder),
				JavaLibrary.classpathFrom(classpath),
				StatementType.INTEGER_LITERAL, new String[] {"org.apache.commons.lang3.text.translate.NumericEntityUnescaperTest"});
		fixComparison(patch.get(0), "start == seqEnd");
		/* PATCH: PRECONDITIONAL specific!=null */
	}
	
	private Patch test(int projectNumber, int linePosition, StatementType type, Collection<String> expectedFailedTests) {
		ProjectReference project = projectForExample(projectNumber);
		TestCasesListener listener = new TestCasesListener();
		URLClassLoader classLoader = new URLClassLoader(project.classpath());
		TestSuiteExecution.runCasesIn(project.testClasses(), classLoader, listener);
		Collection<String> failedTests = TestCase.testNames(listener.failedTests());
		assertEquals(expectedFailedTests.size(), failedTests.size());
		assertTrue(expectedFailedTests.containsAll(failedTests));
		List<Patch> patches = patchFor(project, type);
		assertEquals(1, patches.size());
		Patch patch = patches.get(0);
		assertEquals(patch.getType(), type);
		assertEquals(linePosition, patch.getLineNumber());
		System.out.println(String.format("Patch for nopol example %d: %s", projectNumber, patch.asString()));
		return patch;
	}
	
	private void fixComparison(Patch foundPatch, String... expectedFixes) {
		Collection<String> possibleFixes = MetaSet.newHashSet(expectedFixes);
		assertTrue(foundPatch + "is not a valid patch",
				possibleFixes.contains(foundPatch.asString()));
	}
	
	public static ProjectReference projectForExample(int nopolExampleNumber) {
		String sourceFile = absolutePathOf(nopolExampleNumber);
		String classpath = "../test-projects/target/test-classes:../test-projects/target/classes";
		String[] testClasses = new String[] { "nopol_examples.nopol_example_" + nopolExampleNumber + ".NopolExampleTest" };
		return new ProjectReference(sourceFile, classpath, testClasses);
	}
	
	private List<Patch> patchFor(ProjectReference project, StatementType type) {
		clean(project.sourceFile().getParent());
		NoPol nopol = new NoPol(project.sourceFile(), project.classpath(), type);
		List<Patch> patches = nopol.build(project.testClasses());
		clean(project.sourceFile().getParent());
		return patches;
	}
	
	private void clean(String folderPath) {
		String path = folderPath + "/spooned";
		if (FileLibrary.isValidPath(path)) {
			FileLibrary.deleteDirectory(path);
		}
	}
	
	public static String absolutePathOf(int exampleNumber) {
		return "../test-projects/src/main/java/nopol_examples/nopol_example_" + exampleNumber + "/NopolExample.java";
	}
}
