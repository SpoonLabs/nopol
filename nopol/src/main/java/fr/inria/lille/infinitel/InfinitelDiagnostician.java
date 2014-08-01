package fr.inria.lille.infinitel;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
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
		return InfinitelDiagnosticianConfiguration.instance();
	}

	public void diagnose() {
		MonitoringTestExecutor testExecutor = newTestExecutor();
		Collection<String> toBeLogged = ListLibrary.newLinkedList();
		toBeLogged.addAll(logLoopsInvokedOnlyOnce(testExecutor));
		toBeLogged.addAll(logLoopStatistics(testExecutor));
		logDebug(logger, toBeLogged);
	}
	
	protected Collection<String> logLoopsInvokedOnlyOnce(MonitoringTestExecutor testExecutor) {
		List<String> logLoopPositions = logLoopPositions(loopsInvokedOnlyOnce(testExecutor));
		logLoopPositions.add(0, "Loops invoked only once during test suite run:");
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
		return logRecordFrequencies(invocationFrequencies(testExecutor.allLoops(), testExecutor.monitor()));
	}

	private Map<Integer, Integer> invocationFrequencies(Collection<While> loops, CompoundLoopMonitor monitor) {
		Map<Integer, Integer> frequencies = MapLibrary.newHashMap();
		for (While loop : loops) {
			Map<Integer, Integer> recordFrequencies = monitor.recordFrequenciesOf(loop);
			for (Integer record : recordFrequencies.keySet()) {
				int count = MapLibrary.getPutIfAbsent(frequencies, record, 0);
				frequencies.put(record, count + recordFrequencies.get(record));
			}
		}
		return frequencies;
	}
	
	private Collection<String> logRecordFrequencies(Map<Integer, Integer> frequencies) {
		Collection<String> records = ListLibrary.newLinkedList();
		List<Integer> frequencyKeys = ListLibrary.newArrayList(frequencies.keySet());
		Collections.sort(frequencyKeys);
		for (Integer record : frequencyKeys) {
			records.add(record.toString() + ": " + frequencies.get(record).toString());
		}
		return ListLibrary.newLinkedList("Records in loops", records.toString());
	}
}