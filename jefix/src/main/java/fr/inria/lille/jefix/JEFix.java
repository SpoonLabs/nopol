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

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.lille.jefix.junit.TestClassesFinder;
import fr.inria.lille.jefix.sps.SuspiciousStatement;
import fr.inria.lille.jefix.sps.gzoltar.GZoltarSuspiciousProgramStatements;

/**
 * @author Favio D. DeMarco
 *
 */
final class JEFix {

	private static final long TIME_OUT_SECONDS = 1800L;

	private final String rootPackage;

	private final File sourceFolder;
	private final URL[] classpath;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final boolean debug = this.logger.isDebugEnabled();

	/**
	 * @param rootPackage
	 * @param sourceFolder
	 * @param classpath
	 */
	JEFix(final String rootPackage, final File sourceFolder, final URL[] classpath) {
		this.rootPackage = rootPackage;
		this.sourceFolder = sourceFolder;
		this.classpath = classpath;
	}

	Object build() {

		String[] testClasses = new TestClassesFinder(this.rootPackage).findIn(this.classpath);

		Iterable<SuspiciousStatement> statements = GZoltarSuspiciousProgramStatements.create(this.rootPackage,
				this.classpath, testClasses).sortBySuspiciousness();

		return new Object();
	}
}
