package fr.inria.lille.infinitel.mining;

import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.infinitel.InfinitelConfiguration;
import fr.inria.lille.infinitel.instrumenting.CompoundLoopMonitor;
import fr.inria.lille.infinitel.instrumenting.CompoundLoopMonitorBuilder;
import fr.inria.lille.repair.ProjectReference;

public class MonitoringTestExecutorBuilder {

	public static MonitoringTestExecutor buildFor(ProjectReference project, InfinitelConfiguration configuration) {
		CompoundLoopMonitorBuilder monitorBuilder = new CompoundLoopMonitorBuilder(configuration.iterationsThreshold());
		SpoonedProject spoonedProject = new SpoonedProject(project.sourceFile(), project.classpath());
		ClassLoader classLoader = spoonedProject.processedAndDumpedToClassLoader(monitorBuilder);
		CompoundLoopMonitor monitor = monitorBuilder.result();
		MonitoringTestExecutor testExecutor = new MonitoringTestExecutor(classLoader, monitor);
		return testExecutor;
	}
	
}