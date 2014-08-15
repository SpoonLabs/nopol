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

import fr.inria.lille.commons.utils.library.JavaLibrary;
import fr.inria.lille.nopol.patch.Patch;
import fr.inria.lille.nopol.synth.DefaultSynthesizer;
import fr.inria.lille.nopol.synth.SynthesizerFactory;

public class NopolMain {
	
	private static long startTime = -1;
	private static final int PATCH_FOUND = 0;
	private static final int ERR_NO_ANGELIC_VALUE = 2;
	private static final int ERR_NO_SYNTHESIS = 3;
	
	
	public static void nopolLaunch(File sourceFile, URL[] classpath, boolean oneBuild, String[] args) {
		startTime = System.currentTimeMillis();
		
//		JavaLibrary.extendSystemClasspathWith(classpath);                   /// THIS IS FOR THE DYNAMIC CLASS COMPILER
		
		NoPol.setOneBuild(oneBuild);
		NoPol nopol = new NoPol(sourceFile, classpath);
		if (args.length > 0) {
			nopol.build(args);
		} else {
			nopol.build();
		}
		displayResult();
	}
	
	private static void displayResult(){
		System.out.println("----INFORMATION----");
		System.out.println("Nb Statements Analyzed : "+SynthesizerFactory.getNbStatementsAnalysed());
		System.out.println("Nb Statements with Angelic Value Found : "+DefaultSynthesizer.getNbStatementsWithAngelicValue());
		System.out.println("Nopol Execution time : "+(System.currentTimeMillis()-startTime)+"ms");
		
		if ( !NoPol.getPatchList().isEmpty() ){
			System.out.println("----PATCH FOUND----");
			for ( Patch patch : NoPol.getPatchList() ){
				System.out.println(patch);
			}
			System.exit(PATCH_FOUND);
		}
		
		if ( DefaultSynthesizer.getNbStatementsWithAngelicValue() == 0 ){
			System.exit(ERR_NO_ANGELIC_VALUE);
		}else{
			System.exit(ERR_NO_SYNTHESIS);
		}
	}

}
