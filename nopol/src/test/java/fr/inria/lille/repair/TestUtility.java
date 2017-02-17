package fr.inria.lille.repair;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.commons.synthesis.smt.solver.Z3SolverFactory;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.NoPol;
import xxl.java.container.classic.MetaSet;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;
import xxl.java.library.FileLibrary;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Thomas Durieux on 03/03/15.
 */
public class TestUtility {

	private static final String SOLVER = "z3";
	private static final String SOLVER_PATH_DIR = "lib/z3/";
	private static final String SOLVER_NAME_LINUX = "z3_for_linux";
	private static final String SOLVER_NAME_MAC = "z3_for_mac";
	public static String solverPath;

	static {
		if (Z3SolverFactory.isMac()) {
			solverPath = SOLVER_PATH_DIR+SOLVER_NAME_MAC;
		} else {
			solverPath = SOLVER_PATH_DIR+SOLVER_NAME_LINUX;
		}
	}

	public static ProjectReference projectForExample(String executionType, int nopolExampleNumber) {
		String sourceFile = "../test-projects/src/";
		String classpath = "../test-projects/target/test-classes" + File.pathSeparatorChar + "../test-projects/target/classes" + File.pathSeparatorChar + "lib/junit-4.11.jar";
		String[] testClasses = new String[]{executionType + "_examples." + executionType + "_example_"
				+ nopolExampleNumber + ".NopolExampleTest"};
		return new ProjectReference(sourceFile, classpath, testClasses);
	}

	public static List<Patch> patchFor(String executionType, ProjectReference project, Config config) {
		config.setLocalizer(Config.NopolLocalizer.GZOLTAR);
		String[] sourceFiles = new String[project.sourceFiles().length];
		for (int i = 0; i < project.sourceFiles().length; i++) {
			File file = project.sourceFiles()[i];
			clean(file.getParent());
			try {
				sourceFiles[i] = file.getCanonicalPath();
			} catch (IOException ignore) {
			}
		}
		config.setProjectSourcePath(sourceFiles);
		List<Patch> patches;
		switch (executionType) {
			case "symbolic":
				config.setOracle(Config.NopolOracle.SYMBOLIC);
				break;
			case "nopol":
				config.setOracle(Config.NopolOracle.ANGELIC);
				break;
			default:
				throw new RuntimeException("Execution type not found");
		}

		NoPol nopol = new NoPol(project, config);
		patches = nopol.build(project.testClasses());

		for (int i = 0; i < project.sourceFiles().length; i++) {
			File file = project.sourceFiles()[i];
			clean(file.getParent());
		}
		return patches;
	}

	public static List<Patch> setupAndRun(String executionType, int projectNumber, Config config, TestCasesListener listener) {
		ProjectReference project = projectForExample(executionType, projectNumber);
		SolverFactory.setSolver(SOLVER, solverPath);
		URLClassLoader classLoader = new URLClassLoader(project.classpath());
		TestSuiteExecution.runCasesIn(project.testClasses(), classLoader, listener, config);
		return patchFor(executionType, project, config);
	}

	public static void assertPatches(int linePosition, Collection<String> expectedFailedTests, StatementType expectedType, TestCasesListener listener, List<Patch> patches) {
		Collection<String> failedTests = TestCase.testNames(listener
				.failedTests());
		Patch patch = patches.get(0);

		assertEquals(expectedFailedTests.size(), failedTests.size());
		assertTrue(expectedFailedTests.containsAll(failedTests));
		assertEquals(patches.toString(), 1, patches.size());

		assertEquals(expectedType, patch.getType());
		assertEquals(linePosition, patch.getLineNumber());
	}

	public static void assertAgainstKnownPatches(Patch foundPatch, String... expectedFixes) {
		Collection<String> possibleFixes = MetaSet.newHashSet(expectedFixes);
		assertTrue(foundPatch + " is not a valid patch",
				possibleFixes.contains(foundPatch.asString()));
	}

	private static void clean(String folderPath) {
		String path = folderPath + "/spooned";
		if (FileLibrary.isValidPath(path)) {
			FileLibrary.deleteDirectory(path);
		}
	}
}
