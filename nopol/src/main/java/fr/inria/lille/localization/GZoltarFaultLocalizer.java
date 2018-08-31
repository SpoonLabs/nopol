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
package fr.inria.lille.localization;

import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.components.count.ComponentCount;
import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import gov.nasa.jpf.tool.Run;
import xxl.java.junit.TestCase;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A list of potential bug root-cause.
 *
 * @author Favio D. DeMarco
 */
public final class GZoltarFaultLocalizer extends GZoltar implements FaultLocalizer {

	private static final String dir = System.getProperty("user.dir");
	private Metric metric;

	private List<StatementExt> statements;

	// encapsulates the try/catcch forced by gzoltar
	public static GZoltarFaultLocalizer createInstance(NopolContext nopolContext) {
		try {
			return new GZoltarFaultLocalizer(nopolContext);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/** uses {@link #createInstance(NopolContext)} */
	private GZoltarFaultLocalizer(NopolContext nopolContext) throws IOException {
		this(nopolContext.getProjectClasspath(), checkNotNull(Arrays.asList("")), nopolContext.getProjectTests(), new Ochiai());
	}

	private GZoltarFaultLocalizer(final URL[] classpath, Collection<String> packageNames, String[] test, Metric metric) throws IOException {
		super(dir);
		this.metric = metric;
		ArrayList<String> classpaths = new ArrayList<>();
		for (URL url : classpath) {
			if ("file".equals(url.getProtocol())) {
				classpaths.add(url.getPath());
			} else {
				classpaths.add(url.toExternalForm());
			}
		}

		this.setClassPaths(classpaths);
		this.addPackageNotToInstrument("org.junit");
		this.addPackageNotToInstrument("junit.framework");
		this.addPackageNotToInstrument("org.easymock");
		this.addTestPackageNotToExecute("junit.framework");
		this.addTestPackageNotToExecute("org.junit");
		this.addTestPackageNotToExecute("org.easymock");
		for (String packageName : packageNames) {
			this.addPackageToInstrument(packageName);
		}

		this.statements = run(test);
	}

	@Override
	public List<? extends StatementSourceLocation> getStatements() {
		return this.statements;
	}

	@Override
	public List<com.gzoltar.core.instr.testing.TestResult> getTestResults() {
		return super.getTestResults();
	}

	@Override
	public Map<SourceLocation, List<fr.inria.lille.localization.TestResult>> getTestListPerStatement() {
		Map<SourceLocation, List<fr.inria.lille.localization.TestResult>> results = new HashMap<>();
		List<TestResult> testResults = this.getTestResults();

		for (int j = 0; j < testResults.size(); j++) {
			TestResult testResult = testResults.get(j);
			TestResultImpl test = new TestResultImpl(TestCase.from(testResult.getName()), testResult.wasSuccessful());

			List<ComponentCount> components = testResult.getCoveredComponents();
			for (int i = 0; i < components.size(); i++) {
				ComponentCount component1 = components.get(i);
				Statement component = (Statement) component1.getComponent();
				String containingClass = component.getMethod().getParent().getLabel();

				SourceLocation sourceLocation = new SourceLocation(containingClass, component.getLineNumber());
				if (!results.containsKey(sourceLocation)) {
					results.put(sourceLocation, new ArrayList<fr.inria.lille.localization.TestResult>());
				}
				results.get(sourceLocation).add(test);
			}
		}

		LinkedHashMap<SourceLocation, List<fr.inria.lille.localization.TestResult>> map = new LinkedHashMap<>();
		for (StatementSourceLocation source : this.statements) {
			map.put(source.getLocation(), results.get(source.getLocation()));
		}

		results = map;
		return results;
	}

	/**
	 * @param testClasses
	 * @return a ranked list of potential bug root-cause.
	 */
	private List<StatementExt> run(final String... testClasses) {
		for (String className : checkNotNull(testClasses)) {
			this.addTestToExecute(className); // we want to execute the test
			this.addClassNotToInstrument(className); // we don't want to include the test as root-cause
			// candidate
		}
		final String systemClasspath = System.getProperty("java.class.path");

		// remove classpath noise
		String[] deps = systemClasspath.split(":");
		StringBuilder cl = new StringBuilder();
		for (int i = 0; i < deps.length; i++) {
			String dep = deps[i];
			if (dep.contains("jre") || dep.contains("gzoltar") || dep.contains("nopol")) {
				cl.append(dep).append(":");
			}
		}
		try {
			System.setProperty("java.class.path", cl.toString());
			setGzoltarDebug(false);
			this.run();
		} finally {
			System.setProperty("java.class.path", systemClasspath);
		}
		return this.getSuspiciousStatements(this.metric);
	}

	protected void setGzoltarDebug(boolean debugValue) {
		try {
			Field debug = com.gzoltar.core.agent.Launcher.class.getDeclaredField("debug");
			debug.setAccessible(true);
			debug.setBoolean(null, debugValue);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	private List<StatementExt> getSuspiciousStatements(final Metric metric) {
		List<Statement> suspiciousStatements = super.getSuspiciousStatements();

		List<StatementExt> result = new ArrayList<>(suspiciousStatements.size());
		int successfulTests;
		int nbFailingTest = 0;
		List<TestResult> testResults = super.getTestResults();
		for (int i = testResults.size() - 1; i >= 0; i--) {
			TestResult testResult = testResults.get(i);
			if (!testResult.wasSuccessful()) {
				nbFailingTest++;
			}
		}
		successfulTests = testResults.size() - nbFailingTest;
		for (int i = suspiciousStatements.size() - 1; i >= 0; i--) {
			Statement statement = suspiciousStatements.get(i);
			BitSet coverage = statement.getCoverage();
			int executedAndPassedCount = 0;
			int executedAndFailedCount = 0;
			int nextTest = coverage.nextSetBit(0);
			while (nextTest != -1) {
				TestResult testResult = testResults.get(nextTest);
				if (testResult.wasSuccessful()) {
					executedAndPassedCount++;
				} else {
					executedAndFailedCount++;
				}
				nextTest = coverage.nextSetBit(nextTest + 1);
			}
			StatementExt s = new StatementExt(metric, statement);
			s.setEf(executedAndFailedCount);
			s.setEp(executedAndPassedCount);
			s.setNp(successfulTests - executedAndPassedCount);
			s.setNf(nbFailingTest - executedAndFailedCount);
			result.add(s);
		}
		Collections.sort(result, new Comparator<StatementExt>() {
			@Override
			public int compare(final StatementExt o1, final StatementExt o2) {
				// reversed parameters because we want a descending order list
				return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness());
			}
		});
		return result;
	}

	@Deprecated
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
