package fr.inria.lille.commons.io;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.string.StringLibrary;

public class FileHandler {
	
	public static String systemClasspath() {
		return System.getProperty("java.class.path");
	}

	public static String currentAbsolutePath() {
		return openFrom(".").getAbsolutePath();
	}
	
	public static boolean ensurePathIsValid(String path) {
		return openFrom(path).exists();
	}
	
	public static boolean isValidPath(String path) {
		return new File(path).exists();
	}
	
	public static File openFrom(String path) {
		File file = new File(path);
		if (! file.exists()) {
			throw new IllegalArgumentException("File does not exist in: '" + path + "'");
		}
		return file;
	}

	public static boolean isSameFile(File aFile, File otherFile) {
		try {
			return aFile.getCanonicalPath().equals(otherFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static File fileFrom(String path) {
		File file = openFrom(path);
		if (! file.isFile()) {
			throw new IllegalArgumentException("This is not a file: '" + path + "'");
		}
		return file;
	}
	
	public static File directoryFrom(String path) {
		File file = openFrom(path);
		if (! file.isDirectory()) {
			throw new IllegalArgumentException("This is not a directory: '" + path + "'");
		}
		return file;
	}
	
	public static Collection<File> filesMatchingNameIn(String directoryPath, String regexToMatch) {
		File directory = directoryFrom(directoryPath);
		Collection<File> matchingFiles = ListLibrary.newLinkedList();
		for (File file : directory.listFiles()) {
			if (file.getName().matches(regexToMatch)) {
				matchingFiles.add(file);
			}
		}
		return matchingFiles;
	}
	
	public static void deleteFiles(Collection<File> files) {
		for (File file : files) {
			deleteFile(file);
		}
	}
	
	public static void deleteFile(File file) {
		deleteFile(file.getAbsolutePath());
	}
	
	public static void deleteFile(String path) {
		File file = openFrom(path);
		if (file.isFile()) {
			log(String.format("Warning: deleting file '%s'", file.getAbsolutePath()));
			file.delete();
		} else {
			deleteDirectory(path);
		}
	}
	
	public static void deleteDirectory(String path) {
		File directory = directoryFrom(path);
		log(String.format("Warning: deleting directory '%s'", directory.getAbsolutePath()));
		deleteFiles(Arrays.asList(directory.listFiles()));
		directory.delete();
	}

	public static URL urlFrom(String path) {
		URL url;
		try {
			url = openFrom(path).toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Illegal name for '" + path + "' while converting to URL");
		}
		return url;
	}
	
	public static URI uriFrom(String scheme) {
		return URI.create(scheme);
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
	
	public static URL resource(String path) {
		/* How method "AnyClass.getResource(filePath)" works:
		 * Suppose the fully qualified name of class "AnyClass" is "any.package.AnyClass".
		 * 	- If "filePath" starts with '/', then the path is relative to the CLASSPATH. That is "any/../"
		 * 	- Otherwise, the path is relative to the location of "AnyClass". That is "any/package/"
		 */
		URL resource = FileHandler.class.getResource(path);
		if (resource == null) {
			throw new IllegalArgumentException("Cannot find resource: '" + path + "'");
		}
		return resource;
	}

	private static void log(String message) {
		System.err.println(message);
	}
	
}
