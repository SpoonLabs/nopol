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
package fr.inria.lille.nopol;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.io.FileHandler;
import fr.inria.lille.commons.string.StringLibrary;
import fr.inria.lille.infinitel.Infinitel;
import fr.inria.lille.nopol.synth.DefaultSynthesizer;
import fr.inria.lille.nopol.synth.SynthesizerFactory;
import fr.inria.lille.nopol.synth.smt.SMTExecutionResult;
import fr.inria.lille.nopol.synth.smt.constraint.ConstraintSolver;

public class Main {

	public static void main(String[] args) {
    	try {
    		String repairMethod = args[0];
    		File sourceFolder = FileHandler.directoryFrom(args[1]);
    		Collection<URL> classpath = collectClassDirectories(args[2]);
    		new Main(args, repairMethod, sourceFolder, classpath);
    	}
    	catch (Exception e) {
    		showUsage();
    		e.printStackTrace();
    	}
    }
    
	private Main(String[] args, String repairMethod, File sourceFolder, Collection<URL> classpath) {
		if (repairMethod.equalsIgnoreCase("nopol")) {
			executeNopol(sourceFolder, CollectionLibrary.toArray(URL.class, classpath));
		}
		else if (repairMethod.equalsIgnoreCase("infinitel")) {
			executeInfinitel(sourceFolder, classpath);
		}
	}

	private static Collection<URL> collectClassDirectories(String classpath) {
		List<String> folderNames = StringLibrary.split(classpath, StringLibrary.javaPathSeparator());
		List<URL> folders = ListLibrary.newArrayList();
		for (String folderName : folderNames) {
			folders.add(FileHandler.urlFrom(folderName));
		}
		return folders;
	}
	
	public void executeNopol(File sourceFolder, URL[] urls) {
		long startTime = System.currentTimeMillis();
		System.out.println("Suggested patch: " + new NoPol(sourceFolder, urls).build());
		System.out.println("----Information----");
		System.out.println("Nb Statements Analysed : " + SynthesizerFactory.getNbStatementsAnalysed());
		System.out.println("Nb Statements with Angelic Value Found : " + DefaultSynthesizer.getNbStatementsWithAngelicValue());
		System.out.println("Nb Solver Execution : " + ConstraintSolver.getExecResult().size());
		for ( SMTExecutionResult result : ConstraintSolver.getExecResult() ){
			System.out.println(result);
		}
		System.out.println("Total Execution time : " + (System.currentTimeMillis() - startTime) + "ms");
	}
    
    public void executeInfinitel(File sourceFolder, Collection<URL> classpath) {
    	Infinitel.run(sourceFolder, classpath);
    }
	
	private static void showUsage() {
		StringBuilder message = new StringBuilder();
		String newline = StringLibrary.javaNewline();
		message.append("$ java " + Main.class.getName() + " <repair method> <source folder> <classpath>" + newline);
		message.append("<repair metod>  'nopol' or 'infinitel'" + newline);
		message.append("<source folder> path to folder containing source code to by fixed" + newline);
		message.append("<classpath>     path(s) to folder(s) with test cases (separated by colon ':')" + newline);
		System.out.println(message);
	}
}
