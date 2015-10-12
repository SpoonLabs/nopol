package fr.inria.lille.repair.infinitel.loop.examination;

import fr.inria.lille.repair.infinitel.loop.While;
import fr.inria.lille.repair.infinitel.loop.implant.CentralLoopMonitor;
import fr.inria.lille.repair.infinitel.loop.implant.LoopStatistics;
import xxl.java.container.various.Table;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;

public class LoopTestListener extends TestCasesListener {

    public LoopTestListener(CentralLoopMonitor monitor) {
        this.monitor = monitor;
        resultTable = Table.newTable(null);
        resultTable().addRows(monitor.allLoops());
    }

    @Override
    protected void processTestStarted(TestCase testCase) {
        monitor().enableAll();
    }

    @Override
    protected void processTestFinished(TestCase testCase) {
        for (While loop : resultTable().rows()) {
            LoopStatistics stats = monitor().statisticsIn(loop);
            resultTable().put(loop, testCase, stats);
        }
        monitor().disableAll();
    }

    public LoopTestResult result() {
        LoopTestResult result = new LoopTestResult(resultTable(), successfulTests(), failedTests());
        return result;
    }

    private Table<While, TestCase, LoopStatistics> resultTable() {
        return resultTable;
    }

    private CentralLoopMonitor monitor() {
        return monitor;
    }

    private CentralLoopMonitor monitor;
    private Table<While, TestCase, LoopStatistics> resultTable;
}
