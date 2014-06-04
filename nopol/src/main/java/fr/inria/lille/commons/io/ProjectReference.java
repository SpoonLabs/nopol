package fr.inria.lille.commons.io;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import fr.inria.lille.commons.classes.TestClassesFinder;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.SetLibrary;

public class ProjectReference {

	public ProjectReference(File sourceFolder, Collection<URL> classpath) {
		this.sourceFolder = sourceFolder;
		this.classpath = classpath;
	}
	
	public File sourceFolder() {
		return sourceFolder;
	}
	
	public Collection<URL> classpath() {
		return classpath;
	}
	
	public Collection<String> testClasses() {
		if (testClasses == null) {
			TestClassesFinder finder = new TestClassesFinder();
			String[] testClassesNames = finder.findIn(CollectionLibrary.toArray(URL.class, classpath()), false);
			testClasses = SetLibrary.newHashSet(testClassesNames);
		}
		return testClasses;
	}
	
	private File sourceFolder;
	private Collection<URL> classpath;
	private Collection<String> testClasses;
}
