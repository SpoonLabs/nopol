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
import java.util.Arrays;

import xxl.java.extensions.library.FileLibrary;
import xxl.java.extensions.library.JavaLibrary;
import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.infinitel.Infinitel;
import fr.inria.lille.repair.nopol.NoPolLauncher;

public class Main {

	public static void main(String[] args) {
    	try {
    		String repairMethod = args[0];
    		File sourceFile = FileLibrary.openFrom(args[1]);
    		URL[] classpath = JavaLibrary.classpathFrom(args[2]);
    		SolverFactory.setSolver(args[3], args[4]);
    		new Main(args, repairMethod, sourceFile, classpath);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		showUsage();
    	}
    }
    
	private Main(String[] args, String repairMethod, File sourceFile, URL[] classpaths) {
		if (repairMethod.equalsIgnoreCase("nopol")) {
			NoPolLauncher.launch(sourceFile, classpaths, false, Arrays.copyOfRange(args, 5, args.length));
		}
		else if (repairMethod.equalsIgnoreCase("infinitel")) {
			Infinitel.run(sourceFile, classpaths);
		} else {
			throw new RuntimeException("Invalid repair method: " + repairMethod);
		}
	}
	
	private static void showUsage() {
		try {
			InputStream usageDeatil = FileLibrary.resource("/usage").openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(usageDeatil));
			String currentLine = "";
			while (currentLine != null) {
				System.out.println(currentLine);
				currentLine = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			throw new RuntimeException("Unexpected: usage detail file not found");
		}
	}
}
