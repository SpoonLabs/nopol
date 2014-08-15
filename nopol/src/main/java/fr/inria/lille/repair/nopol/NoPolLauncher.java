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

import static java.lang.String.format;

import java.io.File;
import java.net.URL;
import java.util.List;

import fr.inria.lille.repair.Main;
import fr.inria.lille.repair.nopol.patch.Patch;
import fr.inria.lille.repair.nopol.synth.DefaultSynthesizer;
import fr.inria.lille.repair.nopol.synth.SynthesizerFactory;

public class NoPolLauncher {
	
	public static void main(String[] args) {
		String filePath = "misc/nopol-example/src/";
		String binFolder = "misc/nopol-example/bin/";
		String junitJar = "misc/nopol-example/junit-4.11.jar";
		String classpath = binFolder + File.pathSeparatorChar + junitJar;
		String solverName = args[0];
		String solverPath = args[1];
		System.out.println(format("args:\n\t%s\n\t%s\n\t%s\n\t%s", filePath, classpath, solverName, solverPath));
		Main.main(new String[] {"nopol", filePath, classpath, solverName, solverPath});
	}
	
	public static void launch(File sourceFile, URL[] classpath, boolean oneBuild, String[] args) {
		long executionTime = System.currentTimeMillis();
		NoPol.setOneBuild(oneBuild);
		NoPol nopol = new NoPol(sourceFile, classpath);
		List<Patch> patches = null;
		if (args.length > 0) {
			patches = nopol.build(args);
		} else {
			patches = nopol.build();
		}
		executionTime = System.currentTimeMillis() - executionTime;
		displayResult(patches, executionTime);
	}
	
	private static void displayResult(List<Patch> patches, long executionTime){
		System.out.println("----INFORMATION----");
		System.out.println("Nb Statements Analyzed : "+SynthesizerFactory.getNbStatementsAnalysed());
		System.out.println("Nb Statements with Angelic Value Found : "+DefaultSynthesizer.getNbStatementsWithAngelicValue());
		System.out.println("Nopol Execution time : "+ executionTime +"ms");
		
		if ( ! patches.isEmpty() ){
			System.out.println("----PATCH FOUND----");
			for ( Patch patch : patches ){
				System.out.println(patch);
			}
		}
	}
}
