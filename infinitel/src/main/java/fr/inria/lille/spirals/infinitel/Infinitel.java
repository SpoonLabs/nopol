package fr.inria.lille.spirals.infinitel;

import java.io.File;
import java.util.Collection;

/**
 * @title Infinite Loops Repair
 *
 */
public class Infinitel {

	public static void run(File sourceFolder, Collection<String> paths) {
		Infinitel infiniteLoopFixer = new Infinitel(sourceFolder, paths);
		infiniteLoopFixer.repair();
		infiniteLoopFixer.showSummary();
	}
	
	public Infinitel(File sourceFolder, Collection<String> paths) {
		// TODO Auto-generated constructor stub
	}

	public void repair() {
		// TODO Auto-generated method stub
		
	}

	public void showSummary() {
		// TODO Auto-generated method stub
		
	}

	
}
