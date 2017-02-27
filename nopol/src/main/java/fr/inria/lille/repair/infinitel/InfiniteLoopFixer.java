package fr.inria.lille.repair.infinitel;

import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.synthesis.ConstraintBasedSynthesis;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.repair.infinitel.loop.While;
import fr.inria.lille.repair.infinitel.loop.examination.LoopTestResult;
import fr.inria.lille.repair.infinitel.loop.implant.MonitoringTestExecutor;
import org.junit.runner.Result;
import org.slf4j.Logger;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.classic.MetaSet;
import xxl.java.junit.TestCase;

import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;
import static xxl.java.library.LoggerLibrary.*;

public class InfiniteLoopFixer {

    public InfiniteLoopFixer(LoopTestResult testResult, MonitoringTestExecutor executor) {
        this.testResult = testResult;
        this.executor = executor;
        synthesis = new ConstraintBasedSynthesis();
    }

    public void repair() {
        Collection<While> infiniteLoops = testResult().infiniteLoops();
        for (While loop : infiniteLoops) {
            logInfiniteLoop(loop);
            fixInfiniteLoop(loop);
        }
    }

    private void logInfiniteLoop(While loop) {
        logDebug(logger(), "Infinite loop:", loop.toString());
        logDebug(logger(), "Instrumented loop:", loop.astLoop().getParent().toString());
    }

    protected CodeGenesis fixInfiniteLoop(While loop) {
        Map<TestCase, Integer> thresholds = infiniteInvocationThresholds(loop, testResult().nonHaltingTestsOf(loop));
        Collection<Specification<Boolean>> specifications = testSpecifications(loop, thresholds);
        return synthesisedFix(specifications);
    }

    protected CodeGenesis synthesisedFix(Collection<Specification<Boolean>> specifications) {
        CodeGenesis fix = synthesis().codesSynthesisedFrom(Boolean.class, specifications);
        return fix;
    }

    protected Map<TestCase, Integer> infiniteInvocationThresholds(While loop, Map<TestCase, Integer> nonHaltingTests) {
        Map<TestCase, Integer> invocationThresholds = MetaMap.newHashMap();
        for (TestCase testCase : nonHaltingTests.keySet()) {
            Integer foundThreshold = firstSuccessfulIteration(loop, testCase, nonHaltingTests.get(testCase));
            invocationThresholds.put(testCase, foundThreshold);
        }
        logCollection(logger(), "Angelic records for hanging tests:", invocationThresholds.entrySet());
        return invocationThresholds;
    }

    protected Integer firstSuccessfulIteration(While loop, TestCase testCase, int invocation) {
        int limit = executor().monitor().threshold();
        for (int newThreshold = 0; newThreshold <= limit; newThreshold += 1) {
            Result result = executor().execute(testCase, loop, newThreshold, invocation);
            if (result.wasSuccessful()) {
                return newThreshold;
            }
        }
        throw new RuntimeException(format("Unable to fix infinite invocation in %s", loop.toString()));
    }

    protected Collection<Specification<Boolean>> testSpecifications(While loop, Map<TestCase, Integer> thresholds) {
        Collection<TestCase> loopTests = testResult().testsOf(loop);
        logCollection(logger(), "Tests of infinite loop:", loopTests);
        Collection<Specification<Boolean>> specifications = MetaSet.newHashSet();
        for (TestCase testCase : loopTests) {
            Collection<Specification<Boolean>> testSpecifications;
            if (thresholds.containsKey(testCase)) {
                testSpecifications = executor().executeCollectingTraces(testCase, loop, thresholds.get(testCase), testResult().infiniteInvocation(loop, testCase));
            } else {
                testSpecifications = executor().executeCollectingTraces(testCase, loop);
            }
            specifications.addAll(testSpecifications);
        }
        return specifications;
    }

    protected LoopTestResult testResult() {
        return testResult;
    }

    protected MonitoringTestExecutor executor() {
        return executor;
    }

    protected ConstraintBasedSynthesis synthesis() {
        return synthesis;
    }

    protected Logger logger() {
        return loggerFor(this);
    }

    private LoopTestResult testResult;
    private MonitoringTestExecutor executor;
    private ConstraintBasedSynthesis synthesis;
}
