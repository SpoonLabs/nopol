package fr.inria.lille.repair.infinitel;

import static java.lang.String.format;
import static xxl.java.library.LoggerLibrary.logDebug;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.runner.Result;

import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.various.Bag;
import xxl.java.container.various.MappingBag;
import xxl.java.container.various.Pair;
import xxl.java.container.various.Table;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;
import xxl.java.library.StringLibrary;
import xxl.java.support.Function;
import xxl.java.support.RangeMapper;
import xxl.java.support.Singleton;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.infinitel.instrumenting.CompoundLoopMonitor;
import fr.inria.lille.repair.infinitel.loop.While;
import fr.inria.lille.repair.infinitel.mining.MonitoringTestExecutor;

public class InfinitelDiagnostician extends Infinitel {

	public static void main(String[] args) {
		File sourceFile = FileLibrary.openFrom(args[0]);
		URL[] classpath = JavaLibrary.classpathFrom(args[1]);
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

	private void diagnose() {
		MonitoringTestExecutor executor = newTestExecutor();
		Collection<String> log = MetaList.newLinkedList();
		List<While> loops = MetaList.newArrayList(executor.allLoops());
		Table<While, String, Object> table = Table.newTable(null);
		TestCasesListener listener = new TestCasesListener();
		Result result = executor.execute(project().testClasses(), listener);
		logTestResult(log, result);
		logIterationStatistics(log, table, loops, executor);
		logTestStatistics(log, table, loops, executor, result, listener.allTests());
		logTable(log, table, loops.size());
		logDebug(logger(), log);
	}
	
	private void logTestResult(Collection<String> log, Result result) {
		log.add("Tests run: " + result.getRunCount());
		log.add("Skipped: " + result.getIgnoreCount());
		log.add("Failures: " + result.getFailureCount());
	}

	private void logIterationStatistics(Collection<String> log, Table<While, String, Object> table, List<While> loops, MonitoringTestExecutor executor) {
		CompoundLoopMonitor monitor = executor.monitor();
		log.add(format("[%s (%d)]", "all loops", loops.size()));
		Bag<Integer> allExitBag = aggregatedExitBags(loops, monitor);
		Collection<Pair<Integer, Integer>> exitRanges = toMappingBag(allExitBag).asSet();
		logStatisticsOfEach(loops, monitor, table, exitRanges);
	}
	
	private void logStatisticsOfEach(Collection<While> loops, CompoundLoopMonitor monitor, Table<While, String, Object> table, Collection<Pair<Integer, Integer>> ranges) {
		int number = 0;
		for (While loop : loops) {
			number += 1;
			Map<String, Object> row = rowFrom(number,
											  loop,
											  ranges, 
											  toMappingBag(monitor.exitRecordsOf(loop)), 
											  toMappingBag(monitor.breakRecordsOf(loop)), 
											  toMappingBag(monitor.returnRecordsOf(loop)), 
											  monitor.exitRecordsOf(loop));
			table.putRow(loop, row);
		}
	}
	
	private Map<String, Object> rowFrom(int row, While loop, Collection<Pair<Integer, Integer>> ranges, Bag<Pair<Integer, Integer>> exitRanges,
			Bag<Pair<Integer, Integer>> breakExitRanges, Bag<Pair<Integer, Integer>> returnExitRanges, Bag<Integer> exitRecords) {
		Map<String, Object> map = MetaMap.newLinkedHashMap();
		int invocations = exitRecords.size();
		long totalIterations = Bag.sum(exitRecords);
		int breakExits = breakExitRanges.size();
		int returnExtis = returnExitRanges.size();
		double iterationsPerInvocation = (invocations == 0) ? 0.0 : ((double) totalIterations) / invocations; 
		map.put("label", row);
		map.put("breaks", loop.numberOfBreaks());
		map.put("returns", loop.numberOfReturns());
		map.put("invocations", invocations);
		map.put("total-iterations", totalIterations);
		map.put("iterations-per-invocation", iterationsPerInvocation);
		map.put("conditional-exits", invocations - breakExits - returnExtis);
		map.put("break-exits", breakExits);
		map.put("return-exits", returnExtis);
		logBag(map, "exit-range", exitRanges, ranges);
		logBag(map, "break-exit-range", breakExitRanges, ranges);
		logBag(map, "return-exit-range", returnExitRanges, ranges);
		map.put("location", loop.astLoop().getPosition());
		return map;
	}
	
	private void logTestStatistics(Collection<String> log, Table<While, String, Object> table, Collection<While> loops, MonitoringTestExecutor executor,
			Result result, Collection<TestCase> tests) {
		Table<While, TestCase, Integer> invocations = executor.invocationsPerTest(loops, tests);
		for (While loop : loops) {
			Map<TestCase, Integer> row = invocations.row(loop);
			int testsCalling = 0;
			int maxTimesInvoked = 0;
			int totalInvocations = 0;
			for (TestCase test : row.keySet()) {
				int testInvocations = row.get(test);
				testsCalling += (testInvocations > 0) ? 1 : 0;
				maxTimesInvoked = (testInvocations > maxTimesInvoked) ? testInvocations : maxTimesInvoked;
				totalInvocations += testInvocations;
			}
			double invocationsPerTest = (testsCalling > 0) ? ((double) totalInvocations) / testsCalling : 0.0;
			table.put(loop, "tests-calling", testsCalling);
			table.put(loop, "max-times-invoked", maxTimesInvoked);
			table.put(loop, "invocations-per-test", invocationsPerTest);
		}
	}

	private Bag<Integer> aggregatedExitBags(Collection<While> loops, CompoundLoopMonitor monitor) {
		Bag<Integer> aggregatedExitBag = Bag.newHashBag();
		for (While loop : loops) {
			aggregatedExitBag.addAll(monitor.exitRecordsOf(loop));
		}
		return aggregatedExitBag;
	}
	
	private MappingBag<Integer, Pair<Integer, Integer>> toMappingBag(Bag<Integer> bag) {
		final int lowerLimit = 100;
		final int upperLimit = configuration().diagnosticsIterationsThreshold();
		final RangeMapper mapper = new RangeMapper(10, 5);
		Function<Integer, Pair<Integer, Integer>> function = new Function<Integer, Pair<Integer, Integer>>() {
			@Override
			public Pair<Integer, Integer> outputFor(Integer value) {
				Pair<Integer, Integer> output = mapper.outputFor(value);
				if (output.first() >= lowerLimit) {
					return Pair.from(lowerLimit, upperLimit);
				}
				return output;
			}
		};
		return MappingBag.newMappingBag(function, bag);
	}
	
	private <T extends Comparable<T>> String logBag(Map<String, Object> row, String description, Bag<T> bag, Collection<T> keys) {
		List<T> sortedKeys = MetaList.newArrayList(keys);
		Collections.sort(sortedKeys);
		double bagSize = bag.size();
		for (T key : sortedKeys) {
			double percentage = (bagSize < 1.0) ? 0.0 : bag.repetitionsOf(key) / bagSize;
			row.put(format("%s %s", description, key.toString()).replace('<', '[').replace('>', ')'), percentage);
		}
		return row.toString();
	}
	
	private void logTable(Collection<String> log, Table<While, String, Object> table, int numberOfLoops) {
		String columnSeparator = " | ";
		Collection<String> columnNames = table.columns();
		log.add(StringLibrary.join(columnNames, columnSeparator));
		for (While loop : table.rows()) {
			List<String> rowString = StringLibrary.toStringList(table.row(loop).values());
			log.add(StringLibrary.join(rowString, columnSeparator));
		}
	}
}