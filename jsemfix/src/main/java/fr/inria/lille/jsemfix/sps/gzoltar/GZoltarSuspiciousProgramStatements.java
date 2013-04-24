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
package fr.inria.lille.jsemfix.sps.gzoltar;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.gzoltar.core.GZoltar;

import fr.inria.lille.jsemfix.sps.Statement;
import fr.inria.lille.jsemfix.sps.SuspiciousProgramStatements;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class GZoltarSuspiciousProgramStatements implements SuspiciousProgramStatements {

	public static GZoltarSuspiciousProgramStatements createWithPackageAndTestClasses(Package sourcePackage,
			Class<?> testClass) {
		return new GZoltarSuspiciousProgramStatements(sourcePackage, Collections.<Class<?>> singleton(testClass));
	}

	public static GZoltarSuspiciousProgramStatements createWithPackageAndTestClasses(Package sourcePackage,
			Iterable<Class<?>> testClasses) {
		return new GZoltarSuspiciousProgramStatements(sourcePackage, testClasses);
	}

	private final List<Statement> statements;

	private GZoltarSuspiciousProgramStatements(Package sourcePackage, Iterable<Class<?>> testClasses) {

		GZoltar gzoltar;
		try {
			gzoltar = new GZoltar("/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		gzoltar.addPackageToInstrument(checkNotNull(sourcePackage.getName())); // TODO see if GZoltar instruments
		// recursively
		for (Class<?> testClass : checkNotNull(testClasses)) {
			String className = testClass.getName();
			gzoltar.addTestToExecute(className); // we want to execute the test
			gzoltar.addClassNotToInstrument(className); // we don't want to include the test as root-cause candidate
		}
		gzoltar.run();
		statements = Lists.transform(gzoltar.getSuspiciousStatements(), GZoltarStatementWrapperFunction.INSTANCE);
	}

	/**
	 * TODO delete this method
	 */
	private void assertExpectedOrder() {
		List<Statement> sortedStatementsList = new ArrayList<>(statements);
		Collections.sort(sortedStatementsList, new Comparator<Statement>() {
			@Override
			public int compare(Statement o1, Statement o2) {
				return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness()); // reversed parameters because we
				// want a descending order list
			}
		});
		assert statements.equals(sortedStatementsList) : "The order does not match:\n" + statements + '\n'
				+ sortedStatementsList;
	}

	@Override
	public List<Statement> sortBySuspiciousness() {

		// TODO delete this method call
		assertExpectedOrder();

		return statements;
	}
}
