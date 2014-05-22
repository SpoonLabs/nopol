package fr.inria.lille.infinitel;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import spoon.reflect.declaration.CtElement;
import fr.inria.lille.commons.classes.TestClassesFinder;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.SetLibrary;

/** @name Infinite Loops Repair */

public class Infinitel {

	public static void run(File sourceFolder, Collection<URL> classFolders) {
		Infinitel infiniteLoopFixer = new Infinitel(sourceFolder, classFolders);
		infiniteLoopFixer.repair();
		infiniteLoopFixer.showSummary();
	}
	
	public Infinitel(File sourceFolder, Collection<URL> classFolders) {
		this.sourceFolder = sourceFolder;
		this.classFolders = classFolders;
		Collection<CtElement> statements = (new fr.inria.lille.infinitel.LoopStatementsCollector(sourceFolder())).statements();
		System.out.println(statements);
	}

	public void repair() {
		
	}

	public void showSummary() {
		// TODO Auto-generated method stub
	}
	
	protected Collection<String> testClasses() {
		TestClassesFinder finder = new TestClassesFinder();
		String[] testClassesNames = finder.findIn(CollectionLibrary.toArray(URL.class, classFolders()), false);
		return SetLibrary.newHashSet(testClassesNames);
	}
	
	protected File sourceFolder() {
		return sourceFolder;
	}
	
	protected Collection<URL> classFolders() {
		return classFolders;
	}

	private File sourceFolder;
	private Collection<URL> classFolders = ListLibrary.newArrayList();
}
