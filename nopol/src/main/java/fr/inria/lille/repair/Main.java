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

import java.io.File;
import java.net.URL;
import java.util.Collection;

import fr.inria.lille.commons.collections.ArrayLibrary;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.io.FileHandler;
import fr.inria.lille.commons.io.JavaLibrary;
import fr.inria.lille.commons.string.StringLibrary;
import fr.inria.lille.infinitel.Infinitel;
import fr.inria.lille.nopol.NopolMain;

public class Main {

	public static void main(String[] args) {
    	try {
    		String repairMethod = args[0];
    		File sourceFile = FileHandler.openFrom(args[1]);
    		URL[] classpath = FileHandler.classpathFrom(args[2]);
    		new Main(args, repairMethod, sourceFile, classpath);
    	}
    	catch (Exception e) {
    		showUsage();
    		e.printStackTrace();
    	}
    	System.exit(0);
    }
    
	private Main(String[] args, String repairMethod, File sourceFile, URL[] classpaths) {
		if (repairMethod.equalsIgnoreCase("nopol")) {
			executeNopol(args);
		}
		else if (repairMethod.equalsIgnoreCase("infinitel")) {
			executeInfinitel(sourceFile, classpaths);
		}
	}
	
	private void executeNopol(String[] args) {
		NopolMain.main(ArrayLibrary.subarray(args, 1, args.length));
	}

    private void executeInfinitel(File sourceFile, URL[] classpath) {
    	Infinitel.run(sourceFile, classpath);
    }
	
	private static void showUsage() {
		Collection<String> lines = ListLibrary.newLinkedList();
		lines.add("$ java " + Main.class.getName() + " <repair method> <source path> <class path>");
		lines.add("<repair metod>  'nopol' or 'infinitel'");
		lines.add("<source path>   path to file/folder containing source code to be fixed");
		lines.add("<class path>    path(s) to folder(s) with class files (separated by colon ':')");
		String usage = StringLibrary.join(lines, JavaLibrary.lineSeparator());
		System.out.println(usage);
	}
}
