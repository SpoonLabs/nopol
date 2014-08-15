package fr.inria.lille.repair.infinitel.mining;

import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.infinitel.InfinitelConfiguration;
import fr.inria.lille.repair.infinitel.instrumenting.CompoundLoopMonitor;
import fr.inria.lille.repair.infinitel.instrumenting.CompoundLoopMonitorBuilder;

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