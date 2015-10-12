package fr.inria.lille.repair.infinitel.loop.examination;

import fr.inria.lille.repair.infinitel.loop.While;
import fr.inria.lille.repair.infinitel.loop.implant.LoopStatistics;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.various.Bag;
import xxl.java.container.various.Table;
import xxl.java.junit.TestCase;
import xxl.java.support.Function;

import java.util.Collection;
import java.util.Map;

import static fr.inria.lille.repair.infinitel.loop.implant.LoopStatistics.meanOf;
import static java.lang.String.format;
import static xxl.java.container.classic.MetaCollection.maximum;
import static xxl.java.library.NumberLibrary.sumInts;
import static xxl.java.library.NumberLibrary.sumLongs;

public class LoopTestResult {

    public LoopTestResult(Table<While, TestCase, LoopStatistics> resultTable, Collection<TestCase> successfulTests, Collection<TestCase> failedTests) {
        this.resultTable = resultTable;
        this.failedTests = failedTests;
        this.successfulTests = successfulTests;
    }

    public int numberOfLoops() {
        return loops().size();
    }

    public int numberOfTestCases() {
        return testCases().size();
    }

    public int numberOfFailedTests() {
        return failedTests().size();
    }

    public int numberOfSuccessfulTests() {
        return successfulTests().size();
    }

    public int numberOfBreaks(While loop) {
        return loop.numberOfBreaks();
    }

    public int numberOfReturns(While loop) {
        return loop.numberOfReturns();
    }

    public int numberOfTestsOf(While loop) {
        return testsOf(loop).size();
    }

    public int numberOfFailedTestsOf(While loop) {
        return failedTestsOf(loop).size();
    }

    public int numberOfSuccessfulTestsOf(While loop) {
        return successfulTestsOf(loop).size();
    }

