package fr.inria.lille.repair.infinitel;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.infinitel.loop.While;
import fr.inria.lille.repair.infinitel.loop.examination.LoopTestResult;
import fr.inria.lille.repair.infinitel.loop.implant.MonitoringTestExecutor;
import xxl.java.container.classic.MetaCollection;
import xxl.java.container.various.Bag;
import xxl.java.container.various.MappingBag;
import xxl.java.container.various.Pair;
import xxl.java.container.various.Table;
import xxl.java.junit.TestCase;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;
import xxl.java.library.StringLibrary;
import xxl.java.support.Function;
import xxl.java.support.RangeMapper;
import xxl.java.support.Singleton;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;
import static xxl.java.library.LoggerLibrary.logDebugLn;

public class InfinitelDiagnostician extends Infinitel {

	/* Expected configuration in logback.xml:
     * --------------------------------------
	 * 
	 * <appender name="DIAGNOSTICIAN" class="ch.qos.logback.core.FileAppender">
	 *	<file>diagnostics.log</file>
	 *	<append>false</append>
	 *	<encoder>
	 *		<pattern>%msg</pattern>
	 *	</encoder>
	 * </appender>
	 *
	 *	<logger name="fr.inria.lille.repair.infinitel.InfinitelDiagnostician" additivity="false">
	 *		<appender-ref ref="DIAGNOSTICIAN" />
	 *	</logger>
	 */

    public static void main(String[] args) {
        File sourceFile = FileLibrary.openFrom(args[0]);
        URL[] classpath = JavaLibrary.classpathFrom(args[1]);
        new InfinitelDiagnostician(new NopolContext(new File[]{sourceFile}, classpath, null)).diagnose();
        System.out.println("Diagnostics ended");
    }

    public InfinitelDiagnostician(NopolContext nopolContext) {
        super(nopolContext);
    }

    @Override
    protected InfinitelConfiguration configuration() {
        return Singleton.of(InfinitelDiagnosticianConfiguration.class);
    }

    private void diagnose() {
        MonitoringTestExecutor executor = newTestExecutor();
        LoopTestResult testResult = newTestResult(executor);
        logTable(loopDataTable(testResult));
        logTable(testDataTable(testResult));
        logAllExitRecords(testResult);
        logRecordComparison(testResult);
        logInvocations(testResult);
    }

    private Table<While, String, Object> loopDataTable(LoopTestResult testResult) {
        Table<While, String, Object> table = Table.newTable(null);
        int label = 1;
        Collection<Pair<Integer, Integer>> sortedRanges = MetaCollection.sorted(asMappingBag(testResult.aggregatedExitRecords()).asSet());
        for (While loop : testResult.loops()) {
            Bag<Integer> exitRecords = testResult.aggregatedExitRecordsOf(loop);
            table.put(loop, "label", label++);
            table.put(loop, "breaks", loop.numberOfBreaks());
            table.put(loop, "returns", loop.numberOfReturns());
            table.put(loop, "unbreakable", loop.isUnbreakable());
            table.put(loop, "iterations", testResult.aggregatedNumberOfIterations(loop));
            table.put(loop, "invocations-0", exitRecords.repetitionsOf(0));
            table.put(loop, "invocations-1", exitRecords.repetitionsOf(1));
            table.put(loop, "invocations", testResult.aggregatedNumberOfRecords(loop));
            table.put(loop, "conditional-exits", testResult.aggregatedNumberOfConditionalExits(loop));
            table.put(loop, "throw-exits", testResult.aggregatedNumberOfErrorExits(loop));
            table.put(loop, "break-exits", testResult.aggregatedNumberOfBreakExits(loop));
            table.put(loop, "return-exits", testResult.aggregatedNumberOfReturnExits(loop));
            table.put(loop, "tests", testResult.numberOfTestsOf(loop));
            table.put(loop, "top-record", testResult.aggregatedTopRecord(loop));
            putRanges(table, loop, "exit", sortedRanges, exitRecords);
            putRanges(table, loop, "error", sortedRanges, testResult.aggregatedErrorRecordsOf(loop));
            putRanges(table, loop, "break", sortedRanges, testResult.aggregatedBreakRecordsOf(loop));
            putRanges(table, loop, "return", sortedRanges, testResult.aggregatedReturnRecordsOf(loop));
            table.put(loop, "condition", loop.loopingCondition());
            table.put(loop, "location", loop.position().toString());
        }
        return table;
    }

    private Table<TestCase, String, Object> testDataTable(LoopTestResult testResult) {
        Table<TestCase, String, Object> table = Table.newTable(null);
        for (TestCase testCase : testResult.testCases()) {
            table.put(testCase, "name", testCase.toString());
            table.put(testCase, "loops", testResult.numberOfLoopsOf(testCase));
            table.put(testCase, "passed", testResult.successfulTests().contains(testCase));
        }
        return table;
    }

    private void putRanges(Table<While, String, Object> table, While loop, String description, Collection<Pair<Integer, Integer>> ranges, Bag<Integer> records) {
        Bag<Pair<Integer, Integer>> recordsInRanges = asMappingBag(records);
        for (Pair<Integer, Integer> range : ranges) {
            String columnName = format("%s-%s", description, range.toString()).replace('<', '[').replace('>', ')');
            table.put(loop, columnName, recordsInRanges.repetitionsOf(range));
        }
    }

    private void logAllExitRecords(LoopTestResult testResult) {
        int label = 1;
        for (While loop : testResult.loops()) {
            logPlainRecords("loop " + label++, testResult.aggregatedExitRecordsOf(loop));
        }
    }

    private void logRecordComparison(LoopTestResult testResult) {
        logPlainRecords("conditional", testResult.aggregatedConditionalRecords());
        logPlainRecords("throw", testResult.aggregatedErrorRecords());
        logPlainRecords("break", testResult.aggregatedBreakRecords());
        logPlainRecords("return", testResult.aggregatedReturnRecords());
    }

    private void logInvocations(LoopTestResult testResult) {
        int label = 1;
        for (While loop : testResult.loops()) {
            logPlainRecords("loop " + label++, testResult.aggregatedNumberOfRecordsOf(loop));
        }
    }

    private void logPlainRecords(String description, Bag<Integer> records) {
        List<Integer> ordered = MetaCollection.sorted(records.asSet());
        for (Integer record : ordered) {
            logLn(description + columnSeparator() + record + columnSeparator() + records.repetitionsOf(record));
        }
    }

    private <R> void logTable(Table<R, ? extends Object, ? extends Object> table) {
        Collection<String> columnNames = StringLibrary.toStringList(table.columns());
        logLn(StringLibrary.join(columnNames, columnSeparator()));
        for (R row : table.rows()) {
            List<String> rowString = StringLibrary.toStringList(table.row(row).values());
            logLn(StringLibrary.join(rowString, columnSeparator()));
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

    private String columnSeparator() {
        return " ¨ ";
    }

    private void logLn(String string) {
        logDebugLn(logger(), string);
    }
}