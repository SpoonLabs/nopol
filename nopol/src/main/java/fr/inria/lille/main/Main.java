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
package fr.inria.lille.main;

import java.io.File;
import java.net.URL;

import fr.inria.lille.commons.io.FileHandler;
import fr.inria.lille.commons.string.StringLibrary;
import fr.inria.lille.infinitel.Infinitel;
import fr.inria.lille.nopol.NoPol;
import fr.inria.lille.nopol.synth.DefaultSynthesizer;
import fr.inria.lille.nopol.synth.SMTExecutionResult;
import fr.inria.lille.nopol.synth.SynthesizerFactory;
import fr.inria.lille.nopol.synth.smt.constraint.ConstraintSolver;

public class Main {

	public static void main(String[] args) {
    	try {
    		String repairMethod = args[0];
    		File sourceFile = FileHandler.openFrom(args[1]);
    		URL[] classpath = FileHandler.classpathFrom(args[2]);
    		new Main(repairMethod, sourceFile, classpath);
    	}
    	catch (Exception e) {
    		showUsage();
    		e.printStackTrace();
    	}
    }
    
	private Main(String repairMethod, File sourceFile, URL[] classpath) {
		extendClasspath(classpath);
		if (repairMethod.equalsIgnoreCase("nopol")) {
			executeNopol(sourceFile, classpath);
		}
		else if (repairMethod.equalsIgnoreCase("infinitel")) {
			executeInfinitel(sourceFile, classpath);
		}
	}
	
	private void extendClasspath(URL[] classpaths) {
		String newClasspath = System.getProperty("java.class.path");
		String pathSepartor = StringLibrary.javaPathSeparator();
		for (URL classpath : classpaths) {
			newClasspath += pathSepartor + classpath.getPath();
		}
		System.setProperty("java.class.path", newClasspath);
	}
	
	public void executeNopol(File sourceFile, URL[] urls) {
		long startTime = System.currentTimeMillis();
		System.out.println("Suggested patch: " + new NoPol(sourceFile, urls).build());
		System.out.println("----Information----");
		System.out.println("Nb Statements Analysed : " + SynthesizerFactory.getNbStatementsAnalysed());
		System.out.println("Nb Statements with Angelic Value Found : " + DefaultSynthesizer.getNbStatementsWithAngelicValue());
		System.out.println("Nb Solver Execution : " + ConstraintSolver.getExecResult().size());
		for ( SMTExecutionResult result : ConstraintSolver.getExecResult() ){
			System.out.println(result);
		}
		System.out.println("Total Execution time : " + (System.currentTimeMillis() - startTime) + "ms");
	}
    
    public void executeInfinitel(File sourceFile, URL[] classpath) {
    	Infinitel.run(sourceFile, classpath);
    }
	
	private static void showUsage() {
		StringBuilder message = new StringBuilder();
		String newline = StringLibrary.javaNewline();
		message.append("$ java " + Main.class.getName() + " <repair method> <source path> <classpath>" + newline);
		message.append("<repair metod>  'nopol' or 'infinitel'" + newline);
		message.append("<source path>   path to file/folder containing source code to be fixed" + newline);
		message.append("<classpath>     path(s) to folder(s) with test cases (separated by colon ':')" + newline);
		System.out.println(message);
	}
}
