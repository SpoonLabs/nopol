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
package fr.inria.lille.jefix;

import static fr.inria.lille.jefix.patch.Patch.NO_PATCH;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.lille.jefix.patch.Patch;
import fr.inria.lille.jefix.sps.SuspiciousStatement;
import fr.inria.lille.jefix.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.jefix.synth.SynthetizerFactory;
import fr.inria.lille.jefix.test.junit.TestClassesFinder;
import fr.inria.lille.jefix.test.junit.TestPatch;

/**
 * @author Favio D. DeMarco
 * 
 */
final class JEFix {

	private final URL[] classpath;
	private final GZoltarSuspiciousProgramStatements gZoltar;
	private final SynthetizerFactory synthetizerFactory;
	private final TestPatch testPatch;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param rootPackage
	 * @param sourceFolder
	 * @param classpath
	 */
	public JEFix(final File sourceFolder, final URL[] classpath) {
		this.classpath = classpath;
		this.gZoltar = GZoltarSuspiciousProgramStatements.create(this.classpath);
		this.synthetizerFactory = new SynthetizerFactory(sourceFolder);
		this.testPatch = new TestPatch(sourceFolder, classpath);
	}

	public Patch build() {
		String[] testClasses = new TestClassesFinder().findIn(this.classpath);
		Iterable<SuspiciousStatement> statements = this.gZoltar.sortBySuspiciousness(testClasses);
		for (SuspiciousStatement statement : statements) {
			this.logger.debug("Analysing {}", statement);
			Patch newRepair = this.synthetizerFactory.getFor(statement.getSourceLocation()).buildPatch(this.classpath,
					testClasses);
			if (this.isOk(newRepair, testClasses)) {
				return newRepair;
			}
		}
		return NO_PATCH;
	}

	private boolean isOk(final Patch newRepair, final String[] testClasses) {
		if (newRepair == NO_PATCH) {
			return false;
		}
		this.logger.debug("Suggested patch: {}", newRepair);
		return this.passesAllTests(newRepair, testClasses);
	}

	private boolean passesAllTests(final Patch newRepair, final String[] testClasses) {
		return this.testPatch.passesAllTests(newRepair, testClasses);
	}
}
