package fr.inria.lille.commons.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import fr.inria.lille.commons.string.StringLibrary;

public class FileHandler {

	public static boolean ensurePathIsValid(String path) {
		return openFrom(path).exists();
	}
	
	public static File openFrom(String path) {
		File file = new File(path);
		if (! file.exists()) {
			throw new IllegalArgumentException("File does not exist in: '" + path + "'");
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
	
	public static URL[] classpathFrom(String classpath) {
		List<String> folderNames = StringLibrary.split(classpath, StringLibrary.javaPathSeparator());
		URL[] folders = new URL[folderNames.size()];
		int index = 0;
		for (String folderName : folderNames) {
			folders[index] = FileHandler.urlFrom(folderName);
			index += 1;
		}
		return folders;
	}
	
}
