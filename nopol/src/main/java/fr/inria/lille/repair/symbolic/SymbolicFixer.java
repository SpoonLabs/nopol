package fr.inria.lille.repair.symbolic;

import static fr.inria.lille.repair.common.patch.Patch.NO_PATCH;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import com.gzoltar.core.instr.testing.TestResult;

import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.TestClassesFinder;
import fr.inria.lille.repair.nopol.sps.SuspiciousStatement;
import fr.inria.lille.repair.nopol.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.symbolic.spoon.AssertReplacer;
import fr.inria.lille.repair.symbolic.spoon.SymbolicProcessor;
import fr.inria.lille.repair.symbolic.spoon.TestExecutorProcessor;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.symbolic.synth.Synthesizer;
import fr.inria.lille.repair.symbolic.synth.SynthesizerFactory;

public class SymbolicFixer {

	private String mainClass;
	private final SpoonedProject spoonProject;
	private final StatementType type;

	public SymbolicFixer(final File sourceFile, final URL[] classpath,
			StatementType type) {
		this(new ProjectReference(sourceFile, classpath), type);
	}

	public SymbolicFixer(ProjectReference project, StatementType type) {
		this.classpath = project.classpath();
		this.sourceFile = project.sourceFile();
		this.testClasses = project.testClasses();
		this.type = type;
		
		// get all test classes of the current project
		if (this.testClasses == null || this.testClasses.length == 0) {
			this.testClasses = new TestClassesFinder().findIn(classpath, false);
		}
		addJPFLibraryToCassPath();
		spoonProject = new SpoonedProject(this.sourceFile, classpath);		
		jpfSpoonedProject = new SpoonedProject(this.sourceFile, classpath);
		// init gzolor
		gZoltar = GZoltarSuspiciousProgramStatements.create(
				project.classpath(), spoonProject.topPackageNames());

	}

	public List<Patch> repair() {
		// get suspicious statement of the current project
		Collection<SuspiciousStatement> statements = gZoltar
				.sortBySuspiciousness(testClasses);
		if (statements.size() == 0) {
			return new ArrayList<Patch>();
		}
		Set<String> faillingClassTest = new HashSet<>();
		List<TestResult> testResults = gZoltar.getGzoltar().getTestResults();
		for (TestResult testResult : testResults) {
			if(!testResult.wasSuccessful()) {
				faillingClassTest.add(testResult.getName().split("#")[0]);
			}
		}
		// get all failling tests
		//Collection<TestCase> faillingTest = failingTests(testClasses,this.spoonProject.dumpedToClassLoader());
		Collection<TestCase> faillingTest = failingTests(faillingClassTest.toArray(new String[0]), new URLClassLoader(classpath));
		

		// create the a main method for unit test (require by JPF)
		String pack = getSharedPackage();
		this.mainClass = pack + ".JPFExecuteTestClass";
		TestExecutorProcessor.createMainTestClass(jpfSpoonedProject, mainClass);

		// transform junit.assert* to condition
		jpfSpoonedProject.process(new AssertReplacer(faillingTest));

		try {
			// generate the output file
			jpfSpoonedProject.generateOutputFile(outputSourceFile);
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
			if (spoonFork == null) {
				continue;
			}
			Synthesizer synth = new SynthesizerFactory(spoonFork).getFor(
					statement.getSourceLocation(), type);

			if (synth == Synthesizer.NO_OP_SYNTHESIZER) {
				continue;
			}
			// generate the patch
			Patch patch = synth.buildPatch(classpath, testClasses,
					faillingTests, spoonProject, mainClass);

			// verify the patch
			if (isOk(patch, testClasses, synth.getSymbolicProcessor())) {
				patches.add(patch);
				break;
			} else {
				logger.debug("Could not find a patch in {}", statement);
			}
		}
		return patches;
	}

	/**
	 * Remove temporary file
	 */
	protected void cleanUp() {
		logger.info("remove temp file");
		try {
			FileUtils.deleteDirectory(outputCompiledFile);
			FileUtils.deleteDirectory(outputSourceFile);
		} catch (IOException e) {
			throw new RuntimeException("Unable to remove temp folders.");
		}
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
		processor.setValue(newRepair.asString());

		String qualifiedName = newRepair.getRootClassName();
		ClassLoader loader = this.spoonProject.forked(qualifiedName)
				.processedAndDumpedToClassLoader(processor);
		logger.info("Running test suite to check the patch \"{}\" is working",
				newRepair.asString());
		Result result = TestSuiteExecution.runCasesIn(testClasses, loader);
		if (result.wasSuccessful()) {
			return true;
		} else {
			System.out.println(result.getFailures());
		}
		return false;
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
		URL[] classpath = new URL[this.classpath.length + 4];
		try {
			File file = new File("lib/jpf/jpf.jar");
			classpath[classpath.length - 3] = file.toURL();
			// file = new File("lib/jpf/gov.nasa-0.0.1.jar");
			// classpath[classpath.length - 3] = file.toURL();
			file = new File("lib/jpf/jpf-annotations.jar");
			classpath[classpath.length - 2] = file.toURL();
			file = new File("lib/jpf/jpf.jar");
			classpath[classpath.length - 1] = file.toURL();
			file = new File("misc/nopol-example/junit-4.11.jar");
			classpath[classpath.length - 4] = file.toURL();
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
	 *            second string
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
