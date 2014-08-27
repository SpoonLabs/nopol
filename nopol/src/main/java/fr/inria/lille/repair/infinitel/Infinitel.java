package fr.inria.lille.repair.infinitel;

import static xxl.java.library.LoggerLibrary.logError;
import static xxl.java.library.LoggerLibrary.loggerFor;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;

import xxl.java.support.Singleton;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.infinitel.loop.examination.LoopTestResult;
import fr.inria.lille.repair.infinitel.loop.implant.MonitoringTestExecutor;
import fr.inria.lille.repair.infinitel.loop.implant.ProjectMonitorImplanter;

/** Infinite Loops Repair */

public class Infinitel {

	public static void run(File sourceFile, URL[] classpath) {
		Infinitel infiniteLoopFixer = new Infinitel(sourceFile, classpath);
		try {
			infiniteLoopFixer.repair();
		} catch (Exception e) {
			e.printStackTrace();
			logError(infiniteLoopFixer.logger(), "Repair failed");
		}
	}
	
	public Infinitel(File sourceFile, URL[] classpath) {
		this(new ProjectReference(sourceFile, classpath));
	}
	
	public Infinitel(ProjectReference project) {
		this.project = project;
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
	
	public void repair() {
		MonitoringTestExecutor testExecutor = newTestExecutor();
		LoopTestResult testResult = newTestResult(testExecutor);
		fixInfiniteLoops(testResult, testExecutor);
	}

	protected MonitoringTestExecutor newTestExecutor() {
		MonitoringTestExecutor executor = ProjectMonitorImplanter.implanted(project(), configuration());
		return executor;
	}
	
	protected LoopTestResult newTestResult(MonitoringTestExecutor testExecutor) {
		return testExecutor.execute(projectTestClasses());
	}
	
	protected void fixInfiniteLoops(LoopTestResult testResult, MonitoringTestExecutor testExecutor) {
		InfiniteLoopFixer fixer = new InfiniteLoopFixer(testResult, testExecutor);
		fixer.repair();
	}

	protected InfinitelConfiguration configuration() {
		return Singleton.of(InfinitelConfiguration.class);
	}
	
	protected Logger logger() {
		return loggerFor(this);
	}
	
	private ProjectReference project;
}
