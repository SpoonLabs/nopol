package fr.inria.lille.repair.nopol.synth.brutpol;

import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.common.patch.ExpressionPatch;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.spoon.ConditionalProcessor;
import fr.inria.lille.repair.nopol.spoon.brutpol.ConditionalInstrumenter;
import fr.inria.lille.repair.nopol.synth.AngelicExecution;
import fr.inria.lille.repair.nopol.synth.DefaultSynthesizer;
import fr.inria.lille.repair.nopol.synth.Synthesizer;
import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.synthesizer.SynthesizerImpl;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.Processor;
import spoon.reflect.code.CtStatement;
import xxl.java.junit.CompoundResult;
import xxl.java.junit.TestSuiteExecution;

import java.io.File;
import java.net.URL;
import java.util.*;

import static fr.inria.lille.repair.common.patch.Patch.NO_PATCH;

/**
 * Created by spirals on 25/03/15.
 */
public class BrutSynthesizer implements Synthesizer {

    private final ConditionalProcessor conditionalProcessor;
    private final StatementType type;
    private final SourceLocation sourceLocation;
    private final SpoonedProject spooner;
    private final File sourceFolder;

    public BrutSynthesizer(File sourceFolder, SourceLocation sourceLocation, StatementType type, ConditionalProcessor processor, SpoonedProject spooner) {
        this.sourceLocation = sourceLocation;
        this.type = type;
        this.conditionalProcessor = processor;
        this.spooner = spooner;
        this.sourceFolder = sourceFolder;
    }

    @Override
    public Patch buildPatch(URL[] classpath, List<TestResult> testClasses, List<TestResult> failures) {
        List<TestResult> failuresExecuted = new ArrayList<>();


        Processor<CtStatement> processor = new ConditionalInstrumenter(conditionalProcessor);
        SpoonedClass fork = spooner.forked(sourceLocation.getContainingClassName());
        ClassLoader classLoader = fork.processedAndDumpedToClassLoader(processor);
        Map<String, Object[]> oracle = new HashMap<>();

        AngelicExecution.enable();
        TestRunListener testCasesListener = new TestRunListener();
        CompoundResult firstResult = TestSuiteExecution.runTestResult(failures, classLoader, testCasesListener);
        Map<String, List<Boolean>> passedTests = testCasesListener.passedTests;
        for (Iterator<String> iterator = passedTests.keySet().iterator(); iterator.hasNext(); ) {
            String next = iterator.next();
            oracle.put(next, passedTests.get(next).toArray());
        }

        AngelicExecution.flip();
        testCasesListener = new TestRunListener();
        CompoundResult secondResult = TestSuiteExecution.runTestResult(failures, classLoader, testCasesListener);
        AngelicExecution.disable();
        passedTests = testCasesListener.passedTests;
        for (Iterator<String> iterator = passedTests.keySet().iterator(); iterator.hasNext(); ) {
            String next = iterator.next();
            oracle.put(next, passedTests.get(next).toArray());
        }
        if (determineViability(firstResult, secondResult)) {
            DefaultSynthesizer.nbStatementsWithAngelicValue++;
            testCasesListener = new TestRunListener();
            AngelicExecution.disable();
            TestSuiteExecution.runTestResult(testClasses, classLoader, testCasesListener);
            passedTests = testCasesListener.passedTests;
            for (Iterator<String> iterator = passedTests.keySet().iterator(); iterator.hasNext(); ) {
                String next = iterator.next();
                oracle.put(next, passedTests.get(next).toArray());
            }
            SynthesizerImpl synthesizer = new SynthesizerImpl(spooner,sourceFolder.getAbsolutePath(),sourceLocation,classpath,oracle, oracle.keySet().toArray(new String[0]));
            Candidates run = synthesizer.run();
            if(run.size() > 0) {
                return new ExpressionPatch(run.get(0), sourceLocation, type);
            }
        }
        return NO_PATCH;
    }

    private boolean determineViability(final Result firstResult, final Result secondResult) {
        Collection<Description> firstFailures = TestSuiteExecution.collectDescription(firstResult.getFailures());
        Collection<Description> secondFailures = TestSuiteExecution.collectDescription(secondResult.getFailures());
        firstFailures.retainAll(secondFailures);
        boolean viablePatch = firstFailures.isEmpty();
        if (!viablePatch) {
            //logger.debug("Failing test(s): {}\n{}", sourceLocation, firstFailures);
            Logger testsOutput = LoggerFactory.getLogger("tests.output");
            testsOutput.debug("First set: \n{}", firstResult.getFailures());
            testsOutput.debug("Second set: \n{}", secondResult.getFailures());
        }
        return viablePatch;
    }

    @Override
    public ConditionalProcessor getConditionalProcessor() {
        return conditionalProcessor;
    }


    private class TestRunListener extends RunListener {
        private Map<String, List<Boolean>> failedTests = new HashMap<>();
        private Map<String, List<Boolean>> passedTests = new HashMap<>();
        @Override
        public void testFailure(Failure failure) throws Exception {
            Description description = failure.getDescription();
            String key = description.getClassName() + "#" + description.getMethodName();
            failedTests.put(key, AngelicExecution.previousValue);
        }

        @Override
        public void testFinished(Description description) throws Exception {
            String key = description.getClassName() + "#" + description.getMethodName();
            if(!failedTests.containsKey(key)) {
                passedTests.put(key, AngelicExecution.previousValue);
            }
            AngelicExecution.previousValue = new ArrayList<>();
        }
    }
}
