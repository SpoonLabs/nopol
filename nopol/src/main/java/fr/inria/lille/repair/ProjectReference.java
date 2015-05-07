package fr.inria.lille.repair;

import java.io.File;
import java.net.URL;

import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

public class ProjectReference {

	public ProjectReference(String sourceFile, String classpath,
			String[] testClasses) {
		this(sourceFile, JavaLibrary
				.classpathFrom(classpath), testClasses);
	}

	public ProjectReference(String sourceFile, URL[] classpath,
			String[] testClasses) {
		this.sourceFiles = new File[1];
		this.sourceFiles[0] = FileLibrary.openFrom(sourceFile);
		this.classpath = classpath;
		this.testClasses = testClasses;
	}

	public ProjectReference(File[] sourceFile, URL[] classpath) {
		this.sourceFiles = sourceFile;
		this.classpath = classpath;
		testClasses = new TestClassesFinder().findIn(classpath(), false);
	}

	public ProjectReference(File[] sourceFile, URL[] classpath,
			String[] testClasses) {
		this.sourceFiles = sourceFile;
		this.classpath = classpath;
		this.testClasses = testClasses;
	}

	public File[] sourceFiles() {
		return sourceFiles;
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
	private File[] sourceFiles;
	private URL[] classpath;
	private String[] testClasses;
}
