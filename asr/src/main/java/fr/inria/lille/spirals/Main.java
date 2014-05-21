package fr.inria.lille.spirals;

import java.io.File;
import java.util.Collection;

import fr.inria.lille.spirals.commons.collections.ArrayLibrary;
import fr.inria.lille.spirals.commons.io.FileHandler;
import fr.inria.lille.spirals.commons.string.StringLibrary;
import fr.inria.lille.spirals.infinitel.Infinitel;


/**
 * @name Automatic Software Repair
 *
 */
public class Main {
	
    public static void main(String[] args) {
    	try {
    		String repairMethod = args[0];
    		File sourceFolder = FileHandler.directoryFrom(args[1]);
    		Collection<String> classFolder = StringLibrary.split(args[2], StringLibrary.javaPathSeparator());
    		new Main(args, repairMethod, sourceFolder, classFolder);
    	}
    	catch (Exception e) {
    		showUsage();
    		e.printStackTrace();
    	}
    }
	
	private Main(String[] args, String repairMethod, File sourceFolder, Collection<String> classFolder) {
		if (repairMethod.equalsIgnoreCase("nopol")) {
			args = ArrayLibrary.subarray(args, 1, 3);
			fr.inria.lille.nopol.Main.main(args);
		}
		else if (repairMethod.equalsIgnoreCase("infinitel")) {
			Infinitel.run(sourceFolder, classFolder);
		}
	}

	private static void showUsage() {
		System.out.println("java " + Main.class.getName() + "<repair method> <source folder> <classpath>");
	}
}
