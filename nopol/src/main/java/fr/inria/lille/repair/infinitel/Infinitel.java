package fr.inria.lille.repair.infinitel;

import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.infinitel.loop.examination.LoopTestResult;
import fr.inria.lille.repair.infinitel.loop.implant.MonitoringTestExecutor;
import fr.inria.lille.repair.infinitel.loop.implant.ProjectMonitorImplanter;
import org.slf4j.Logger;
import xxl.java.support.Singleton;

import java.io.File;
import java.net.URL;

import static xxl.java.library.LoggerLibrary.logError;
import static xxl.java.library.LoggerLibrary.loggerFor;

/**
 * Infinite Loops Repair
 */

public class Infinitel {

    private final Config config;

    public static void run(File[] sourceFile, URL[] classpath) {
        Infinitel infiniteLoopFixer = new Infinitel(new Config(sourceFile, classpath, null));
        try {
            infiniteLoopFixer.repair();
        } catch (Exception e) {
            e.printStackTrace();
            logError(infiniteLoopFixer.logger(), "Repair failed");
        }
    }

    public Infinitel(Config config) {
        this.config = config;
    }

    public String[] projectTestClasses() {
        return config.getProjectTests();
    }

    public void repair() {
        MonitoringTestExecutor testExecutor = newTestExecutor();
        LoopTestResult testResult = newTestResult(testExecutor);
        fixInfiniteLoops(testResult, testExecutor);
    }

    protected MonitoringTestExecutor newTestExecutor() {
        MonitoringTestExecutor executor = ProjectMonitorImplanter.implanted(configuration(), config);
        return executor;
    }

    protected LoopTestResult newTestResult(MonitoringTestExecutor testExecutor) {
        return testExecutor.execute(projectTestClasses());
    }

    protected void fixInfiniteLoops(LoopTestResult testResult, MonitoringTestExecutor testExecutor) {
        InfiniteLoopFixer fixer = new InfiniteLoopFixer(testResult, testExecutor);
        fixer.repair();
    }

    protected InfinitelConfiguration configuration() {
        return Singleton.of(InfinitelConfiguration.class);
    }

    protected Logger logger() {
        return loggerFor(this);
    }
}
