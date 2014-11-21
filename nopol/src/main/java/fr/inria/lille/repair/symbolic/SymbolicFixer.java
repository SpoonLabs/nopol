package fr.inria.lille.repair.symbolic;

import static fr.inria.lille.repair.symbolic.patch.Patch.NO_PATCH;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;
import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.TestClassesFinder;
import fr.inria.lille.repair.nopol.sps.SuspiciousStatement;
import fr.inria.lille.repair.nopol.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.repair.symbolic.patch.Patch;
import fr.inria.lille.repair.symbolic.spoon.AssertReplacer;
import fr.inria.lille.repair.symbolic.spoon.TestExecutorProcessor;
import fr.inria.lille.repair.symbolic.spoon.SymbolicProcessor;
import fr.inria.lille.repair.symbolic.synth.Synthesizer;
import fr.inria.lille.repair.symbolic.synth.SynthesizerFactory;

public class SymbolicFixer {

	public static void runTest(String[] methods) throws java.lang.Exception {
		for (String method : methods) {
			String[] split = method.split(".");
			SymbolicFixer.class.getMethod("run", String[].class).invoke(
					Class.forName(method.replace("." + split[split.length - 1],
							"")), new String[] { split[split.length - 1] });
		}
	}

	private String mainClass;
	private SpoonedProject spoonProject;

	public SymbolicFixer(final File sourceFile, final URL[] classpath) {
		this(new ProjectReference(sourceFile, classpath));
	}

	public SymbolicFixer(ProjectReference project) {
		this.classpath = project.classpath();
		this.sourceFile = project.sourceFile();
		this.testClasses = project.testClasses();

		addJPFLibraryToCassPath();

		// get all test classes of the current project
		if (this.testClasses == null) {
			this.testClasses = new TestClassesFinder().findIn(classpath, false);
		}
		spoonProject = new SpoonedProject(this.sourceFile, classpath);
		jpfSpoonedProject = new SpoonedProject(this.sourceFile, classpath);

		// init gzolor
		gZoltar = GZoltarSuspiciousProgramStatements.create(this.classpath,
				jpfSpoonedProject.topPackageNames());
	}

	public List<Patch> repair() {
		// get suspicious statement of the current project
		Collection<SuspiciousStatement> statements = gZoltar
				.sortBySuspiciousness(testClasses);
		// get all failling tests
		Collection<TestCase> faillingTest = failingTests(testClasses,
				this.jpfSpoonedProject.dumpedToClassLoader());

		// create the a main method for unit test (require by JPF)
		String pack = getSharedPackage();
		this.mainClass = pack + ".JPFExecuteTestClass";
		TestExecutorProcessor.createMainTestClass(jpfSpoonedProject, mainClass);

		// transform junit.assert* to condition
		jpfSpoonedProject.process(new AssertReplacer());

		// generate the output file
		jpfSpoonedProject.generateOutputFile(outputSourceFile);
		try {
			jpfSpoonedProject.generateOutputCompiledFile(outputCompiledFile);
		} catch (IOException e) {
			throw new RuntimeException("Unable to write transformed test", e);
		}

		List<Patch> patchs = analyzeStatements(statements, testClasses,
				faillingTest);
		cleanUp();
		return patchs;
	}

	/**
	 * Analyzes suspicious statements
	 * 
	 * @param statements
	 *            suspicious statements
	 * @param testClasses
	 *            all test classes of the current project
	 * @param faillingTest
	 *            all failing tests
	 * 
	 */
	private List<Patch> analyzeStatements(
			Collection<SuspiciousStatement> statements, String[] testClasses,
			Collection<TestCase> faillingTests) {
		List<Patch> patches = new ArrayList<Patch>();

		// analyze all suspicious statements
		for (SuspiciousStatement statement : statements) {
			// don't analyze statement in test
			if (isInTest(statement))
				continue;

			SpoonedClass spoonFork = spoonProject.forked(statement
					.getSourceLocation().getContainingClassName());

			Synthesizer synth = new SynthesizerFactory(spoonFork)
					.getFor(statement.getSourceLocation());

			if (synth == Synthesizer.NO_OP_SYNTHESIZER) {
				continue;
			}
			// generate the patch
			Patch patch = synth.buildPatch(classpath, testClasses,
					faillingTests, spoonProject, mainClass);

			// verify the patch
			if (isOk(patch, testClasses, synth.getSymbolicProcessor())) {
				patches.add(patch);
			} else {
				logger.debug("Could not find a patch in {}", statement);
			}
		}
		return patches;
	}

	/**
	 * Remove temporary file
	 */
	public void cleanUp() {
		logger.info("remove temp file");
		outputCompiledFile.delete();
		outputSourceFile.delete();
	}

	private boolean isInTest(SuspiciousStatement statement) {
		if (statement.getSourceLocation().getContainingClassName()
				.contains("Test")) {
			return true;
		}
		return false;
	}

	private boolean isOk(Patch newRepair, String[] testClasses,
			SymbolicProcessor processor) {
		if (newRepair == NO_PATCH) {
			return false;
		}
		logger.trace("Suggested patch: {}", newRepair);
		return true;
		// return testPatch.passesAllTests(newRepair, testClasses, processor);
	}

	/**
	 * returns the list of failing tests
	 * 
	 * @param testClasses
	 * @return the list of failing tests
	 */
	private Collection<TestCase> failingTests(String[] testClasses,
			ClassLoader testClassLoader) {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(testClasses, testClassLoader, listener);
		return listener.failedTests();
	}

	/**
	 * Add JPF library to class path
	 */
	private void addJPFLibraryToCassPath() {
		URL[] classpath = new URL[this.classpath.length + 5];
		try {
			File file = new File("lib/jpf/jpf.jar");
			classpath[classpath.length - 4] = file.toURL();
			file = new File("lib/jpf/gov.nasa-0.0.1.jar");
			classpath[classpath.length - 3] = file.toURL();
			file = new File("lib/jpf/jpf-annotations.jar");
			classpath[classpath.length - 2] = file.toURL();
			file = new File("lib/jpf/jpf.jar");
			classpath[classpath.length - 1] = file.toURL();
			file = new File("misc/nopol-example/junit-4.11.jar");
			classpath[classpath.length - 5] = file.toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("JPF dependencies not found");
		}
		for (int i = 0; i < this.classpath.length; i++) {
			classpath[i] = this.classpath[i];
		}

		this.classpath = classpath;
	}

	/**
	 * get the intersection between two string
	 * 
	 * @param s1
	 *            first string
	 * @param s2
	 *            seconf string
	 * @return the intersection
	 */
	private String intersection(String s1, String s2) {
		String result = "";
		for (int i = 0; i < s2.length() && i < s1.length(); i++) {
			if (s1.charAt(i) == s2.charAt(i)) {
				result += s1.charAt(i);
			} else {
				break;
			}
		}
		return result;
	}

	/**
	 * get the shared package between all package
	 * 
	 * @return
	 */
	private String getSharedPackage() {
		String[] packages = jpfSpoonedProject.topPackageNames().toArray(
				new String[0]);
		String pack = packages[0];
		for (String ctPackage : packages) {
			pack = intersection(pack, ctPackage);
		}
		if (pack.equals("")) {
			pack += "main";
		}
		return pack;
	}

	private final File outputSourceFile = new File("src-gen");
	private final File outputCompiledFile = new File("target-gen");

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonedProject jpfSpoonedProject;
	private URL[] classpath;
	private final File sourceFile;
	private final GZoltarSuspiciousProgramStatements gZoltar;
	private String[] testClasses;
}
