package fr.inria.lille.repair.infinitel.loop.implant;

import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.commons.trace.SpecificationTestCasesListener;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.infinitel.loop.While;
import fr.inria.lille.repair.infinitel.loop.examination.LoopTestListener;
import fr.inria.lille.repair.infinitel.loop.examination.LoopTestResult;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import xxl.java.junit.NullRunListener;
import xxl.java.junit.TestCase;
import xxl.java.support.Singleton;

import java.util.Collection;

import static xxl.java.junit.TestSuiteExecution.runCasesIn;
import static xxl.java.junit.TestSuiteExecution.runTestCase;

public class MonitoringTestExecutor {

    private final NopolContext nopolContext;

    public MonitoringTestExecutor(ClassLoader classLoader, CentralLoopMonitor monitor, NopolContext nopolContext) {
        this.monitor = monitor;
        this.classLoader = classLoader;
        this.nopolContext = nopolContext;
        monitor().disableAll();
    }

    public Result execute(TestCase testCase, While loop, int threshold, int invocation) {
        return execute(testCase, loop, threshold, invocation, nullRunListener());
    }

    public Result execute(TestCase testCase, Collection<While> loops) {
        return execute(testCase, loops, nullRunListener());
    }

    public Result execute(TestCase testCase, While loop) {
        return execute(testCase, loop, nullRunListener());
    }

    public Collection<Specification<Boolean>> executeCollectingTraces(TestCase testCase, While loop) {
        SpecificationTestCasesListener<Boolean> listener = specificationListener(loop);
        execute(testCase, loop, listener);
        return listener.specifications();
    }

    public Collection<Specification<Boolean>> executeCollectingTraces(TestCase testCase, While loop, int threshold, int invocation) {
        SpecificationTestCasesListener<Boolean> listener = specificationListener(loop);
        execute(testCase, loop, threshold, invocation, listener);
        return listener.specifications();
    }

    public Result execute(TestCase testCase, While loop, int threshold, int invocation, RunListener listener) {
        int oldThreshold = monitor().setThresholdOf(loop, threshold, invocation);
        Result result = execute(testCase, loop, listener);
        monitor().setThresholdOf(loop, oldThreshold, invocation);
        return result;
    }

    public Result execute(TestCase testCase, While loop, RunListener listener) {
        monitor().enable(loop);
        Result result = runTestCase(testCase, classLoader(), listener, nopolContext);
        monitor().disable(loop);
        return result;
    }

    public Result execute(TestCase testCase, Collection<While> loops, RunListener listener) {
        monitor().enable(loops);
        Result result = runTestCase(testCase, classLoader(), listener, nopolContext);
        monitor().disable(loops);
        return result;
    }

    public LoopTestResult execute(String[] testClasses) {
        LoopTestListener listener = new LoopTestListener(monitor());
        runCasesIn(testClasses, classLoader(), listener, nopolContext);
        return listener.result();
    }

    public CentralLoopMonitor monitor() {
        return monitor;
    }

    protected ClassLoader classLoader() {
        return classLoader;
    }

    protected SpecificationTestCasesListener<Boolean> specificationListener(While loop) {
        RuntimeValues<Boolean> runtimeValues = monitor().runtimeValuesOf(loop);
        SpecificationTestCasesListener<Boolean> listener = new SpecificationTestCasesListener<Boolean>(runtimeValues);
        return listener;
    }

    private RunListener nullRunListener() {
        return Singleton.of(NullRunListener.class);
    }

    private ClassLoader classLoader;
    private CentralLoopMonitor monitor;
}
