package fr.inria.lille.commons.string;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.inria.lille.commons.collections.ListLibrary;

public class StringLibrary {

	public static List<String> split(String chainedStrings, String splittingRegex) {
		return ListLibrary.newArrayList(chainedStrings.split(splittingRegex));
	}
	
	public static String join(Collection<String> subStrings, String connector) {
		StringBuilder joined = new StringBuilder();
		if (! subStrings.isEmpty()) {
			Iterator<String> iterator = subStrings.iterator();
			joined.append(iterator.next());
			while (iterator.hasNext()) {
				joined.append(connector + iterator.next());
			}
		}
		return joined.toString();
	}
	
	public static String stripEnd(String string, String suffix) {
		if (string.endsWith(suffix)) {
			return string.substring(0, string.length() - suffix.length());
		}
		return string;
	}
	
	public static String firstAfterSplit(String string, String splittingRegex) {
		List<String> splitted = split(string, splittingRegex);
		if (! splitted.isEmpty()) {
			return splitted.get(0);
		}
		return string;
	}
	
	public static String lastAfterSplit(String string, String splittingRegex) {
		List<String> splitted = split(string, splittingRegex);
		if (! splitted.isEmpty()) {
			return splitted.get(splitted.size() - 1);
		}
		return string;
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

