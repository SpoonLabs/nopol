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
import static fr.inria.lille.jsemfix.Patch.NO_PATCH;

import java.util.HashSet;
import java.util.Set;

import sacha.finder.main.TestInClasspath;
import fr.inria.lille.jsemfix.sps.Statement;
import fr.inria.lille.jsemfix.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.jsemfix.test.Test;
import fr.inria.lille.jsemfix.test.TestRunner;
import fr.inria.lille.jsemfix.test.junit.JUnitTestRunner;

/**
 * @author Favio D. DeMarco
 *
 */
public final class Fixer {

	private final Package mainPackage;

	/**
	 * A test suite.
	 */
	private TestRunner testSuite;

	/**
	 * @param mainPackage
	 */
	public Fixer(final Package mainPackage) {
		super();
		this.mainPackage = checkNotNull(mainPackage);
	}

	private void applyRepair(final Patch newRepair) {
		// TODO Auto-generated method stub
		//
		throw new UnsupportedOperationException("Fixer.applyRepair");
	}

	public Patch createPatch() {

		// A test suite.
		Class<?>[] testClasses = this.findTestClasses();
		this.testSuite = new JUnitTestRunner(testClasses);

		// A ranked list of potential bug root-cause.
		Iterable<Statement> statements = GZoltarSuspiciousProgramStatements.createWithPackageAndTestClasses(
				this.mainPackage, testClasses).sortBySuspiciousness();

		return this.createPatch(statements);
	}

	private Patch createPatch(final Iterable<Statement> statements) {
		for (Statement rc : statements) {

			Patch newRepair = this.createPatchForStatement(rc);

			if (NO_PATCH != newRepair) {
				return newRepair;
			}
		}
		return NO_PATCH;
	}

	private Patch createPatchForStatement(final Statement rc) {
		// A test suite for repair generation
		Set<Test> s = new HashSet<>();
		Set<Test> tf = this.extractFailedTests();
		Repair repair = this.createRepair(rc);
		Patch newRepair = NO_PATCH;
		while (!tf.isEmpty()) {
			s.addAll(tf);
			newRepair = repair.createPatch(s);
			if (NO_PATCH == newRepair) {
				break;
			}
			this.applyRepair(newRepair);
			tf = this.extractFailedTests();
		}
		return newRepair;
	}

	private Repair createRepair(final Statement rc) {
		return new SimpleRepair(rc);
	}

	private Set<Test> extractFailedTests() {
		return this.testSuite.run();
	}

	private Class<?>[] findTestClasses() {
		return new TestInClasspath().find();
	}
}
