package fr.inria.lille.repair;

import java.io.File;
import java.net.URL;

import xxl.java.extensions.library.FileLibrary;
import xxl.java.extensions.library.JavaLibrary;
import xxl.java.extensions.support.TestClassesFinder;

public class ProjectReference {

	public ProjectReference(String sourceFile, String classpath, String[] testClasses) {
		this(FileLibrary.openFrom(sourceFile), JavaLibrary.classpathFrom(classpath));
		this.testClasses = testClasses;
	}
	
	public ProjectReference(File sourceFile, URL[] classpath) {
		this.sourceFile = sourceFile;
		this.classpath = classpath;
		testClasses = new TestClassesFinder().findIn(classpath(), false);
	}
	
	public File sourceFile() {
		return sourceFile;
	}
	
	public URL[] classpath() {
		return classpath;
	}
	
	public String[] testClasses() {
		return testClasses;
	}
	
	private File sourceFile;
	private URL[] classpath;
	private String[] testClasses;
}
