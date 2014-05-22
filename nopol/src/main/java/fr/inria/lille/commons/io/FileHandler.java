package fr.inria.lille.commons.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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

	public static URL urlFrom(String path) {
		URL url;
		try {
			url = openFrom(path).toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Illegal name for '" + path + "' while converting to URL");
		}
		return url;
	}
}