    public int topRecordIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).topRecord();
    }

    public int numberOfRecordsIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).numberOfRecords();
    }

    public int numberOfConditionalExitsIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).numberOfConditionalExits();
    }

    public int numberOfNonConditionalExitsIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).numberOfNonConditionalExits();
    }

    public int numberOfBreakExitsIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).numberOfBreakExits();
    }

    public int numberOfReturnExitsIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).numberOfReturnExits();
    }

    public long numberOfIterationsIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).numberOfIterations();
    }

    public double iterationsPerInvocationIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).iterationsRatio();
    }

    public double iterationMedianIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).iterationMedian();
    }

    public int aggregatedTopRecord(While loop) {
        return maximum(topRecordsByTestIn(loop).values(), 0);
    }

    public int aggregatedNumberOfRecords(While loop) {
        return sumInts(numberOfRecordsByTestIn(loop).values());
    }

    public int aggregatedNumberOfConditionalExits(While loop) {
        return sumInts(numberOfConditionalExitsByTestIn(loop).values());
    }

    public int aggregatedNumberOfNonConditionalExits(While loop) {
        return sumInts(numberOfNonConditionalExitsByTestIn(loop).values());
    }

    public Object aggregatedNumberOfErrorExits(While loop) {
        return sumInts(numberOfErrorExitsByTestIn(loop).values());
    }

    public int aggregatedNumberOfBreakExits(While loop) {
        return sumInts(numberOfBreakExitsByTestIn(loop).values());
    }

    public int aggregatedNumberOfReturnExits(While loop) {
        return sumInts(numberOfReturnExitsByTestIn(loop).values());
    }

    public double aggregatedIterationMedian(While loop) {
        return meanOf(aggregatedExitRecordsOf(loop));
    }

    public long aggregatedNumberOfIterations(While loop) {
        return sumLongs(numberOfIterationsByTestIn(loop).values());
    }

    public Bag<Integer> aggregatedExitRecordsOf(While loop) {
        return Bag.flatBag(exitRecordsByTestIn(loop).values());
    }

    public Bag<Integer> aggregatedConditionalRecordsOf(While loop) {
        return Bag.flatBag(conditionalRecordsByTestIn(loop).values());
    }

    public Bag<Integer> aggregatedErrorRecordsOf(While loop) {
        return Bag.flatBag(errorRecordsByTestIn(loop).values());
    }

    public Bag<Integer> aggregatedBreakRecordsOf(While loop) {
        return Bag.flatBag(breakRecordsByTestIn(loop).values());
    }

    public Bag<Integer> aggregatedReturnRecordsOf(While loop) {
        return Bag.flatBag(returnRecordsByTestIn(loop).values());
    }

    public Bag<Integer> aggregatedNumberOfRecordsOf(While loop) {
        return Bag.newHashBag(numberOfRecordsByTestIn(loop).values());
    }

    public int numberOfLoopsOf(TestCase testCase) {
        return loopsOf(testCase).size();
    }

    public Bag<Integer> aggregatedExitRecords() {
        Bag<Integer> exitRecords = Bag.newHashBag();
        for (While loop : loops()) {
            exitRecords.addAll(aggregatedExitRecordsOf(loop));
        }
        return exitRecords;
    }

    public Bag<Integer> aggregatedConditionalRecords() {
        Bag<Integer> exitRecords = Bag.newHashBag();
        for (While loop : loops()) {
            exitRecords.addAll(aggregatedConditionalRecordsOf(loop));
        }
        return exitRecords;
    }

    public Bag<Integer> aggregatedErrorRecords() {
        Bag<Integer> exitRecords = Bag.newHashBag();
        for (While loop : loops()) {
            exitRecords.addAll(aggregatedErrorRecordsOf(loop));
        }
        return exitRecords;
    }

    public Bag<Integer> aggregatedBreakRecords() {
        Bag<Integer> exitRecords = Bag.newHashBag();
        for (While loop : loops()) {
            exitRecords.addAll(aggregatedBreakRecordsOf(loop));
        }
        return exitRecords;
    }

    public Bag<Integer> aggregatedReturnRecords() {
        Bag<Integer> exitRecords = Bag.newHashBag();
        for (While loop : loops()) {
            exitRecords.addAll(aggregatedReturnRecordsOf(loop));
        }
        return exitRecords;
    }

    public Integer infiniteInvocation(While loop, TestCase testCase) {
        return statistics(loop, testCase).infiniteInvocation();
    }

    public boolean isInfinite(While loop) {
        for (TestCase testCase : testCases()) {
            if (notHalting(loop, testCase)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUsing(While loop, TestCase testCase) {
        return numberOfRecordsIn(loop, testCase) > 0;
    }

    public boolean halts(TestCase testCase) {
        Collection<While> candidates = loopsUsing(testCase, infiniteLoops());
        for (While loop : candidates) {
            if (notHalting(loop, testCase)) {
                return false;
            }
        }
        return true;
    }

    public Collection<While> loops() {
        return resultTable().rows();
    }

    public Collection<While> infiniteLoops() {
        Collection<While> infiniteLoops = MetaList.newLinkedList();
        for (While loop : loops()) {
            if (isInfinite(loop)) {
                infiniteLoops.add(loop);
            }
        }
        return infiniteLoops;
    }

    public Collection<TestCase> testCases() {
        return resultTable().columns();
    }

    public Collection<TestCase> failedTests() {
        return failedTests;
    }

    public Collection<TestCase> successfulTests() {
        return successfulTests;
    }

    public Collection<TestCase> testsOf(While loop) {
        return testsUsing(loop, testCases());
    }

    public Collection<While> loopsOf(TestCase testCase) {
        return loopsUsing(testCase, loops());
    }

    public Collection<TestCase> successfulTestsOf(While loop) {
        return testsUsing(loop, successfulTests());
    }

    public Collection<TestCase> failedTestsOf(While loop) {
        return testsUsing(loop, failedTests());
    }

    public Map<TestCase, Integer> nonHaltingTestsOf(While loop) {
        Map<TestCase, Integer> nonHaltingTests = MetaMap.newHashMap();
        for (TestCase testCase : testsOf(loop)) {
            if (notHalting(loop, testCase)) {
                nonHaltingTests.put(testCase, infiniteInvocation(loop, testCase));
            }
        }
        return nonHaltingTests;
    }

    public Bag<Integer> exitRecordsIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).exitRecords();
    }

    public Bag<Integer> breakRecordsIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).breakRecords();
    }

    public Bag<Integer> returnRecordsIn(While loop, TestCase testCase) {
        return statistics(loop, testCase).returnRecords();
    }

    public Map<TestCase, Long> numberOfIterationsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodNumberOfIterations());
    }

    public Map<TestCase, Integer> topRecordsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodTopRecord());
    }

    public Map<TestCase, Integer> numberOfRecordsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodNumberOfRecords());
    }

    public Map<TestCase, Integer> numberOfConditionalExitsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodNumberOfConditionalExits());
    }

    public Map<TestCase, Integer> numberOfNonConditionalExitsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodNumberOfNonConditionalExits());
    }

    private Map<TestCase, Integer> numberOfErrorExitsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodNumberOfErrorExits());
    }

    public Map<TestCase, Integer> numberOfBreakExitsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodNumberOfBreakExits());
    }

    public Map<TestCase, Integer> numberOfReturnExitsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodNumberOfReturnExits());
    }

    public Map<TestCase, Bag<Integer>> exitRecordsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodExitRecords());
    }

    public Map<TestCase, Bag<Integer>> conditionalRecordsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodConditionalRecords());
    }

    public Map<TestCase, Bag<Integer>> errorRecordsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodErrorRecords());
    }

    public Map<TestCase, Bag<Integer>> breakRecordsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodBreakRecords());
    }

    public Map<TestCase, Bag<Integer>> returnRecordsByTestIn(While loop) {
        return byTest(loop, testsOf(loop), LoopStatistics.methodReturnRecords());
    }

    private <T> Map<TestCase, T> byTest(While loop, Collection<TestCase> tests, Function<LoopStatistics, T> method) {
        Map<TestCase, T> byTest = MetaMap.newHashMap();
        for (TestCase testCase : tests) {
            LoopStatistics statistics = statistics(loop, testCase);
            byTest.put(testCase, method.outputFor(statistics));
        }
        return byTest;
    }

    @SuppressWarnings("unused")
    private <T> Map<TestCase, T> byLoop(TestCase testCase, Collection<While> loops, Function<LoopStatistics, T> method) {
        Map<TestCase, T> byLoop = MetaMap.newHashMap();
        for (While loop : loops) {
            LoopStatistics statistics = statistics(loop, testCase);
            byLoop.put(testCase, method.outputFor(statistics));
        }
        return byLoop;
    }

    private boolean notHalting(While loop, TestCase testCase) {
        return statistics(loop, testCase).hasInfiniteInvocation();
    }

    private Collection<TestCase> testsUsing(While loop, Collection<TestCase> testCases) {
        Collection<TestCase> testsUsing = MetaList.newLinkedList();
        for (TestCase testCase : testCases) {
            if (isUsing(loop, testCase)) {
                testsUsing.add(testCase);
            }
        }
        return testsUsing;
    }

    private Collection<While> loopsUsing(TestCase testCase, Collection<While> loops) {
        Collection<While> loopsUsing = MetaList.newLinkedList();
        for (While loop : loops) {
            if (isUsing(loop, testCase)) {
                loopsUsing.add(loop);
            }
        }
        return loopsUsing;
    }

    private LoopStatistics statistics(While loop, TestCase testCase) {
        return resultTable().cell(loop, testCase);
    }

    private Table<While, TestCase, LoopStatistics> resultTable() {
        return resultTable;
    }

    @Override
    public String toString() {
        return format("LoopTestResult[%d loops][%d test cases]", numberOfLoops(), numberOfTestCases());
    }

    private Collection<TestCase> failedTests;
    private Collection<TestCase> successfulTests;
    private Table<While, TestCase, LoopStatistics> resultTable;
}
