package fr.inria.lille.spirals.infinitel;

import java.io.File;
import java.util.Collection;

/**
 * @name Infinite Loops Repair
 *
 */
public class Infinitel {

	public static void run(File sourceFolder, Collection<String> paths) {
		Infinitel infiniteLoopFixer = new Infinitel(sourceFolder, paths);
		infiniteLoopFixer.repair();
		infiniteLoopFixer.showSummary();
	}
	
	public Infinitel(File sourceFolder, Collection<String> classFolder) {
		
	}

	public void repair() {
		// TODO Auto-generated method stub
		
	}

	public void showSummary() {
		// TODO Auto-generated method stub
	}

	private Collection<Class<?>> testClasses;
}
