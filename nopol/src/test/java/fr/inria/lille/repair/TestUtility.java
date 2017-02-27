package fr.inria.lille.repair;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.commons.synthesis.smt.solver.Z3SolverFactory;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.NoPol;
import fr.inria.lille.repair.nopol.NopolResult;
import xxl.java.container.classic.MetaSet;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import java.io.File;
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

	public static NopolContext configForExample(String executionType, int nopolExampleNumber) {
		String sourceFile = "../test-projects/src/";
		String classpath = "../test-projects/target/test-classes" + File.pathSeparatorChar + "../test-projects/target/classes" + File.pathSeparatorChar + "lib/junit-4.11.jar";
		String[] testClasses = new String[]{executionType + "_examples." + executionType + "_example_"
				+ nopolExampleNumber + ".NopolExampleTest"};
		return new NopolContext(sourceFile, JavaLibrary.classpathFrom(classpath), testClasses);
	}

	public static List<Patch> patchFor(String executionType, NopolContext nopolContext) {
		nopolContext.setLocalizer(NopolContext.NopolLocalizer.GZOLTAR);

		List<Patch> patches;
		switch (executionType) {
			case "symbolic":
				nopolContext.setOracle(NopolContext.NopolOracle.SYMBOLIC);
				break;
			case "nopol":
				nopolContext.setOracle(NopolContext.NopolOracle.ANGELIC);
				break;
			default:
				throw new RuntimeException("Execution type not found");
		}

		NoPol nopol = new NoPol(nopolContext);
		NopolResult status = nopol.build();

		for (int i = 0; i < nopolContext.getProjectSources().length; i++) {
			File file = nopolContext.getProjectSources()[i];
			clean(file.getParent());
		}
		return status.getPatches();
	}

	public static List<Patch> setupAndRun(String executionType, int projectNumber, TestCasesListener listener, StatementType type) {
		NopolContext nopolContext = configForExample(executionType, projectNumber);
		nopolContext.setType(type);
		SolverFactory.setSolver(SOLVER, solverPath);
		URLClassLoader classLoader = new URLClassLoader(nopolContext.getProjectClasspath());
		TestSuiteExecution.runCasesIn(nopolContext.getProjectTests(), classLoader, listener, nopolContext);
		return patchFor(executionType, nopolContext);
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
