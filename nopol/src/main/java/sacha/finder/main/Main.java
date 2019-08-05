package sacha.finder.main;

import java.io.File;

public abstract class Main {
	
	protected static String checkFolder(String testFolder) {
	if(testFolder.endsWith("/")||testFolder.endsWith("\\"))
		testFolder=testFolder.substring(0, testFolder.length());
	
	File testSrcFolder = new File(testFolder);
	if(!testSrcFolder.exists() || !testSrcFolder.isDirectory() || !testSrcFolder.canRead())
		throw new IllegalArgumentException("cannot found "+testFolder+" or is not a directory or is not readable");
	
	return testFolder;
	
	}

}
