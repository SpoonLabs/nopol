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
package fr.inria.lille.repair;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.infinitel.InfinitelLauncher;
import fr.inria.lille.repair.nopol.NoPolLauncher;
import fr.inria.lille.repair.ranking.Ranking;
import fr.inria.lille.repair.symbolic.SymbolicLauncher;
import fr.inria.lille.repair.common.synth.StatementType;

public class Main {
	private static JSAP jsap = new JSAP();
	private static String mode;
	private static String type;
	private static String execution;
	private static String[] tests;
	private static String solverPath;
	private static String solver;
	private static String source;
	private static String classpath;

	private static void parseArguments(String[] args) throws JSAPException {
		UnflaggedOption opt1 = new UnflaggedOption("mode")
				.setUsageName("repair|ranking")
				.setStringParser(JSAP.STRING_PARSER).setDefault("repair")
				.setRequired(true);
		opt1.setHelp("The nopol mode. The repair mode is used to generate patch. The ranking mode is used to display suspicious statements");
		jsap.registerParameter(opt1);
		JSAPResult config = jsap.parse(args[0]);

		mode = config.getString("mode");
		if (!mode.equals("repair") && !mode.equals("ranking")) {
			System.err
					.println("Error: the mode \"" + mode + "\" is not valid.");

			showUsage();
		} else {
			//jsap = new JSAP();
		}
		type = "";
		if (mode.equals("repair")) {
			String[] types = new String[] { "loop", "condition",
					"precondition", "arithmetic" };
			UnflaggedOption opt2 = new UnflaggedOption("type")
					.setUsageName("statement-type")
					.setStringParser(JSAP.STRING_PARSER)
					.setDefault("condition").setRequired(true);
			opt2.setHelp("The statement to analyse (possible values: loop, condition, precondition, arithmetic).");
			jsap.registerParameter(opt2);

			config = jsap.parse(args);
			type = config.getString("type");
			boolean found = false;
			for (String string : types) {
				if (string.equals(type)) {
					found = true;
					break;
				}
			}
			if (!found) {
				showUsage();
			}
			if (!type.equals("loop")) {
				UnflaggedOption opt3 = new UnflaggedOption("execution")
						.setStringParser(JSAP.STRING_PARSER)
						.setDefault(new String[] { "angelic", "symbolic" })
						.setRequired(true).setGreedy(false);
				opt3.setHelp("The execution type can be angelic or symbolic");
				jsap.registerParameter(opt3);
				config = jsap.parse(args);
				execution = config.getString("execution");
			}
		}

		UnflaggedOption opt4 = new UnflaggedOption("source")
				.setStringParser(JSAP.STRING_PARSER).setRequired(true)
				.setGreedy(false);
		opt4.setHelp("The source folder of the project.");
		jsap.registerParameter(opt4);
		config = jsap.parse(args);
		source = config.getString("source");

		UnflaggedOption opt5 = new UnflaggedOption("classpath")
				.setStringParser(JSAP.STRING_PARSER).setRequired(true)
				.setGreedy(false);
		opt5.setHelp("The classpath of the project.");
		jsap.registerParameter(opt5);

		if (mode.equals("repair")) {
			UnflaggedOption opt6 = new UnflaggedOption("solver")
					.setUsageName("z3|cvc4")
					.setStringParser(JSAP.STRING_PARSER).setRequired(true)
					.setGreedy(false);
			opt6.setHelp("The solver SAT type (possible values: z3 and cvc4).");
			jsap.registerParameter(opt6);

			UnflaggedOption opt7 = new UnflaggedOption("solver-path")
					.setStringParser(JSAP.STRING_PARSER).setRequired(true)
					.setGreedy(false);
			opt7.setHelp("The path to chosen solver binary");
			jsap.registerParameter(opt7);
		}

		UnflaggedOption opt8 = new UnflaggedOption("tests").setList(true)
				.setStringParser(JSAP.STRING_PARSER).setRequired(false)
				.setGreedy(false);
		opt8.setHelp("Test classes");
		jsap.registerParameter(opt8);

		config = jsap.parse(args);
		classpath = config.getString("classpath");
		solver = config.getString("solver");
		solverPath = config.getString("solver-path");
		tests = config.getStringArray("tests");
		if (!config.success()) {

			System.err.println();

			// print out specific error messages describing the problems
			// with the command line, THEN print usage, THEN print full
			// help. This is called "beating the user with a clue stick."
			for (java.util.Iterator<?> errs = config.getErrorMessageIterator(); errs
					.hasNext();) {
				System.err.println("Error: " + errs.next());
			}

			System.err.println();
			System.err.println("Usage: java " + Main.class.getName());
			System.err.println("                " + jsap.getUsage());
			System.err.println();
			System.err.println(jsap.getHelp());
			System.exit(1);
		}
	}

