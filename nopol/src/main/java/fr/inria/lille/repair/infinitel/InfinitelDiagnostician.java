package fr.inria.lille.repair.infinitel;

import static java.lang.String.format;
import static xxl.java.library.LoggerLibrary.logDebug;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import xxl.java.container.classic.MetaList;
import xxl.java.container.various.Bag;
import xxl.java.container.various.MappingBag;
import xxl.java.container.various.Pair;
import xxl.java.container.various.Table;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;
import xxl.java.library.StringLibrary;
import xxl.java.support.Function;
import xxl.java.support.RangeMapper;
import xxl.java.support.Singleton;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.infinitel.loop.While;
import fr.inria.lille.repair.infinitel.loop.examination.LoopTestResult;
import fr.inria.lille.repair.infinitel.loop.implant.MonitoringTestExecutor;

public class InfinitelDiagnostician extends Infinitel {

	public static void main(String[] args) {
		File sourceFile = FileLibrary.openFrom(args[0]);
		URL[] classpath = JavaLibrary.classpathFrom(args[1]);
		new InfinitelDiagnostician(sourceFile, classpath).diagnose();
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
		Collection<String> log = MetaList.newLinkedList();
		MonitoringTestExecutor executor = newTestExecutor();
		LoopTestResult testResult = newTestResult(executor);
		Table<While, String, Object> table = dataTable(testResult);
		logTable(log, table);
		logDebug(logger(), log);
	}
	
	private Table<While, String, Object> dataTable(LoopTestResult testResult) {
		Table<While, String, Object> table = Table.newTable(null);
		int label = 1;
		Collection<Pair<Integer, Integer>> exitRanges = asMappingBag(testResult.aggregatedExitRecords()).asSet();
		for (While loop : testResult.loops()) {
			table.put(loop, "label", label++);
			table.put(loop, "breaks", loop.numberOfBreaks());
			table.put(loop, "returns", loop.numberOfReturns());
			table.put(loop, "iterations", testResult.aggregatedNumberOfIterations(loop));
			table.put(loop, "iterations-ratio", testResult.aggregatedIterationsRatio(loop));
			table.put(loop, "invocations", testResult.aggregatedNumberOfRecords(loop));
			table.put(loop, "conditional-exits", testResult.aggregatedNumberOfConditionalExits(loop));
			table.put(loop, "break-exits", testResult.aggregatedNumberOfBreakExits(loop));
			table.put(loop, "return-exits", testResult.aggregatedNumberOfReturnExits(loop));
			table.put(loop, "tests", testResult.numberOfTestsOf(loop));
			table.put(loop, "top-record", testResult.aggregatedTopRecord(loop));
			table.put(loop, "invocations-ratio", testResult.aggregatedInvocationsPerTest(loop));
			putRanges(table, loop, "exit", exitRanges, testResult.aggregatedExitRecordsOf(loop));
			putRanges(table, loop, "break", exitRanges, testResult.aggregatedBreakRecordsOf(loop));
			putRanges(table, loop, "return", exitRanges, testResult.aggregatedReturnRecordsOf(loop));
			table.put(loop, "condition", loop.loopingCondition());
			table.put(loop, "location", loop.position().toString());
		}
		return table;
	}
	
	private void putRanges(Table<While, String, Object> table, While loop, String description, Collection<Pair<Integer, Integer>> ranges, Bag<Integer> records) {
		Bag<Pair<Integer, Integer>> recordsInRanges = asMappingBag(records); 
		List<Pair<Integer, Integer>> sortedKeys = MetaList.newArrayList(ranges);
		Collections.sort(sortedKeys);
		double bagSize = recordsInRanges.size();
		for (Pair<Integer, Integer> range : sortedKeys) {
			double percentage = (bagSize < 1.0) ? 0.0 : recordsInRanges.repetitionsOf(range) / bagSize;
			String columnName = format("%s-%s", description, range.toString()).replace('<', '[').replace('>', ')');
			table.put(loop, columnName, percentage);
		}
	}

	private void logTable(Collection<String> log, Table<While, String, Object> table) {
		String columnSeparator = " | ";
		Collection<String> columnNames = table.columns();
		log.add(StringLibrary.join(columnNames, columnSeparator));
		for (While loop : table.rows()) {
			List<String> rowString = StringLibrary.toStringList(table.row(loop).values());
			log.add(StringLibrary.join(rowString, columnSeparator));
		}
	}
	
	private MappingBag<Integer, Pair<Integer, Integer>> asMappingBag(Bag<Integer> bag) {
		final int lowerLimit = 100;
		final int upperLimit = configuration().iterationsThreshold();
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