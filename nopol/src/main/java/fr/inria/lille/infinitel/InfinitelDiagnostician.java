package fr.inria.lille.infinitel;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static java.lang.String.format;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.io.FileHandler;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.infinitel.loop.CentralLoopMonitor;
import fr.inria.lille.infinitel.loop.FixableLoop;
import fr.inria.lille.infinitel.loop.MonitoringTestExecutor;

public class InfinitelDiagnostician extends Infinitel {

	public static void main(String[] args) {
		File sourceFile = FileHandler.openFrom(args[0]);
		URL[] classpath = FileHandler.classpathFrom(args[1]);
		new InfinitelDiagnostician(sourceFile, classpath).diagnose();
		System.exit(0);
	}
	
	public InfinitelDiagnostician(File sourceFile, URL[] classpath) {
		super(sourceFile, classpath);
	}
	
	public InfinitelDiagnostician(ProjectReference project) {
		super(project);
	}

	@Override
	protected Number iterationsThreshold() {
		return InfinitelConfiguration.diagnosticsIterationsThreshold();
	}
	
	public void diagnose() {
		MonitoringTestExecutor testExecutor = newTestExecutor();
		Collection<FixableLoop> loopsInvokedOnce = loopsInvokedOnlyOnce(testExecutor);
		logLoopPositions(loopsInvokedOnce);
		logLoopStatistics(testExecutor);
	}
	
	protected Collection<FixableLoop> loopsInvokedOnlyOnce(MonitoringTestExecutor testExecutor) {
		TestCasesListener listener = new TestCasesListener();
		testExecutor.execute(project().testClasses(), listener);
		return fixableLoops(testExecutor, testExecutor.allLoops(), listener);
	}
	
	private void logLoopPositions(Collection<FixableLoop> loopsInvokedOnce) {
		Collection<String> lines = ListLibrary.newArrayList();
		lines.add("Loops invoked only once during tests run:");
		for (FixableLoop loop : loopsInvokedOnce) {
			lines.add("[" + loop.position() + "]");
		}
		logDebug(logger, lines);
	}
	
	private void logLoopStatistics(MonitoringTestExecutor testExecutor) {
		testExecutor.execute(project().testClasses());
		List<Integer> records = ListLibrary.newArrayList();
		CentralLoopMonitor monitor = testExecutor.monitor();
		for (SourcePosition loop : monitor.allLoops()) {
			records.add(monitor.topRecordIn(loop));
		}
		logDebug(logger, format("Top records in %d loops", records.size()), records.toString());
	}
	
}