	private static StatementType parseStatementType(String type) {
		if(type.equals("condition")) {
			return StatementType.CONDITIONAL;
		} else if(type.equals("precondition")) {
			return StatementType.PRECONDITION;
		} else if(type.equals("arithmetic")) {
			return StatementType.INTEGER_LITERAL;
		} 
		return null;
	}
	public static void main(String[] args) {
		try {
			parseArguments(args);

			File sourceFile = FileLibrary.openFrom(source);
			URL[] classpath = JavaLibrary.classpathFrom(Main.classpath);
			new Main(mode, execution, sourceFile, classpath, parseStatementType(type), solver,
					solverPath, tests);
			// new Main(args, repairMethod, sourceFile, classpath);
		} catch (Exception e) {
			e.printStackTrace();
			showUsage();
		}
	}

	private Main(String mode, String execution, File sourceFile,
			URL[] classpath, StatementType type, String solver, String solverPath,
			String[] tests) {
		if (mode.equals("repair")) {
			SolverFactory.setSolver(solver, solverPath);
			if (type.equals("loop")) {
				InfinitelLauncher.launch(sourceFile, classpath, tests);
			} else if (execution.equals("symbolic")) {
				SymbolicLauncher.launch(sourceFile, classpath, type, tests);
			} else if (execution.equals("angelic")) {
				NoPolLauncher.launch(sourceFile, classpath, type, tests);
			} else {
				throw new RuntimeException("Invalid repair type: " + execution);
			}
		} else if (mode.equals("ranking")) {
			Ranking ranking = new Ranking(sourceFile, classpath, tests);
			System.out.println(ranking.summary());
		} else {
			throw new RuntimeException("Invalid repair method: " + mode);
		}
	}

	/*private Main(String[] args, String repairMethod, File sourceFile,
			URL[] classpath) {
		String[] remainder = Arrays.copyOfRange(args, 5, args.length);
		if (repairMethod.equalsIgnoreCase("nopol")) {
			NoPolLauncher.launch(sourceFile, classpath, remainder);
		} else if (repairMethod.equalsIgnoreCase("infinitel")) {
			InfinitelLauncher.launch(sourceFile, classpath, remainder);
		} else if (repairMethod.equalsIgnoreCase("symbolic")) {
			SymbolicLauncher.launch(sourceFile, classpath, remainder);
		} else if (repairMethod.equalsIgnoreCase("ranking")) {
			Ranking ranking = new Ranking(sourceFile, classpath, remainder);
			System.out.println(ranking.summary());
		} else {
			throw new RuntimeException("Invalid repair method: " + repairMethod);
		}
	}*/

	private static void showUsage() {
		System.err.println();
		System.err.println("Usage: java " + Main.class.getName());
		System.err.println("                " + jsap.getUsage());
		System.err.println();
		System.err.println(jsap.getHelp());
		System.exit(1);

		try {
			InputStream usageDeatil = FileLibrary.resource("/usage")
					.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					usageDeatil));
			String currentLine = "";
			while (currentLine != null) {
				System.out.println(currentLine);
				currentLine = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unexpected: usage detail file not found");
		}
	}
}
