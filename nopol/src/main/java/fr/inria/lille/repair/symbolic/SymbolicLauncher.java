package fr.inria.lille.repair.symbolic;

import static xxl.java.library.LoggerLibrary.logError;
import static xxl.java.library.LoggerLibrary.loggerFor;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;

import xxl.java.support.Singleton;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.symbolic.patch.Patch;
import fr.inria.lille.repair.symbolic.synth.StatementType;

public class SymbolicLauncher {
	public static List<Patch> launch(File sourceFile, URL[] classpath,
			StatementType type, String[] args) {
		long executionTime = System.currentTimeMillis();
		SymbolicLauncher symbolicFixer = new SymbolicLauncher(sourceFile,
				classpath, type, args);
		try {
			List<Patch> patches = symbolicFixer.repair();
			executionTime = System.currentTimeMillis() - executionTime;
			displayResult(patches, executionTime);
			return patches;
		} catch (Exception e) {
			e.printStackTrace();
			logError(symbolicFixer.logger(), "Repair failed");
		}
		return null;
	}

	public static List<Patch> run(ProjectReference project, StatementType type) {
		SymbolicLauncher symbolicFixer = new SymbolicLauncher(project, type);
		try {
			return symbolicFixer.repair();
		} catch (Exception e) {
			e.printStackTrace();
			logError(symbolicFixer.logger(), "Repair failed");
		}
		return null;
	}

	public SymbolicLauncher(File sourceFile, URL[] classpath, StatementType type, String[] tests) {
		this(new ProjectReference(sourceFile, classpath, tests), type);
	}

	public SymbolicLauncher(ProjectReference projectReference, StatementType type) {
		this.project = projectReference;
		this.type = type;
	}

	public ProjectReference project() {
		return project;
	}

	public URL[] projectClasspath() {
		return project().classpath();
	}

	public String[] projectTestClasses() {
		return project().testClasses();
	}

	public List<Patch> repair() {
		SymbolicFixer symbolicFixer = new SymbolicFixer(this.project, type);
		return symbolicFixer.repair();
	}

	protected SymbolicConfiguration configuration() {
		return Singleton.of(SymbolicConfiguration.class);
	}

	protected Logger logger() {
		return loggerFor(this);
	}

	private static void displayResult(List<Patch> patches, long executionTime) {
		System.out.println("----INFORMATION----");
		System.out.println("Nopol Execution time : " + executionTime + "ms");

		if (!patches.isEmpty()) {
			System.out.println("----PATCH FOUND----");
			for (Patch patch : patches) {
				System.out.println(patch);
			}
		}
	}

	private ProjectReference project;
	private StatementType type;
}
