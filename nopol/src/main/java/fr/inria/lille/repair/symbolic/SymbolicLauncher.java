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

public class SymbolicLauncher {
	public static List<Patch> launch(File sourceFile, URL[] classpath,
			String[] args) {

		SymbolicLauncher symbolicFixer = new SymbolicLauncher(sourceFile,
				classpath, args);
		try {
			return symbolicFixer.repair();
		} catch (Exception e) {
			e.printStackTrace();
			logError(symbolicFixer.logger(), "Repair failed");
		}
		return null;
	}

	public static List<Patch> run(ProjectReference project) {
		SymbolicLauncher symbolicFixer = new SymbolicLauncher(project);
		try {
			return symbolicFixer.repair();
		} catch (Exception e) {
			e.printStackTrace();
			logError(symbolicFixer.logger(), "Repair failed");
		}
		return null;
	}

	public SymbolicLauncher(File sourceFile, URL[] classpath, String[] tests) {
		this(new ProjectReference(sourceFile, classpath, tests));
	}

	public SymbolicLauncher(ProjectReference projectReference) {
		this.project = projectReference;
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
		SymbolicFixer symbolicFixer = new SymbolicFixer(this.project);
		return symbolicFixer.repair();
	}

	protected SymbolicConfiguration configuration() {
		return Singleton.of(SymbolicConfiguration.class);
	}

	protected Logger logger() {
		return loggerFor(this);
	}

	private ProjectReference project;
}
