package fr.inria.lille.repair.infinitel;

import fr.inria.lille.repair.ProjectReference;
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

    public static void run(File[] sourceFile, URL[] classpath) {
        Infinitel infiniteLoopFixer = new Infinitel(sourceFile, classpath);
        Config config = new Config();//TODO default config
        try {
            infiniteLoopFixer.repair(config);
        } catch (Exception e) {
            e.printStackTrace();
            logError(infiniteLoopFixer.logger(), "Repair failed");
        }
    }

    public Infinitel(File[] sourceFile, URL[] classpath) {
        this(new ProjectReference(sourceFile, classpath));
    }

    public Infinitel(ProjectReference project) {
        this.project = project;
    }

    public ProjectReference project() {
        return project;
    }

    public URL[] projectClasspath() {
        return project().classpath();
    }

    public String[] projectTestClasses() {
        return project().testClasses();
    }

    public void repair(Config config) {
        MonitoringTestExecutor testExecutor = newTestExecutor(config);
        LoopTestResult testResult = newTestResult(testExecutor, config);
        fixInfiniteLoops(testResult, testExecutor, config);
    }

    protected MonitoringTestExecutor newTestExecutor(Config config) {
        MonitoringTestExecutor executor = ProjectMonitorImplanter.implanted(project(), configuration(), config);
        return executor;
    }

    protected LoopTestResult newTestResult(MonitoringTestExecutor testExecutor, Config config) {
        return testExecutor.execute(projectTestClasses(), config);
    }

    protected void fixInfiniteLoops(LoopTestResult testResult, MonitoringTestExecutor testExecutor, Config config) {
        InfiniteLoopFixer fixer = new InfiniteLoopFixer(testResult, testExecutor, config);
        fixer.repair();
    }

    protected InfinitelConfiguration configuration() {
        return Singleton.of(InfinitelConfiguration.class);
    }

    protected Logger logger() {
        return loggerFor(this);
    }

    private ProjectReference project;
}
