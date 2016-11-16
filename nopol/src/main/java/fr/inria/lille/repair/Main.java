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


import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.infinitel.Infinitel;
import fr.inria.lille.repair.nopol.NoPolLauncher;
import fr.inria.lille.repair.ranking.Ranking;
import org.slf4j.LoggerFactory;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.concurrent.*;

public class Main {
	private static JSAP jsap = new JSAP();

	public static void main(String[] args) {
		int returnCode = -1;
		try {
			final Config config = new Config();
			initJSAP();
			if (!parseArguments(args, config)) {
				return;
			}

			//For using Dynamoth, you must add tools.jar in the classpath
			if (config.getSynthesis() == Config.NopolSynthesis.DYNAMOTH) {
				URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
				try {
					loader.loadClass("com.sun.jdi.Value");
				} catch (ClassNotFoundException e) {
					System.err.println("For using Dynamoth, you must add tools.jar in your classpath from your installed jdk");
					System.exit(-1);
				}
			}

			final File[] sourceFiles = new File[config.getProjectSourcePath().length];
			for (int i = 0; i < config.getProjectSourcePath().length; i++) {
				String path = config.getProjectSourcePath()[i];
				File sourceFile = FileLibrary.openFrom(path);
				sourceFiles[i] = sourceFile;
			}

			final URL[] classpath = JavaLibrary.classpathFrom(config.getProjectClasspath());


			switch (config.getMode()) {
				case REPAIR:
					switch (config.getType()) {
						case LOOP:
							ProjectReference project = new ProjectReference(sourceFiles, classpath, config.getProjectTests());
							Infinitel infinitel = new Infinitel(project, config);
							infinitel.repair();
							break;
						default:
							final ExecutorService executor = Executors.newSingleThreadExecutor();
							final Future nopolExecution = executor.submit(
									new Callable() {
										@Override
										public Object call() throws Exception {
											return NoPolLauncher.launch(sourceFiles, classpath, config).isEmpty() ? -1 : 0;
										}
									});
							try {
								returnCode = (int) nopolExecution.get(config.getMaxTimeInMinutes(), TimeUnit.MINUTES);
							} catch (TimeoutException exception) {
								LoggerFactory.getLogger(Main.class).error("Timeout: execution time > " + config.getMaxTimeInMinutes() + " " + TimeUnit.MINUTES, exception);
							}
							break;
					}
					break;
				case RANKING:
					Ranking ranking = new Ranking(sourceFiles, classpath, config.getProjectTests());
					System.out.println(ranking.summary());
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			showUsage();
		}
		System.exit(returnCode);
	}

	private static void showUsage() {
		System.err.println();
		System.err.println("Usage: java -jar nopol.jar");
		System.err.println("                          " + jsap.getUsage());
		System.err.println();
		System.err.println(jsap.getHelp());
	}


	private static boolean parseArguments(String[] args, Config config) {
		JSAPResult jsapConfig = jsap.parse(args);
		if (!jsapConfig.success()) {
			System.err.println();
			for (Iterator<?> errs = jsapConfig.getErrorMessageIterator(); errs.hasNext(); ) {
				System.err.println("Error: " + errs.next());
			}
			showUsage();
			return false;
		}

		config.setType(strToStatementType(jsapConfig.getString("type")));
		config.setMode(strToMode(jsapConfig.getString("mode")));
		config.setSynthesis(strToSynthesis(jsapConfig.getString("synthesis")));
		config.setOracle(strToOracle(jsapConfig.getString("oracle")));
		config.setSolver(strToSolver(jsapConfig.getString("solver")));
		if (jsapConfig.getString("solverPath") != null) {
			config.setSolverPath(jsapConfig.getString("solverPath"));
			SolverFactory.setSolver(config.getSolver(), config.getSolverPath());
		}
		config.setProjectClasspath(jsapConfig.getString("classpath"));
		config.setProjectSourcePath(jsapConfig.getStringArray("source"));
		config.setProjectTests(jsapConfig.getStringArray("test"));
		config.setComplianceLevel(jsapConfig.getInt("complianceLevel", 7));
		config.setMaxTimeInMinutes(jsapConfig.getInt("maxTime", 10));
		config.setMaxTimeEachTypeOfFixInMinutes(jsapConfig.getInt("maxTimeType", 5));
		config.setLocalizer(strToLocalizer(jsapConfig.getString("faultLocalization")));
		return true;
	}

	private static Config.NopolSynthesis strToSynthesis(String str) {
		if (str.equals("smt")) {
			return Config.NopolSynthesis.SMT;
		} else if (str.equals("dynamoth")) {
			return Config.NopolSynthesis.DYNAMOTH;
		}
		throw new RuntimeException("Unknow Nopol oracle " + str);
	}

	private static Config.NopolOracle strToOracle(String str) {
		if (str.equals("angelic")) {
			return Config.NopolOracle.ANGELIC;
		} else if (str.equals("symbolic")) {
			return Config.NopolOracle.SYMBOLIC;
		}
		throw new RuntimeException("Unknow Nopol oracle " + str);
	}

	private static Config.NopolSolver strToSolver(String str) {
		if (str.equals("z3")) {
			return Config.NopolSolver.Z3;
		} else if (str.equals("cvc4")) {
			return Config.NopolSolver.CVC4;
		}
		throw new RuntimeException("Unknow Nopol solver " + str);
	}

	private static Config.NopolMode strToMode(String str) {
		if (str.equals("repair")) {
			return Config.NopolMode.REPAIR;
		} else if (str.equals("ranking")) {
			return Config.NopolMode.RANKING;
		}
		throw new RuntimeException("Unknow Nopol mode " + str);
	}

	private static StatementType strToStatementType(String str) {
		if (str.equals("pre_then_cond")) {
			return StatementType.PRE_THEN_COND;
		} else if (str.equals("loop")) {
			return StatementType.LOOP;
		} else if (str.equals("condition")) {
			return StatementType.CONDITIONAL;
		} else if (str.equals("precondition")) {
			return StatementType.PRECONDITION;
		} else if (str.equals("arithmetic")) {
			return StatementType.INTEGER_LITERAL;
		}
		return StatementType.NONE;
	}

	private static Config.NopolLocalizer strToLocalizer(String str) {
		if (str.equals("gzoltar")) {
			return Config.NopolLocalizer.GZOLTAR;
		} else if (str.equals("dumb")) {
			return Config.NopolLocalizer.DUMB;
		} else
			return Config.NopolLocalizer.OCHIAI;
	}

	private static void initJSAP() throws JSAPException {
		FlaggedOption modeOpt = new FlaggedOption("mode");
		modeOpt.setRequired(false);
		modeOpt.setAllowMultipleDeclarations(false);
		modeOpt.setLongFlag("mode");
		modeOpt.setShortFlag('m');
		modeOpt.setUsageName("repair|ranking");
		modeOpt.setStringParser(JSAP.STRING_PARSER);
		modeOpt.setDefault("repair");
		modeOpt.setHelp("Define the mode of execution.");
		jsap.registerParameter(modeOpt);

		FlaggedOption typeOpt = new FlaggedOption("type");
		typeOpt.setRequired(false);
		typeOpt.setAllowMultipleDeclarations(false);
		typeOpt.setLongFlag("type");
		typeOpt.setShortFlag('e');
		typeOpt.setUsageName("pre_then_cond|loop|condition|precondition|arithmetic");
		typeOpt.setStringParser(JSAP.STRING_PARSER);
		typeOpt.setDefault("pre_then_cond");
		typeOpt.setHelp("The type of statement to analyze (only used with repair mode).");
		jsap.registerParameter(typeOpt);

		FlaggedOption oracleOpt = new FlaggedOption("oracle");
		oracleOpt.setRequired(false);
		oracleOpt.setAllowMultipleDeclarations(false);
		oracleOpt.setLongFlag("oracle");
		oracleOpt.setShortFlag('o');
		oracleOpt.setUsageName("angelic|symbolic");
		oracleOpt.setStringParser(JSAP.STRING_PARSER);
		oracleOpt.setDefault("angelic");
		oracleOpt.setHelp("Define the oracle (only used with repair mode).");
		jsap.registerParameter(oracleOpt);

		FlaggedOption synthesisOpt = new FlaggedOption("synthesis");
		synthesisOpt.setRequired(false);
		synthesisOpt.setAllowMultipleDeclarations(false);
		synthesisOpt.setLongFlag("synthesis");
		synthesisOpt.setShortFlag('y');
		synthesisOpt.setUsageName("smt|dynamoth");
		synthesisOpt.setStringParser(JSAP.STRING_PARSER);
		synthesisOpt.setDefault("smt");
		synthesisOpt.setHelp("Define the patch synthesis.");
		jsap.registerParameter(synthesisOpt);

		FlaggedOption solverOpt = new FlaggedOption("solver");
		solverOpt.setRequired(false);
		solverOpt.setAllowMultipleDeclarations(false);
		solverOpt.setLongFlag("solver");
		solverOpt.setShortFlag('l');
		solverOpt.setUsageName("z3|cvc4");
		solverOpt.setStringParser(JSAP.STRING_PARSER);
		solverOpt.setDefault("z3");
		solverOpt.setHelp("Define the solver (only used with smt synthesis).");
		jsap.registerParameter(solverOpt);

		FlaggedOption solverPathOpt = new FlaggedOption("solverPath");
		solverPathOpt.setRequired(false);
		solverPathOpt.setAllowMultipleDeclarations(false);
		solverPathOpt.setLongFlag("solver-path");
		solverPathOpt.setShortFlag('p');
		solverPathOpt.setStringParser(JSAP.STRING_PARSER);
		solverPathOpt.setHelp("Define the solver binary path (only used with smt synthesis).");
		jsap.registerParameter(solverPathOpt);

		FlaggedOption sourceOpt = new FlaggedOption("source");
		sourceOpt.setRequired(true);
		sourceOpt.setAllowMultipleDeclarations(false);
		sourceOpt.setLongFlag("source");
		sourceOpt.setShortFlag('s');
		sourceOpt.setStringParser(JSAP.STRING_PARSER);
		sourceOpt.setList(true);
		sourceOpt.setHelp("Define the path to the source code of the project.");
		jsap.registerParameter(sourceOpt);

		FlaggedOption classpathOpt = new FlaggedOption("classpath");
		classpathOpt.setRequired(true);
		classpathOpt.setAllowMultipleDeclarations(false);
		classpathOpt.setLongFlag("classpath");
		classpathOpt.setShortFlag('c');
		classpathOpt.setStringParser(JSAP.STRING_PARSER);
		classpathOpt.setHelp("Define the classpath of the project.");
		jsap.registerParameter(classpathOpt);

		FlaggedOption testOpt = new FlaggedOption("test");
		testOpt.setRequired(false);
		testOpt.setAllowMultipleDeclarations(false);
		testOpt.setLongFlag("test");
		testOpt.setShortFlag('t');
		testOpt.setList(true);
		testOpt.setStringParser(JSAP.STRING_PARSER);
		testOpt.setHelp("Define the tests of the project.");
		jsap.registerParameter(testOpt);

		FlaggedOption complianceLevelOpt = new FlaggedOption("complianceLevel");
		complianceLevelOpt.setRequired(false);
		complianceLevelOpt.setAllowMultipleDeclarations(false);
		complianceLevelOpt.setLongFlag("complianceLevel");
		complianceLevelOpt.setStringParser(JSAP.INTEGER_PARSER);
		complianceLevelOpt.setDefault("7");
		complianceLevelOpt.setHelp("The compliance level of the project.");
		jsap.registerParameter(complianceLevelOpt);

		FlaggedOption maxTime = new FlaggedOption("maxTime");
		maxTime.setRequired(false);
		maxTime.setAllowMultipleDeclarations(false);
		maxTime.setLongFlag("maxTime");
		maxTime.setStringParser(JSAP.INTEGER_PARSER);
		maxTime.setDefault("10");
		maxTime.setHelp("The maximum time execution in minute for the whole execution of Nopol.(default: 10)");
		jsap.registerParameter(maxTime);

		FlaggedOption maxTimeByTypeInMinutes = new FlaggedOption("maxTimeType");
		maxTimeByTypeInMinutes.setRequired(false);
		maxTimeByTypeInMinutes.setAllowMultipleDeclarations(false);
		maxTimeByTypeInMinutes.setLongFlag("maxTimeType");
		maxTimeByTypeInMinutes.setStringParser(JSAP.INTEGER_PARSER);
		maxTimeByTypeInMinutes.setDefault("5");
		maxTimeByTypeInMinutes.setHelp("The maximum time execution in minute for one type of patch. (default: 5)");
		jsap.registerParameter(maxTimeByTypeInMinutes);

		FlaggedOption faultLocalization = new FlaggedOption("faultLocalization");
		faultLocalization.setRequired(false);
		faultLocalization.setAllowMultipleDeclarations(false);
		faultLocalization.setLongFlag("flocal");
		faultLocalization.setShortFlag('z');
		faultLocalization.setUsageName(" ochiai|dumb|gzoltar");//TODO ADD PARAMETIZED FAULT LOCALIZER
		faultLocalization.setStringParser(JSAP.STRING_PARSER);
		faultLocalization.setDefault("ochiai");
		faultLocalization.setHelp("Define the fault localizer to be used.");
		jsap.registerParameter(faultLocalization);
	}
}
