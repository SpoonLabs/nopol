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
import com.martiansoftware.jsap.QualifiedSwitch;
import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.synth.RepairType;
import fr.inria.lille.repair.infinitel.Infinitel;
import fr.inria.lille.repair.nopol.NoPol;
import fr.inria.lille.repair.nopol.NopolResult;
import fr.inria.lille.repair.nopol.NopolStatus;
import fr.inria.lille.repair.ranking.Ranking;
import org.slf4j.LoggerFactory;
import xxl.java.junit.CustomClassLoaderThreadFactory;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
	private JSAP jsap;
	private NopolContext nopolContext;

	public Main() {
		this.jsap = new JSAP();
	}

	public NopolContext getNopolContext() {
		return nopolContext;
	}

	public static void main(String[] args) {
		int returnCode = -1;
		Main main = new Main();
		NopolResult result = null;
		try {
			main.initJSAP();
			if (!main.parseArguments(args)) {
				return;
			}

			final NopolContext nopolContext = main.getNopolContext();

			// use currentDir to compute diff path
			File currentDir = new File(".");
			nopolContext.setRootProject(currentDir.getCanonicalFile().toPath().toAbsolutePath());

			//For using Dynamoth, you must add tools.jar in the classpath
			if (nopolContext.getSynthesis() == NopolContext.NopolSynthesis.DYNAMOTH) {
				URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
				try {
					loader.loadClass("com.sun.jdi.Value");
				} catch (ClassNotFoundException e) {
					System.err.println("For using Dynamoth, you must add tools.jar in your classpath from your installed jdk");
					System.exit(-1);
				}
			}

			switch (nopolContext.getMode()) {
				case REPAIR:
					switch (nopolContext.getType()) {
						case LOOP:
							Infinitel infinitel = new Infinitel(nopolContext);
							infinitel.repair();
							break;
						default:
							final NoPol nopol = new NoPol(nopolContext);
							final ExecutorService executor = Executors.newSingleThreadExecutor(new CustomClassLoaderThreadFactory(Thread.currentThread().getContextClassLoader()));
							final Future<NopolResult> nopolExecution = executor.submit(
									new Callable() {
										@Override
										public Object call() throws Exception {
											return nopol.build();
										}
									});
							try {
								executor.shutdown();
								result = nopolExecution.get(nopolContext.getMaxTimeInMinutes(), TimeUnit.MINUTES);
							} catch (TimeoutException exception) {

								result = nopol.getNopolResult();
								result.setNopolStatus(NopolStatus.TIMEOUT);
								LoggerFactory.getLogger(Main.class).error("Timeout: execution time > " + nopolContext.getMaxTimeInMinutes() + " " + TimeUnit.MINUTES, exception);
							}
							break;
					}
					break;
				case RANKING:
					Ranking ranking = new Ranking(nopolContext);
					System.out.println(ranking.summary());
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			main.showUsage();
		}
		if (result != null) {
			System.out.println(result.getNopolStatus());

			returnCode = (result.getPatches().isEmpty()) ? -1 : 0;
		}

		System.exit(returnCode);
	}

	private void showUsage() {
		System.err.println();
		System.err.println("Usage: java -jar nopol.jar");
		System.err.println("                          " + jsap.getUsage());
		System.err.println();
		System.err.println(jsap.getHelp());
	}


	private boolean parseArguments(String[] args) {
		JSAPResult jsapConfig = jsap.parse(args);
		if (!jsapConfig.success()) {
			System.err.println();
			for (Iterator<?> errs = jsapConfig.getErrorMessageIterator(); errs.hasNext(); ) {
				System.err.println("Error: " + errs.next());
			}
			showUsage();
			return false;
		}
		String[] sources = jsapConfig.getStringArray("source");
		final File[] sourceFiles = new File[sources.length];
		for (int i = 0; i < sources.length; i++) {
			String path = sources[i];
			File sourceFile = FileLibrary.openFrom(path);
			sourceFiles[i] = sourceFile;
		}

		final URL[] classpath = JavaLibrary.classpathFrom(jsapConfig.getString("classpath"));

		this.nopolContext = new NopolContext(sourceFiles, classpath, jsapConfig.getStringArray("test"));

		nopolContext.setType(strToStatementType(jsapConfig.getString("type")));
		nopolContext.setMode(strToMode(jsapConfig.getString("mode")));
		nopolContext.setSynthesis(strToSynthesis(jsapConfig.getString("synthesis")));
		nopolContext.setOracle(strToOracle(jsapConfig.getString("oracle")));
		nopolContext.setSolver(strToSolver(jsapConfig.getString("solver")));
		if (jsapConfig.getString("solverPath") != null) {
			nopolContext.setSolverPath(jsapConfig.getString("solverPath"));
			SolverFactory.setSolver(nopolContext.getSolver(), nopolContext.getSolverPath());
		}
		nopolContext.setComplianceLevel(jsapConfig.getInt("complianceLevel", nopolContext.getComplianceLevel())); 
		nopolContext.setMaxTimeInMinutes(jsapConfig.getInt("maxTime", nopolContext.getMaxTimeInMinutes()));
		nopolContext.setMaxTimeEachTypeOfFixInMinutes(jsapConfig.getLong("maxTimeType",nopolContext.getMaxTimeEachTypeOfFixInMinutes()));

		nopolContext.setLocalizer(strToLocalizer(jsapConfig.getString("faultLocalization")));
		nopolContext.setOutputFolder(jsapConfig.getString("outputFolder"));
		nopolContext.setJson(jsapConfig.getBoolean("outputJson", false));
		return true;
	}

	private static NopolContext.NopolSynthesis strToSynthesis(String str) {
		if (str.equals("smt")) {
			return NopolContext.NopolSynthesis.SMT;
		} else if (str.equals("dynamoth")) {
			return NopolContext.NopolSynthesis.DYNAMOTH;
		}
		throw new RuntimeException("Unknow Nopol oracle " + str);
	}

	private static NopolContext.NopolOracle strToOracle(String str) {
		if (str.equals("angelic")) {
			return NopolContext.NopolOracle.ANGELIC;
		} else if (str.equals("symbolic")) {
			return NopolContext.NopolOracle.SYMBOLIC;
		}
		throw new RuntimeException("Unknow Nopol oracle " + str);
	}

	private static NopolContext.NopolSolver strToSolver(String str) {
		if (str.equals("z3")) {
			return NopolContext.NopolSolver.Z3;
		} else if (str.equals("cvc4")) {
			return NopolContext.NopolSolver.CVC4;
		}
		throw new RuntimeException("Unknow Nopol solver " + str);
	}

	private static NopolContext.NopolMode strToMode(String str) {
		if (str.equals("repair")) {
			return NopolContext.NopolMode.REPAIR;
		} else if (str.equals("ranking")) {
			return NopolContext.NopolMode.RANKING;
		}
		throw new RuntimeException("Unknow Nopol mode " + str);
	}

	private static RepairType strToStatementType(String str) {
		if (str.equals("pre_then_cond")) {
			return RepairType.PRE_THEN_COND;
		} else if (str.equals("loop")) {
			return RepairType.LOOP;
		} else if (str.equals("condition")) {
			return RepairType.CONDITIONAL;
		} else if (str.equals("precondition")) {
			return RepairType.PRECONDITION;
		} else if (str.equals("arithmetic")) {
			return RepairType.INTEGER_LITERAL;
		}
		return RepairType.NONE;
	}

	private static NopolContext.NopolLocalizer strToLocalizer(String str) {
		if (str.equals("gzoltar")) {
			return NopolContext.NopolLocalizer.GZOLTAR;
		} else if (str.equals("dumb")) {
			return NopolContext.NopolLocalizer.DUMB;
		} else
			return NopolContext.NopolLocalizer.COCOSPOON;
	}

	private void initJSAP() throws JSAPException {
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
		typeOpt.setRequired(true);
		typeOpt.setAllowMultipleDeclarations(false);
		typeOpt.setLongFlag("type");
		typeOpt.setShortFlag('e');
		typeOpt.setUsageName("condition|precondition|pre_then_cond|loop|arithmetic");
		typeOpt.setStringParser(JSAP.STRING_PARSER);
		typeOpt.setDefault("condition");
		typeOpt.setHelp("The repair type (example fixing only conditions, or adding precondition). REQUIRED OPTION");
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
		testOpt.setHelp("Define the tests of the project (both failing and passing), fully-qualified, separated with ':' (even if the classpath contains other tests, only those are considered.");
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
		maxTime.setHelp("The maximum time execution in minute for the whole execution of Nopol.(default: 10)");
		jsap.registerParameter(maxTime);

		FlaggedOption maxTimeByTypeInMinutes = new FlaggedOption("maxTimeType");
		maxTimeByTypeInMinutes.setRequired(false);
		maxTimeByTypeInMinutes.setAllowMultipleDeclarations(false);
		maxTimeByTypeInMinutes.setLongFlag("maxTimeType");
		maxTimeByTypeInMinutes.setStringParser(JSAP.INTEGER_PARSER);
		maxTimeByTypeInMinutes.setHelp("The maximum time execution in minute for one type of patch. (default: 5)");
		jsap.registerParameter(maxTimeByTypeInMinutes);

		FlaggedOption faultLocalization = new FlaggedOption("faultLocalization");
		faultLocalization.setRequired(false);
		faultLocalization.setAllowMultipleDeclarations(false);
		faultLocalization.setLongFlag("flocal");
		faultLocalization.setShortFlag('z');
		faultLocalization.setUsageName(" cocospoon|dumb|gzoltar");//TODO ADD PARAMETIZED FAULT LOCALIZER
		faultLocalization.setStringParser(JSAP.STRING_PARSER);
		faultLocalization.setDefault(NopolContext.DEFAULT_FAULT_LOCALIZER.name().toLowerCase());
		faultLocalization.setHelp("Define the fault localizer to be used.");
		jsap.registerParameter(faultLocalization);


		FlaggedOption outputFolder = new FlaggedOption("outputFolder");
		outputFolder.setRequired(false);
		outputFolder.setAllowMultipleDeclarations(false);
		outputFolder.setLongFlag("output");
		outputFolder.setStringParser(JSAP.STRING_PARSER);
		outputFolder.setDefault(".");
		outputFolder.setHelp("Define the location where the patches will be saved.");
		jsap.registerParameter(outputFolder);

		QualifiedSwitch outputJson = new QualifiedSwitch("outputJson");
		outputJson.setRequired(false);
		outputJson.setAllowMultipleDeclarations(false);
		outputJson.setLongFlag("json");
		outputJson.setStringParser(JSAP.STRING_PARSER);
		outputJson.setHelp("Output a json file in the current working directory.");
		jsap.registerParameter(outputJson);
	}
}
