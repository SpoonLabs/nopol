package fr.inria.lille.commons.io;

import java.io.File;
import java.net.URL;

public class JavaLibrary {

	public static String systemClasspath() {
		return property("java.class.path");
	}
	
	public static String extendClasspathWith(URL[] classpaths) {
		String newClasspath = systemClasspath();
		String pathSepartor = pathSeparator();
		for (URL classpath : classpaths) {
			newClasspath += pathSepartor + classpath.getPath();
		}
		setClasspath(newClasspath);
		return newClasspath;
	}
	
	public static void setClasspath(String newClasspath) {
		setProperty("java.class.path", newClasspath);
	}
	
	public static String pathSeparator() {
		if (javaPathSeparator == null) {
			javaPathSeparator = Character.toString(File.pathSeparatorChar);
		}
		return javaPathSeparator;
	}
	
	public static String lineSeparator() {
		if (javaLineSeparator == null) {
			javaLineSeparator = property("line.separator");
		}
		return javaLineSeparator;
	}
	
	private static String property(String key) {
		return System.getProperty(key);
	}
	
	private static void setProperty(String key, String value) {
		System.setProperty(key, value);
	}
	
	private static String javaLineSeparator;
	private static String javaPathSeparator; 
}
