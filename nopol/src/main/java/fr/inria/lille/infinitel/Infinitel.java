package fr.inria.lille.infinitel;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.lille.commons.io.ProjectReference;

/** Infinite Loops Repair */

public class Infinitel {

	public static void run(File sourceFile, URL[] classFolders) {
		Infinitel infiniteLoopFixer = new Infinitel(sourceFile, classFolders);
		infiniteLoopFixer.repair();
		infiniteLoopFixer.showSummary();
	}
	
	public Infinitel(File sourceFile, URL[] classpath) {
		project = new ProjectReference(sourceFile, classpath);
	}
	
	public void repair() {
		log("Starting repair process");
	}

	public void showSummary() {
		log("<end>");
	}
	
	public ProjectReference project() {
		return project;
	}
	
	protected static void log(String message) {
		logger.debug(message);
	}

	private ProjectReference project;
	private static Logger logger = LoggerFactory.getLogger(Infinitel.class);
}
