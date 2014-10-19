package fr.inria.lille.repair.infinitel;

import java.io.File;
import java.net.URL;

public class InfinitelLauncher {

	public static void launch(File sourceFile, URL[] classpath, String[] args) {
		if (args.length > 0) {
			InfinitelConfiguration.setIterationsThreshold(Integer.valueOf(args[0]));
		}
		Infinitel.run(sourceFile, classpath);
	}
	
}
