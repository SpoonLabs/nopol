/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.repair.nopol;

import static fr.inria.lille.repair.common.patch.Patch.NO_PATCH;

import java.io.File;
import java.net.URL;
import java.util.*;

import com.gzoltar.core.components.Component;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.TestClassesFinder;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.patch.TestPatch;
import fr.inria.lille.repair.nopol.spoon.ConditionalProcessor;
import fr.inria.lille.repair.nopol.sps.SuspiciousStatement;
import fr.inria.lille.repair.nopol.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.repair.nopol.synth.Synthesizer;
import fr.inria.lille.repair.nopol.synth.SynthesizerFactory;
import fr.inria.lille.repair.common.synth.StatementType;

/**
 * @author Favio D. DeMarco
 * 
 */
public class NoPol {

	private final URL[] classpath;
	private final GZoltarSuspiciousProgramStatements gZoltar;
	private final TestPatch testPatch;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonedProject spooner;
	private final File sourceFile;
	private final StatementType type;
	private static boolean singlePatch = true;

	public NoPol(final File sourceFile, final URL[] classpath, StatementType type) {
		this.classpath = classpath;
		this.sourceFile = sourceFile;
		this.type = type;
		spooner = new SpoonedProject(sourceFile, classpath);
		testPatch = new TestPatch(sourceFile, spooner);
		gZoltar = GZoltarSuspiciousProgramStatements.create(this.classpath, spooner.topPackageNames());
	}

	public List<Patch> build() {
		String[] testClasses = new TestClassesFinder().findIn(classpath, false);
		return build(testClasses);
	}
	
	public List<Patch> build(String[] testClasses) {
		Collection<SuspiciousStatement> statements = gZoltar.sortBySuspiciousness(testClasses);
		Map<SourceLocation, List<TestResult>> testListPerStatement = getTestListPerStatement();
		if (statements.isEmpty()) {
			System.out.println("No suspicious statements found.");
		}
		List<TestResult> faillingClassTest = new ArrayList<>();
		for (TestResult testResult : gZoltar.getGzoltar().getTestResults()) {
			if(!testResult.wasSuccessful()) {
				faillingClassTest.add(testResult);
			}
		}
		return solveWithMultipleBuild(statements,testListPerStatement,testClasses, faillingClassTest);
	}

	private Map<SourceLocation, List<TestResult>> getTestListPerStatement() {
		Map<SourceLocation, List<TestResult>> results = new HashMap<>();
		List<TestResult> testResults = gZoltar.getGzoltar().getTestResults();
		for (int i = 0; i < testResults.size(); i++) {
			TestResult testResult = testResults.get(i);
			List<Component> components = testResult.getCoveredComponents();
			for (int j = 0; j < components.size(); j++) {
				Statement component = (Statement) components.get(j);
				SourceLocation sourceLocation = new SourceLocation(component.getMethod().getParent().getLabel(), component.getLineNumber());
				if(!results.containsKey(sourceLocation)) {
					results.put(sourceLocation, new ArrayList<TestResult>());
				}
				results.get(sourceLocation).add(testResult);
			}
		}
		return results;
	}
	
	private Collection<TestCase> failingTests(String[] testClasses) {
		TestCasesListener listener = new TestCasesListener();
		TestSuiteExecution.runCasesIn(testClasses, spooner.dumpedToClassLoader(), listener);
		return listener.failedTests();
	}
	
	/*
	 * First algorithm of Nopol,
	 * build the initial model
	 * apply only one modification
	 * build
	 * try to find patch
	 */
	private List<Patch> solveWithMultipleBuild(Collection<SuspiciousStatement> statements, Map<SourceLocation, List<TestResult>> testListPerStatement, String[] testClasses, List<TestResult> failures){
		List<Patch> patches = new ArrayList<Patch>();
		for (SuspiciousStatement statement : statements) {
			try {
				if ( !statement.getSourceLocation().getContainingClassName().contains("Test")){ // Avoid modification on test cases
					logger.debug("Analysing {}", statement);
					SourceLocation sourceLocation = new SourceLocation(statement.getSourceLocation().getContainingClassName(), statement.getSourceLocation().getLineNumber());
					Synthesizer synth = new SynthesizerFactory(sourceFile, spooner, type).getFor(sourceLocation);
					Patch patch = synth.buildPatch(classpath, testListPerStatement.get(sourceLocation), failures);
					if (isOk(patch, testListPerStatement.get(sourceLocation), synth.getConditionalProcessor())) {
						patches.add(patch);
						if ( isSinglePatch() ){
							break;
						}
					} else {
						logger.debug("Could not find a patch in {}", statement);
					}
				}
			}
			catch (RuntimeException re) {
				re.printStackTrace();
			}
		}
		return patches;
	}

	private boolean isOk(Patch newRepair, List<TestResult> testClasses, ConditionalProcessor processor) {
		if (newRepair == NO_PATCH) {
			return false;
		}
		logger.trace("Suggested patch: {}", newRepair);
		return testPatch.passesAllTests(newRepair, testClasses, processor);
	}

	public static boolean isSinglePatch() {
		return singlePatch;
	}
	
	public static boolean setSinglePatch(boolean singlePatch) {
		return NoPol.singlePatch = singlePatch;
	}
}
