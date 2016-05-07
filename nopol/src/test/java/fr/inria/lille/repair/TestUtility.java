package fr.inria.lille.repair;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
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
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Thomas Durieux on 03/03/15.
 */
public abstract class TestUtility {
    String solver = "z3";
    String solverPath =  "lib/z3/z3_for_linux";
    String executionType;

    public TestUtility(String executionType) {
        this.executionType = executionType;
    }

    public ProjectReference projectForExample(int nopolExampleNumber) {
        String sourceFile = "../test-projects/src/";
        String classpath = "../test-projects/target/test-classes"+File.pathSeparatorChar+"../test-projects/target/classes"+File.pathSeparatorChar+"misc/nopol-example/junit-4.11.jar";
        String[] testClasses = new String[] { executionType + "_examples." + executionType + "_example_"
                + nopolExampleNumber + ".NopolExampleTest" };
        return new ProjectReference(sourceFile, classpath, testClasses);
    }

    private List<Patch> patchFor(ProjectReference project, StatementType type) {
        for (int i = 0; i < project.sourceFiles().length; i++) {
            File file = project.sourceFiles()[i];
            clean(file.getParent());
        }
        List<Patch> patches;
        switch (this.executionType) {
            case "symbolic":
                Config.INSTANCE.setOracle(Config.NopolOracle.SYMBOLIC);
                break;
            case "nopol":
                Config.INSTANCE.setOracle(Config.NopolOracle.ANGELIC);
                break;
            default:
                throw new RuntimeException("Execution type not found");
        }
        NoPol nopol = new NoPol(project.sourceFiles(), project.classpath(), type);
        patches = nopol.build(project.testClasses());

        for (int i = 0; i < project.sourceFiles().length; i++) {
            File file = project.sourceFiles()[i];
            clean(file.getParent());
        }
        return patches;
    }

    protected void fixComparison(Patch foundPatch, String... expectedFixes) {
        Collection<String> possibleFixes = MetaSet.newHashSet(expectedFixes);
        assertTrue(foundPatch + " is not a valid patch",
                possibleFixes.contains(foundPatch.asString()));
    }

    protected Patch test(int projectNumber, int linePosition, StatementType type,
                       Collection<String> expectedFailedTests) {
        ProjectReference project = projectForExample(projectNumber);
        SolverFactory.setSolver(solver, solverPath);
        TestCasesListener listener = new TestCasesListener();
        URLClassLoader classLoader = new URLClassLoader(project.classpath());
        TestSuiteExecution.runCasesIn(project.testClasses(), classLoader,
                listener);
        Collection<String> failedTests = TestCase.testNames(listener
                .failedTests());
        // assertEquals(expectedFailedTests.size(), failedTests.size());
        // assertTrue(expectedFailedTests.containsAll(failedTests));
        List<Patch> patches = patchFor(project, type);
        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), type);
        assertEquals(linePosition, patch.getLineNumber());
        System.out.println(String.format("Patch for nopol example %d: %s",
                projectNumber, patch.asString()));
        return patch;
    }

    void clean(String folderPath) {
        String path = folderPath + "/spooned";
        if (FileLibrary.isValidPath(path)) {
            FileLibrary.deleteDirectory(path);
        }
    }
}
