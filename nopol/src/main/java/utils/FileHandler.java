package utils;

import java.io.File;

public class FileHandler {

	public static boolean isPathValid(String path) {
		return new File(path).exists();
	}

	public static void ensurePathIsValid(String path) {
		if (! isPathValid(path)) {
			throw new RuntimeException("File does not exist in: '" + path + "'");
		}
	}
	
}
