package fr.inria.lille.infinitel.mining;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;

import org.slf4j.Logger;

import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.spoon.SpoonClassLoaderFactory;
import fr.inria.lille.infinitel.InfinitelConfiguration;
import fr.inria.lille.infinitel.instrumenting.CompoundLoopMonitor;
import fr.inria.lille.infinitel.instrumenting.CompoundLoopMonitorBuilder;

public class MonitoringTestExecutorBuilder {

	public static MonitoringTestExecutor buildFor(ProjectReference project, InfinitelConfiguration configuration) {
		CompoundLoopMonitorBuilder monitorBuilder = new CompoundLoopMonitorBuilder(configuration.iterationsThreshold());
		ClassLoader classLoader = loaderWithInstrumentedClasses(project, monitorBuilder);
		CompoundLoopMonitor monitor = monitorBuilder.result();
		MonitoringTestExecutor testExecutor = new MonitoringTestExecutor(classLoader, monitor);
		return testExecutor;
	}
	
	protected static ClassLoader loaderWithInstrumentedClasses(ProjectReference project, CompoundLoopMonitorBuilder monitorBuilder) {
		logDebug(logger, "# Instrumenting project classes");
		SpoonClassLoaderFactory spooner = new SpoonClassLoaderFactory(project.sourceFile(), monitorBuilder);
		ClassLoader loader = spooner.classLoaderProcessing(spooner.modelledClasses(), project.classpath());
		logDebug(logger, "# Classes were instrumented and compiled successfully");
		return loader;
	}
	
	private static Logger logger = newLoggerFor(MonitoringTestExecutorBuilder.class);
}