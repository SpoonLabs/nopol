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
package fr.inria.lille.jefix.sps.gzoltar;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Statement;

import fr.inria.lille.jefix.sps.SuspiciousProgramStatements;
import fr.inria.lille.jefix.sps.SuspiciousStatement;

/**
 * 
 * A list of potential bug root-cause.
 * 
 * @author Favio D. DeMarco
 */
public final class GZoltarSuspiciousProgramStatements implements SuspiciousProgramStatements {

	private enum IsSuspicious implements Predicate<Statement> {
		INSTANCE;
		@Override
		public boolean apply(final Statement input) {
			return input.getSuspiciousness() > 0D;
		}
	}

	/**
	 * @param sourcePackage
	 * @param classpath
	 * @return
	 */
	public static GZoltarSuspiciousProgramStatements create(final URL[] classpath) {
		return new GZoltarSuspiciousProgramStatements(checkNotNull(classpath));
	}

	private final GZoltar gzoltar;

	private GZoltarSuspiciousProgramStatements(final URL[] classpath) {
		try {
			this.gzoltar = new GZoltarJava7();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		HashSet<String> classpaths = this.gzoltar.getClasspaths();
		for (URL url : classpath) {
			classpaths.add(url.toExternalForm());
		}
		this.gzoltar.setClassPaths(classpaths);
		this.gzoltar.addPackageToInstrument("");
		this.gzoltar.addPackageNotToInstrument("org.junit");
	}

	/**
	 * TODO delete this method
	 */
	private void assertExpectedOrder(final Collection<SuspiciousStatement> statements) {
		List<SuspiciousStatement> sortedStatementsList = new ArrayList<SuspiciousStatement>(statements);
		Collections.sort(sortedStatementsList, new Comparator<SuspiciousStatement>() {
			@Override
			public int compare(final SuspiciousStatement o1, final SuspiciousStatement o2) {
				return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness()); // reversed parameters because we
				// want a descending order list
			}
		});
		assert statements.equals(sortedStatementsList) : "The order does not match:\n" + statements + '\n'
		+ sortedStatementsList;
	}

	/**
	 * @param testClasses
	 * @return a ranked list of potential bug root-cause.
	 * @see fr.inria.lille.jsemfix.sps.SuspiciousProgramStatements#sortBySuspiciousness()
	 */
	@Override
	public List<SuspiciousStatement> sortBySuspiciousness(final String... testClasses) {

		for (String className : checkNotNull(testClasses)) {
			this.gzoltar.addTestToExecute(className); // we want to execute the test
			this.gzoltar.addClassNotToInstrument(className); // we don't want to include the test as root-cause
			// candidate
		}
		this.gzoltar.run();

		List<SuspiciousStatement> statements = from(this.gzoltar.getSuspiciousStatements())
				.filter(IsSuspicious.INSTANCE).transform(GZoltarStatementWrapperFunction.INSTANCE).toList();

		Logger logger = LoggerFactory.getLogger(this.getClass());
		if (logger.isDebugEnabled()) {
			logger.debug("\n{}", Joiner.on('\n').join(statements));
		}

		// TODO delete this method call
		this.assertExpectedOrder(statements);

		return statements;
	}
}
