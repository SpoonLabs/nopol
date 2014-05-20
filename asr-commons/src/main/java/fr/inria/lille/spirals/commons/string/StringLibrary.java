package fr.inria.lille.spirals.commons.string;

import java.io.File;
import java.util.List;

import fr.inria.lille.spirals.commons.collections.ListLibrary;

public class StringLibrary {

	public static List<String> split(String chainedStrings, String delimiter) {
		return ListLibrary.newArrayList(chainedStrings.split(delimiter));
	}
	
	public static String javaPathSeparator() {
		return Character.toString(File.pathSeparatorChar);
	}
}

