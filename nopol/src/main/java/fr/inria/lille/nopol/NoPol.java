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
package fr.inria.lille.nopol;

import static fr.inria.lille.nopol.patch.Patch.NO_PATCH;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.lille.commons.classes.TestClassesFinder;
import fr.inria.lille.nopol.patch.Patch;
import fr.inria.lille.nopol.patch.TestPatch;
import fr.inria.lille.nopol.sps.SuspiciousStatement;
import fr.inria.lille.nopol.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.nopol.synth.SynthesizerFactory;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class NoPol {

	private final URL[] classpath;
	private final GZoltarSuspiciousProgramStatements gZoltar;
	private final SynthesizerFactory synthetizerFactory;
	private final TestPatch testPatch;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Logger patchLogger = LoggerFactory.getLogger("patch");

	/**
	 * @param rootPackage
	 * @param sourceFolder
	 * @param classpath
	 */
	public NoPol(final File sourceFolder, final URL[] classpath) {
		this.classpath = classpath;
		gZoltar = GZoltarSuspiciousProgramStatements.create(this.classpath);
		synthetizerFactory = new SynthesizerFactory(sourceFolder);
		testPatch = new TestPatch(sourceFolder, classpath);
	}

	public Patch build() {
		String[] testClasses = new TestClassesFinder().findIn(classpath, false);			
		
		if (testClasses.length == 0) {
			System.out.printf("No test classes found in classpath: %s%n", Arrays.toString(classpath));
			return NO_PATCH;
		}

		Collection<SuspiciousStatement> statements = gZoltar.sortBySuspiciousness(testClasses);

		if (statements.isEmpty()) {
			System.out.println("No suspicious statements found.");
		}

		for (SuspiciousStatement statement : statements) {
			logger.debug("Analysing {}", statement);
			Patch newRepair = buildPatch(testClasses, statement);
			if (isOk(newRepair, testClasses)) {
				return newRepair;
			}
		}
		return NO_PATCH;
	}

	/**
	 * @param testClasses
	 * @param statement
	 * @return
	 */
	private Patch buildPatch(final String[] testClasses, final SuspiciousStatement statement) {
		try {
			return synthetizerFactory.getFor(statement.getSourceLocation()).buildPatch(classpath,
					testClasses);
		} catch (IllegalArgumentException e) {
			logger.info(e.getMessage());
			return NO_PATCH;
		}
	}

	private boolean isOk(final Patch newRepair, final String[] testClasses) {
		if (newRepair == NO_PATCH) {
			return false;
		}
		patchLogger.trace("Suggested patch: {}", newRepair);
		return passesAllTests(newRepair, testClasses);
	}

	private boolean passesAllTests(final Patch newRepair, final String[] testClasses) {
		return testPatch.passesAllTests(newRepair, testClasses);
	}
}
