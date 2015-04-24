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
package fr.inria.lille.repair.nopol.sps.gzoltar;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

import java.io.File;
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

import fr.inria.lille.repair.nopol.sps.SuspiciousProgramStatements;
import fr.inria.lille.repair.nopol.sps.SuspiciousStatement;

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
	 * @param source
	 * @param classpath
	 * @return
	 */
	public static GZoltarSuspiciousProgramStatements create(File source, URL[] classpath, Collection<String> packageNames) {
		return new GZoltarSuspiciousProgramStatements(source, checkNotNull(classpath), checkNotNull(packageNames));
	}

	private final GZoltar gzoltar;

	private GZoltarSuspiciousProgramStatements(File source, final URL[] classpath, Collection<String> packageNames) {
		try {
			gzoltar = new GZoltarJava7();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		HashSet<String> classpaths = new HashSet<>(gzoltar.getClasspaths());
		for (URL url : classpath) {
			if ("file".equals(url.getProtocol())) {
				classpaths.add(url.getPath());
			} else {
				classpaths.add(url.toExternalForm());
			}
		}
		
		gzoltar.setClassPaths(new ArrayList<String>(classpaths));
		gzoltar.addPackageNotToInstrument("org.junit");
		gzoltar.addPackageNotToInstrument("junit.framework");
		for (String packageName : packageNames) {
			gzoltar.addPackageToInstrument(packageName);
		}
	}

	/**
	 * @param testClasses
	 * @return a ranked list of potential bug root-cause.
	 * @see fr.inria.lille.jsemfix.sps.SuspiciousProgramStatements#sortBySuspiciousness()
	 */
	@Override
	public List<SuspiciousStatement> sortBySuspiciousness(final String... testClasses) {

		for (String className : checkNotNull(testClasses)) {
			gzoltar.addTestToExecute(className); // we want to execute the test
			gzoltar.addClassNotToInstrument(className); // we don't want to include the test as root-cause
			// candidate
		}
		gzoltar.run();

		List<SuspiciousStatement> statements = from(gzoltar.getSuspiciousStatements())
				.filter(IsSuspicious.INSTANCE).transform(GZoltarStatementWrapperFunction.INSTANCE).toList();
		
		Logger logger = LoggerFactory.getLogger(this.getClass());
		if (logger.isDebugEnabled()) {
			logger.debug("Suspicious statements:\n{}", Joiner.on('\n').join(statements));
		}

		return sortByDescendingOrder(statements);
	}
	
	public GZoltar getGzoltar() {
		return gzoltar;
	}
	
	private List<SuspiciousStatement> sortByDescendingOrder(List<SuspiciousStatement> statements) {
		List<SuspiciousStatement> tmp = new ArrayList<>(statements);
		Collections.sort(tmp, new Comparator<SuspiciousStatement>() {
			@Override
			public int compare(final SuspiciousStatement o1, final SuspiciousStatement o2) {
				// reversed parameters because we want a descending order list
				return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness());
			}
		});
		return tmp;
	}
}
