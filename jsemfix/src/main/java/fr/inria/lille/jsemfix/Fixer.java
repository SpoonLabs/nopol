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
package fr.inria.lille.jsemfix;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.inria.lille.jsemfix.patch.Patch.NO_PATCH;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacha.finder.main.TestInClasspath;
import fr.inria.lille.jsemfix.constraint.BooleanRepairConstraintBuilder;
import fr.inria.lille.jsemfix.patch.Patch;
import fr.inria.lille.jsemfix.patch.Patcher;
import fr.inria.lille.jsemfix.patch.SimplePatcher;
import fr.inria.lille.jsemfix.sps.SuspiciousStatement;
import fr.inria.lille.jsemfix.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.jsemfix.test.Test;
import fr.inria.lille.jsemfix.test.TestRunner;
import fr.inria.lille.jsemfix.test.junit.JUnitTestRunner;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class Fixer {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final boolean loggerEnabled = this.logger.isDebugEnabled();

	private final Package mainPackage;

	/**
	 * A test suite.
	 */
	private TestRunner testSuite;

	/**
	 * @param mainPackage
	 */
	public Fixer(final Package mainPackage) {
		this.mainPackage = checkNotNull(mainPackage);
	}

	public Patch createPatch() {

		// A test suite.
		Class<?>[] testClasses = this.findTestClasses();
		this.testSuite = new JUnitTestRunner(testClasses);

		// A ranked list of potential bug root-cause.
		Iterable<SuspiciousStatement> statements = GZoltarSuspiciousProgramStatements.createWithPackageAndTestClasses(
				this.mainPackage, testClasses).sortBySuspiciousness();

		return this.createPatch(statements);
	}

	private Patch createPatch(final Iterable<SuspiciousStatement> statements) {
		Program program = Program.DEFAULT;
		for (SuspiciousStatement rc : statements) {

			Patch newRepair = this.createPatchForStatement(program, rc);

			if (NO_PATCH != newRepair) {
				this.log("Patch found: {}", newRepair);
				return newRepair;
			}
		}
		return NO_PATCH;
	}

	private Patcher createPatcher(final SuspiciousStatement rc) {
		return new SimplePatcher(rc, new BooleanRepairConstraintBuilder());
	}

	private Patch createPatchForStatement(final Program program, final SuspiciousStatement rc) {
		// A test suite for repair generation
		Set<Test> s = new HashSet<>();
		Set<Test> tf = this.extractFailedTests(program);
		Patcher patcher = this.createPatcher(rc);
		Patch newRepair = NO_PATCH;
		while (!tf.isEmpty()) {
			s.addAll(tf);
			this.log("Tests: {}", s);
			newRepair = patcher.createPatch(s);
			if (NO_PATCH == newRepair) {
				break;
			} else {
				this.log("Candidate patch: {}", newRepair);
			}
			Program patchedProgram = newRepair.apply(program);
			tf = this.extractFailedTests(patchedProgram);
		}
		return newRepair;
	}

	private void log(final String msg, final Object... parameters) {
		if (this.loggerEnabled) {
			this.logger.debug(msg, parameters);
		}
	}

	private Set<Test> extractFailedTests(final Program program) {
		return this.testSuite.run(program);
	}

	private Class<?>[] findTestClasses() {
		return new TestInClasspath().find();
	}
}
