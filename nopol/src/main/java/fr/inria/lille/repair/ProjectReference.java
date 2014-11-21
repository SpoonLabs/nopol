package fr.inria.lille.repair;

import java.io.File;
import java.net.URL;

import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

public class ProjectReference {

	public ProjectReference(String sourceFile, String classpath,
			String[] testClasses) {
		this(FileLibrary.openFrom(sourceFile), JavaLibrary
				.classpathFrom(classpath));
		this.testClasses = testClasses;
	}

	public ProjectReference(String sourceFile, URL[] classpath,
			String[] testClasses) {
		this.sourceFile = FileLibrary.openFrom(sourceFile);
		this.classpath = classpath;
		this.testClasses = testClasses;
	}

	public ProjectReference(File sourceFile, URL[] classpath) {
		this.sourceFile = sourceFile;
		this.classpath = classpath;
		testClasses = new TestClassesFinder().findIn(classpath(), false);
	}

	public ProjectReference(File sourceFile, URL[] classpath,
			String[] testClasses) {
		this.sourceFile = sourceFile;
		this.classpath = classpath;
		this.testClasses = testClasses;
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

	public File testFile() {
		return testFile;
	}

	private File testFile;
	private File sourceFile;
	private URL[] classpath;
	private String[] testClasses;
}
