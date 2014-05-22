package fr.inria.lille.commons.string;

import java.io.File;
import java.util.List;

import fr.inria.lille.commons.collections.ListLibrary;

public class StringLibrary {

	public static List<String> split(String chainedStrings, String delimiter) {
		return ListLibrary.newArrayList(chainedStrings.split(delimiter));
	}
	
	public static String javaPathSeparator() {
		return javaPathSeparator;
	}
	
	public static String javaNewline() {
		return javaNewline;
	}
	
	private static String javaNewline = System.getProperty("line.separator");
	private static String javaPathSeparator = Character.toString(File.pathSeparatorChar); 
}

