package fr.inria.lille.spirals.commons.io;

import java.io.File;

public class FileHandler {

	public static boolean ensurePathIsValid(String path) {
		return openFrom(path).exists();
	}
	
	public static File openFrom(String path) {
		File file = new File(path);
		if (! file.exists()) {
			throw new RuntimeException("File does not exist in: '" + path + "'");
		}
		return file;
	}

	public static File fileFrom(String path) {
		File file = openFrom(path);
		if (! file.isFile()) {
			throw new RuntimeException("This is not a file: '" + path + "'");
		}
		return file;
	}
	
	public static File directoryFrom(String path) {
		File file = openFrom(path);
		if (! file.isDirectory()) {
			throw new RuntimeException("This is not a directory: '" + path + "'");
		}
		return file;
	}

}
