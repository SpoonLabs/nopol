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

import java.util.Arrays;
import java.util.List;

import sacha.finder.main.TestInClasspath;
import fr.inria.lille.jsemfix.sps.Statement;
import fr.inria.lille.jsemfix.sps.gzoltar.GZoltarSuspiciousProgramStatements;

/**
 * @author Favio D. DeMarco
 *
 */
public final class Fixer {

	private final Package mainPackage;

	/**
	 * @param mainPackage
	 */
	public Fixer(final Package mainPackage) {
		super();
		this.mainPackage = checkNotNull(mainPackage);
	}

	public Patch createPatch() {

		List<Class<?>> testClasses = this.findTestClasses();

		List<Statement> statements = GZoltarSuspiciousProgramStatements.createWithPackageAndTestClasses(
				this.mainPackage, testClasses).sortBySuspiciousness();

		return null;
	}

	private List<Class<?>> findTestClasses() {
		return Arrays.asList(new TestInClasspath().find());
	}
}
