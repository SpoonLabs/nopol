package fr.inria.lille.infinitel;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static java.lang.String.format;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fr.inria.lille.commons.classes.Singleton;
import fr.inria.lille.commons.collections.Bag;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.io.FileHandler;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.infinitel.instrumenting.CompoundLoopMonitor;
import fr.inria.lille.infinitel.loop.FixableLoop;
import fr.inria.lille.infinitel.loop.While;
import fr.inria.lille.infinitel.mining.MonitoringTestExecutor;

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
	protected InfinitelConfiguration configuration() {
		return Singleton.of(InfinitelDiagnosticianConfiguration.class);
	}

	public void diagnose() {
		MonitoringTestExecutor testExecutor = newTestExecutor();
		Collection<String> toBeLogged = ListLibrary.newLinkedList();
		toBeLogged.addAll(logLoopsInvokedOnlyOnce(testExecutor));
		toBeLogged.add("");
		toBeLogged.addAll(logLoopStatistics(testExecutor));
		logDebug(logger, toBeLogged);
	}

	protected Collection<String> logLoopsInvokedOnlyOnce(MonitoringTestExecutor testExecutor) {
		Collection<FixableLoop> loopsInvokedOnlyOnce = loopsInvokedOnlyOnce(testExecutor);
		List<String> logLoopPositions = logLoopPositions(loopsInvokedOnlyOnce);
		logLoopPositions.add(0, format("loops invoked only once during test suite run (%d)", loopsInvokedOnlyOnce.size()));
		return logLoopPositions;
	}
	
	protected Collection<FixableLoop> loopsInvokedOnlyOnce(MonitoringTestExecutor testExecutor) {
		TestCasesListener listener = new TestCasesListener();
		testExecutor.execute(project().testClasses(), listener);
		return fixableLoops(testExecutor, testExecutor.allLoops(), listener);
	}
	
	private List<String> logLoopPositions(Collection<FixableLoop> loopsInvokedOnce) {
		List<String> lines = ListLibrary.newLinkedList();
		for (FixableLoop loop : loopsInvokedOnce) {
			lines.add("[" + loop.position() + "]");
		}
		return lines;
	}
	
	private Collection<String> logLoopStatistics(MonitoringTestExecutor testExecutor) {
		testExecutor.execute(project().testClasses());
		List<String> loopStatisticsLog = ListLibrary.newLinkedList();
		CompoundLoopMonitor monitor = testExecutor.monitor();
		logIndividualStatistics(loopStatisticsLog, monitor);
		logAggregatedStatistics(loopStatisticsLog, monitor);
		return loopStatisticsLog;
	}

	private void logIndividualStatistics(List<String> loopStatisticsLog, CompoundLoopMonitor monitor) {
		loopStatisticsLog.add("individual loop statistics");
		for (While loop : monitor.allLoops()) {
			loopStatisticsLog.add(loop.toString());
			logIndividualStatisticsFor(loopStatisticsLog, loop, monitor);
			loopStatisticsLog.add("");
		}
	}

	private void logIndividualStatisticsFor(List<String> loopStatisticsLog, While loop, CompoundLoopMonitor monitor) {
		loopStatisticsLog.add(format("%d break statements", loop.numberOfBreaks()));
		loopStatisticsLog.add(format("%d return statements", loop.numberOfReturns()));
		logBag(loopStatisticsLog, "invocations", monitor.exitRecordsOf(loop));
		logBag(loopStatisticsLog, "break exits", monitor.breakRecordsOf(loop));
		logBag(loopStatisticsLog, "return exits", monitor.returnRecordsOf(loop));
	}
	
	private void logAggregatedStatistics(List<String> loopStatisticsLog, CompoundLoopMonitor monitor) {
		loopStatisticsLog.add("aggregated loop statistics");
		logAggregatedStatisticsFor(loopStatisticsLog, "all loops", monitor.allLoops(), monitor);
		logAggregatedStatisticsFor(loopStatisticsLog, "loops with break", monitor.loopsWithBreak(), monitor);
		logAggregatedStatisticsFor(loopStatisticsLog, "loops with return", monitor.loopsWithReturn(), monitor);
		logAggregatedStatisticsFor(loopStatisticsLog, "loops with both", monitor.loopsWithBreakAndReturn(), monitor);
		logAggregatedStatisticsFor(loopStatisticsLog, "loops without any", monitor.loopsWithoutBodyExit(), monitor);
	}
	
	private void logAggregatedStatisticsFor(List<String> loopStatisticsLog, String description, Collection<While> loops, CompoundLoopMonitor monitor) {
		loopStatisticsLog.add(format("%s (%d)", description, loops.size()));
		loopStatisticsLog.add("individual invocations: " + invocations(loops, monitor).toString());
		logBag(loopStatisticsLog, "invocations", aggregatedExitBags(loops, monitor));
		logBag(loopStatisticsLog, "break exits", aggregatedBreakBags(loops, monitor));
		logBag(loopStatisticsLog, "return exits", aggregatedReturnBags(loops, monitor));
		loopStatisticsLog.add("");
	}
	
	private List<Integer> invocations(Collection<While> loops, CompoundLoopMonitor monitor) {
		List<Integer> invocations = ListLibrary.newArrayList();
		for (While loop : loops) {
			invocations.add(monitor.numberOfRecordsIn(loop));
		}
		Collections.sort(invocations);
		return invocations;
	}

	private Bag<Integer> aggregatedExitBags(Collection<While> loops, CompoundLoopMonitor monitor) {
		Bag<Integer> aggregatedExitBag = Bag.newHashBag();
		for (While loop : loops) {
			aggregatedExitBag.addAll(monitor.exitRecordsOf(loop));
		}
		return aggregatedExitBag;
	}
	
	private Bag<Integer> aggregatedBreakBags(Collection<While> loops, CompoundLoopMonitor monitor) {
		Bag<Integer> aggregatedExitBag = Bag.newHashBag();
		for (While loop : loops) {
			aggregatedExitBag.addAll(monitor.breakRecordsOf(loop));
		}
		return aggregatedExitBag;
	}
	
	private Bag<Integer> aggregatedReturnBags(Collection<While> loops, CompoundLoopMonitor monitor) {
		Bag<Integer> aggregatedExitBag = Bag.newHashBag();
		for (While loop : loops) {
			aggregatedExitBag.addAll(monitor.returnRecordsOf(loop));
		}
		return aggregatedExitBag;
	}

	private void logBag(List<String> loopStatisticsLog, String description, Bag<Integer> bag) {
		int size = bag.size();
		loopStatisticsLog.add(size + " " + description);
		if (size > 0) {
			loopStatisticsLog.add(bag.toString());
		}
	}
}