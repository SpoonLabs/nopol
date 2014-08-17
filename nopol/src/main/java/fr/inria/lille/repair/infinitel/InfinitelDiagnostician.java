package fr.inria.lille.repair.infinitel;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static xxl.java.library.LoggerLibrary.logDebug;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.various.Bag;
import xxl.java.container.various.MappingBag;
import xxl.java.container.various.Pair;
import xxl.java.container.various.Table;
import xxl.java.junit.TestCasesListener;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;
import xxl.java.library.StringLibrary;
import xxl.java.support.Function;
import xxl.java.support.RangeMapper;
import xxl.java.support.Singleton;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.infinitel.instrumenting.CompoundLoopMonitor;
import fr.inria.lille.repair.infinitel.loop.FixableLoop;
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

	public void diagnose() {
		MonitoringTestExecutor testExecutor = newTestExecutor();
		Collection<String> toBeLogged = MetaList.newLinkedList();
		toBeLogged.addAll(logLoopsInvokedOnlyOnce(testExecutor));
		toBeLogged.add("");
		toBeLogged.addAll(logLoopStatistics(testExecutor));
		logDebug(logger(), toBeLogged);
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
		List<String> lines = MetaList.newLinkedList();
		for (FixableLoop loop : loopsInvokedOnce) {
			lines.add("[" + loop.position() + "]");
		}
		return lines;
	}
	
	private Collection<String> logLoopStatistics(MonitoringTestExecutor testExecutor) {
		testExecutor.execute(project().testClasses());
		List<String> loopStatisticsLog = MetaList.newLinkedList();
		CompoundLoopMonitor monitor = testExecutor.monitor();
		logLoopStatisticsIn(loopStatisticsLog, monitor);
		return loopStatisticsLog;
	}
	
	
	private void logLoopStatisticsIn(List<String> loopStatisticsLog, CompoundLoopMonitor monitor) {
		logStatisticsOf(loopStatisticsLog, monitor.allLoops(), monitor, "[all loops]");
		logStatisticsOf(loopStatisticsLog, monitor.loopsWithBreak(), monitor, "[loops-with-break]");
		logStatisticsOf(loopStatisticsLog, monitor.loopsWithReturn(), monitor, "[loops-with-return]");
		logStatisticsOf(loopStatisticsLog, monitor.loopsWithBreakAndReturn(), monitor, "[loops-with-break-and-return]");
		logStatisticsOf(loopStatisticsLog, monitor.loopsWithoutBodyExit(), monitor, "[loops-without-break-or-return]");
	}
	
	private void logStatisticsOf(Collection<String> loopStatisticsLog, Collection<While> loops, CompoundLoopMonitor monitor, String title) {
		loopStatisticsLog.add(format("%s (%d)", title, loops.size()));
		Table<Integer, String, Object> table = Table.newTable(null);
		Bag<Integer> allExitBag = aggregatedExitBags(loops, monitor);
		Bag<Pair<Integer, Integer>> allExitRangesBag = toMappingBag(allExitBag);
		Bag<Pair<Integer, Integer>> allBreakRangesBag = toMappingBag(aggregatedBreakBags(loops, monitor));
		Bag<Pair<Integer, Integer>> allReturnRangesBag = toMappingBag(aggregatedReturnBags(loops, monitor));
		logStatisticsOfEach(loops, monitor, table, allExitRangesBag);
		logAccumulatedStatistics(loops, monitor, table, allExitBag, allExitRangesBag, allBreakRangesBag, allReturnRangesBag);
		logTable(loopStatisticsLog, table, loops.size());
		loopStatisticsLog.add("");
	}

	private void logStatisticsOfEach(Collection<While> loops, CompoundLoopMonitor monitor, Table<Integer, String, Object> table, Bag<Pair<Integer, Integer>> allExitRanges) {
		int number = 0;
		for (While loop : loops) {
			number += 1;
			addRowFrom(table.rowAddIfAbsent(number), 
					number,
					asList(loop),
					allExitRanges.asSet(),
					toMappingBag(monitor.exitRecordsOf(loop)),
					toMappingBag(monitor.breakRecordsOf(loop)),
					toMappingBag(monitor.returnRecordsOf(loop)),
					monitor.exitRecordsOf(loop));
		}
	}

	private void logAccumulatedStatistics(Collection<While> loops, CompoundLoopMonitor monitor, Table<Integer, String, Object> table, Bag<Integer> allExitBag,
			Bag<Pair<Integer, Integer>> allExitRangesBag, Bag<Pair<Integer, Integer>> allBreakRangesBag, Bag<Pair<Integer, Integer>> allReturnRangesBag) {
		int rowNumber = loops.size() + 1;
		addRowFrom(table.rowAddIfAbsent(rowNumber),
				rowNumber,
				loops,
				allExitRangesBag.asSet(),
				allExitRangesBag,
				allBreakRangesBag,
				allReturnRangesBag,
				allExitBag);
	}
	
	private void addRowFrom(Map<String, Object> map, int row, Collection<While> loops, Collection<Pair<Integer, Integer>> ranges, Bag<Pair<Integer, Integer>> exitRanges,
			Bag<Pair<Integer, Integer>> breakExitRanges, Bag<Pair<Integer, Integer>> returnExitRanges, Bag<Integer> exitRecords) {
		map.put("loop-number", row);
		map.put("breaks", totalNumberOfBreaks(loops));
		map.put("returns", totalNumberOfReturns(loops));
		map.put("invocations", exitRecords.size());
		map.put("conditional-exits", exitRecords.size() - breakExitRanges.size() - returnExitRanges.size());
		map.put("break-exits", breakExitRanges.size());
		map.put("return-exits", returnExitRanges.size());
		map.put("total-iterations", Bag.sum(exitRecords));
		logBag(map, "exit-ranges", exitRanges, ranges);
		logBag(map, "break-exit-ranges", breakExitRanges, ranges);
		logBag(map, "return-exit-ranges", returnExitRanges, ranges);
		map.put("exit-records", logBag((Map) MetaMap.newLinkedHashMap(), "", exitRecords, exitRecords.asSet()));
	}

	private <T extends Comparable<T>> String logBag(Map<String, Object> row, String description, Bag<T> bag, Collection<T> keys) {
		List<T> sortedKeys = MetaList.newArrayList(keys);
		Collections.sort(sortedKeys);
		double bagSize = bag.size();
		for (T key : sortedKeys) {
			double percentage = (bagSize < 1.0) ? 0.0 : bag.repetitionsOf(key) / bagSize;
			row.put(format("%s %s", description, key.toString()), percentage);
		}
		return row.toString();
	}
	
	private void logTable(Collection<String> loopStatisticsLog, Table<Integer, String, Object> table, int numberOfLoops) {
		String columnSeparator = " | ";
		Collection<String> columnNames = table.row(1).keySet();
		loopStatisticsLog.add(StringLibrary.join(columnNames, columnSeparator));
		for (int rowNumber = 1; rowNumber <= numberOfLoops + 1; rowNumber += 1) {
			List<String> rowString = StringLibrary.toStringList(table.row(rowNumber).values());
			loopStatisticsLog.add(StringLibrary.join(rowString, columnSeparator));
		}
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
	
	private int totalNumberOfBreaks(Collection<While> loops) {
		int total = 0;
		for (While loop : loops) {
			total += loop.numberOfBreaks();
		}
		return total;
	}
	
	private int totalNumberOfReturns(Collection<While> loops) {
		int total = 0;
		for (While loop : loops) {
			total += loop.numberOfReturns();
		}
		return total;
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
}