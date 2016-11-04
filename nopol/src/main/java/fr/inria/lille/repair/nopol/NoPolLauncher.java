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
package fr.inria.lille.repair.nopol;

import fr.inria.lille.commons.synthesis.ConstraintBasedSynthesis;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.repair.Main;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.synth.SMTNopolSynthesizer;
import fr.inria.lille.repair.nopol.synth.SynthesizerFactory;
import org.apache.commons.io.FileUtils;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.net.URL;
import java.util.*;

import static java.lang.String.format;

public class NoPolLauncher {

	public static void main(String[] args) {
		String filePath = "misc/nopol-example/src/";
		String binFolder = "misc/nopol-example/bin/";
		String junitJar = "misc/nopol-example/junit-4.11.jar";
		String classpath = binFolder + File.pathSeparatorChar + junitJar;
		String type = args[0];
		String solverName = args[1];
		String solverPath = args[2];
		System.out.println(format("args:\n\t%s\n\t%s\n\t%s\n\t%s", filePath, classpath, solverName, solverPath));
		Main.main(new String[]{"repair", type, "angelic", filePath, classpath, solverName, solverPath});
		System.exit(1);
	}

	public static List<Patch> launch(File[] sourceFile, URL[] classpath, Config config) {
		StatementType type = config.getType();
		String[] args = config.getProjectTests();
		System.out.println("Source files: " + Arrays.toString(sourceFile));
		System.out.println("Classpath: " + Arrays.toString(classpath));
		System.out.println("Statement type: " + type);
		System.out.println("Args: " + Arrays.toString(args));
		System.out.println("Config: " + config);
		System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());

    	/* Total amount of free memory available to the JVM */
		System.out.println("Free memory: " + FileUtils.byteCountToDisplaySize(Runtime.getRuntime().freeMemory()));

    	/* This will return Long.MAX_VALUE if there is no preset limit */
		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		System.out.println("Maximum memory: " +
				(maxMemory == Long.MAX_VALUE ? "no limit" : FileUtils.byteCountToDisplaySize(maxMemory)));

    	/* Total memory currently available to the JVM */
		System.out.println("Total memory available to JVM: " +
				FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory()));

		System.out.println("Java version: " + Runtime.class.getPackage().getImplementationVersion());
		System.out.println("JAVA_HOME: " + System.getenv("JAVA_HOME"));
		System.out.println("PATH: " + System.getenv("PATH"));

		return runNopol(sourceFile, classpath, config, args);
	}

	private static List<Patch> runNopol(File[] sourceFile, URL[] classpath, Config config, String[] args) {
		long executionTime = System.currentTimeMillis();
		NoPol nopol = new NoPol(sourceFile, classpath, config);
		List<Patch> patches = null;
		try {
			if (args.length > 0) {
				patches = nopol.build(args);
			} else {
				patches = nopol.build();
			}
		} catch (NoSuspiciousStatementException stmt) {
			throw stmt;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executionTime = System.currentTimeMillis() - executionTime;
			displayResult(nopol, patches, executionTime, config);
			nbPassedTestExecution.clear();
			if (nbFailingTestExecution.isEmpty()) {
				throw new NoFailingTestCaseException("NoPol did not find any failing test case." , "No failing test case");
			} else {
				nbFailingTestExecution.clear();
			}
		}
		return patches;
	}

	public static ArrayList<Integer> nbFailingTestExecution = new ArrayList<>();
	public static ArrayList<Integer> nbPassedTestExecution = new ArrayList<>();

	private static void displayResult(NoPol nopol, List<Patch> patches, long executionTime, Config config) {
		System.out.println("----INFORMATION----");
		List<CtType<?>> allClasses = nopol.getSpooner().spoonFactory().Class().getAll();
		int nbMethod = 0;
		for (int i = 0; i < allClasses.size(); i++) {
			CtType<?> ctSimpleType = allClasses.get(i);
			if (ctSimpleType instanceof CtClass) {
				Set methods = ((CtClass) ctSimpleType).getMethods();
				nbMethod += methods.size();
			}
		}
		System.out.println("Nb classes : " + allClasses.size());
		System.out.println("Nb methods : " + nbMethod);
		if (NoPol.currentStatement != null) {
			BitSet coverage = NoPol.currentStatement.getCoverage();
			int countStatementSuccess = 0;
			int countStatementFailed = 0;
			int nextTest = coverage.nextSetBit(0);
			/*while (nextTest != -1) {
				TestResultImpl testResult = nopol.getgZoltar().getGzoltar().getTestResults().get(nextTest);
				if (testResult.wasSuccessful()) {
					countStatementSuccess += testResult.getCoveredComponents().size();
				} else {
					countStatementFailed += testResult.getCoveredComponents().size();
				}
				nextTest = coverage.nextSetBit(nextTest + 1);
			}*/

			System.out.println("Nb statement executed by the passing tests of the patched line: " + countStatementSuccess);
			System.out.println("Nb statement executed by the failing tests of the patched line: " + countStatementFailed);
		}
//		System.out.println("Nb statements: " + nopol.getgZoltar().getGzoltar().getSpectra().getNumberOfComponents());
//		System.out.println("Nb unit tests : " + nopol.getgZoltar().getGzoltar().getTestResults().size());
		System.out.println("Nb Statements Analyzed : " + SynthesizerFactory.getNbStatementsAnalysed());
		System.out.println("Nb Statements with Angelic Value Found : " + SMTNopolSynthesizer.getNbStatementsWithAngelicValue());
		if (config.getSynthesis() == Config.NopolSynthesis.SMT) {
			System.out.println("Nb inputs in SMT : " + SMTNopolSynthesizer.getDataSize());
			System.out.println("Nb SMT level: " + ConstraintBasedSynthesis.level);
			if (ConstraintBasedSynthesis.operators != null) {
				System.out.println("Nb SMT components: [" + ConstraintBasedSynthesis.operators.size() + "] " + ConstraintBasedSynthesis.operators);
				Iterator<Operator<?>> iterator = ConstraintBasedSynthesis.operators.iterator();
				Map<Class, Integer> mapType = new HashMap<>();
				while (iterator.hasNext()) {
					Operator<?> next = iterator.next();
					if (!mapType.containsKey(next.type())) {
						mapType.put(next.type(), 1);
					} else {
						mapType.put(next.type(), mapType.get(next.type()) + 1);
					}
				}
				for (Iterator<Class> patchIterator = mapType.keySet().iterator(); patchIterator.hasNext(); ) {
					Class next = patchIterator.next();
					System.out.println("                  " + next + ": " + mapType.get(next));
				}
			}

			System.out.println("Nb variables in SMT : " + SMTNopolSynthesizer.getNbVariables());
		}
		System.out.println("Nb run failing test  : " + nbFailingTestExecution);
		System.out.println("Nb run passing test : " + nbPassedTestExecution);

		System.out.println("NoPol Execution time : " + executionTime + "ms");

		if (patches != null && !patches.isEmpty()) {
			System.out.println("----PATCH FOUND----");
			for (Patch patch : patches) {
				System.out.println(patch);
			}
		}
	}
}
