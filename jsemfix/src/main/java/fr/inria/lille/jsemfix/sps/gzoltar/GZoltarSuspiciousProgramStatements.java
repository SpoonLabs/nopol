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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.gzoltar.core.GZoltar;

import fr.inria.lille.jsemfix.functors.ClassName;
import fr.inria.lille.jsemfix.sps.SuspiciousProgramStatements;
import fr.inria.lille.jsemfix.sps.SuspiciousStatement;

/**
 * 
 * A list of potential bug root-cause.
 * 
 * @author Favio D. DeMarco
 */
public final class GZoltarSuspiciousProgramStatements implements SuspiciousProgramStatements {

	/**
	 * XXX FIXME TODO too many arguments. Builder?
	 * 
	 * @param sourcePackage
	 * @param classpath
	 * @param testClasses
	 * @return
	 */
	public static GZoltarSuspiciousProgramStatements create(final String sourcePackage, final URL[] classpath,
			final String... testClasses) {
		return new GZoltarSuspiciousProgramStatements(sourcePackage, classpath, testClasses);
	}

	public static GZoltarSuspiciousProgramStatements createWithPackageAndTestClasses(final Package sourcePackage,
			final Class<?>... testClasses) {
		return createWithPackageAndTestClasses(sourcePackage.getName(), testClasses);
	}

	public static GZoltarSuspiciousProgramStatements createWithPackageAndTestClasses(final String sourcePackage,
			final Class<?>... testClasses) {

		return new GZoltarSuspiciousProgramStatements(sourcePackage, Collections2.transform(Arrays.asList(testClasses),
				ClassName.INSTANCE).toArray(new String[testClasses.length]));
	}

	private final List<SuspiciousStatement> statements;

	private GZoltarSuspiciousProgramStatements(final String sourcePackage, final String... testClasses) {
		this(sourcePackage, (URL[]) null, testClasses);
	}

	private GZoltarSuspiciousProgramStatements(final String sourcePackage, final URL[] classpath,
			final String... testClasses) {

		GZoltar gzoltar;
		try {
			gzoltar = new GZoltarJava7();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		if (null != classpath) {
			HashSet<String> classpaths = gzoltar.getClasspaths();
			for (URL url : classpath) {
				classpaths.add(url.toExternalForm());
			}
			gzoltar.setClassPaths(classpaths);
		}
		gzoltar.addPackageToInstrument(checkNotNull(sourcePackage)); // TODO see if GZoltar instruments
		// recursively

		for (String className : checkNotNull(testClasses)) {
			gzoltar.addTestToExecute(className); // we want to execute the test
			gzoltar.addClassNotToInstrument(className); // we don't want to include the test as root-cause candidate
		}
		gzoltar.run();

		Logger logger = LoggerFactory.getLogger(this.getClass());
		if (logger.isDebugEnabled()) {
			logger.debug("\n{}", gzoltar.getSpectra());
		}

		this.statements = Lists.transform(gzoltar.getSuspiciousStatements(), GZoltarStatementWrapperFunction.INSTANCE);
	}

	/**
	 * TODO delete this method
	 */
	private void assertExpectedOrder() {
		List<SuspiciousStatement> sortedStatementsList = new ArrayList<SuspiciousStatement>(this.statements);
		Collections.sort(sortedStatementsList, new Comparator<SuspiciousStatement>() {
			@Override
			public int compare(final SuspiciousStatement o1, final SuspiciousStatement o2) {
				return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness()); // reversed parameters because we
				// want a descending order list
			}
		});
		assert this.statements.equals(sortedStatementsList) : "The order does not match:\n" + this.statements + '\n'
		+ sortedStatementsList;
	}

	/**
	 * @return a ranked list of potential bug root-cause.
	 * @see fr.inria.lille.jsemfix.sps.SuspiciousProgramStatements#sortBySuspiciousness()
	 */
	@Override
	public List<SuspiciousStatement> sortBySuspiciousness() {

		// TODO delete this method call
		this.assertExpectedOrder();

		return this.statements;
	}
}
