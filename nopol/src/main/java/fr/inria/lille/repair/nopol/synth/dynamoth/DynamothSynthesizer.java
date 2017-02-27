package fr.inria.lille.repair.nopol.synth.dynamoth;

import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.localization.TestResult;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.patch.ExpressionPatch;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.nopol.spoon.dynamoth.ConditionalInstrumenter;
import fr.inria.lille.repair.nopol.synth.AngelicExecution;
import fr.inria.lille.repair.nopol.synth.AngelicValue;
import fr.inria.lille.repair.nopol.synth.SMTNopolSynthesizer;
import fr.inria.lille.repair.nopol.synth.Synthesizer;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.synthesis.DynamothCodeGenesis;
import fr.inria.lille.repair.synthesis.DynamothCodeGenesisImpl;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.Processor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import xxl.java.compiler.DynamicCompilationException;
import xxl.java.junit.CompoundResult;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestSuiteExecution;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by spirals on 25/03/15.
 */
public class DynamothSynthesizer<T> implements Synthesizer {

    private final Logger testsOutput = LoggerFactory.getLogger(getClass().getName());
    private final NopolProcessor nopolProcessor;
    private final StatementType type;
    private final SourceLocation sourceLocation;
    private final SpoonedProject spooner;
    private final File[] sourceFolders;
    private final AngelicValue angelicValue;
    private final NopolContext nopolContext;

    public DynamothSynthesizer(AngelicValue angelicValue, File[] sourceFolders, SourceLocation sourceLocation, StatementType type, NopolProcessor processor, SpoonedProject spooner, NopolContext nopolContext) {
        this.sourceLocation = sourceLocation;
        this.nopolContext = nopolContext;
        this.type = type;
        this.nopolProcessor = processor;
        this.spooner = spooner;
        this.sourceFolders = sourceFolders;
        this.angelicValue = angelicValue;
    }

    @Override
    public List<Patch> buildPatch(URL[] classpath, List<TestResult> testClasses, Collection<TestCase> failures, long maxTimeBuildPatch) {
        long startTime = System.currentTimeMillis();
        /*Collection<Specification<T>> collection = angelicValue.collectSpecifications(classpath, testClasses, failures);

        for (Iterator<Specification<T>> iterator = collection.iterator(); iterator.hasNext(); ) {
            Specification<T> next = iterator.next();
            next.inputs();
        }*/
        Processor<CtStatement> processor = new ConditionalInstrumenter(nopolProcessor, type.getType());
        SpoonedClass fork = spooner.forked(sourceLocation.getContainingClassName());
        ClassLoader classLoader;
        try {
            classLoader = fork.processedAndDumpedToClassLoader(processor);
        } catch (DynamicCompilationException e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
        Map<String, Object[]> oracle = new HashMap<>();

        AngelicExecution.enable();
        AngelicExecution.setBooleanValue(false);
        TestRunListener testCasesListener = new TestRunListener();
        CompoundResult firstResult = TestSuiteExecution.runTestCases(failures, classLoader, testCasesListener, nopolContext);
        Map<String, List<T>> passedTests = testCasesListener.passedTests;
        for (Iterator<String> iterator = passedTests.keySet().iterator(); iterator.hasNext(); ) {
            String next = iterator.next();
            oracle.put(next, passedTests.get(next).toArray());
        }
        AngelicExecution.flip();

        testCasesListener = new TestRunListener();
        CompoundResult secondResult = TestSuiteExecution.runTestCases(failures, classLoader, testCasesListener, nopolContext);
        AngelicExecution.disable();
        passedTests = testCasesListener.passedTests;
        for (Iterator<String> iterator = passedTests.keySet().iterator(); iterator.hasNext(); ) {
            String next = iterator.next();
            oracle.put(next, passedTests.get(next).toArray());
        }
        if (determineViability(failures, firstResult, secondResult)) {
            SMTNopolSynthesizer.nbStatementsWithAngelicValue++;
            testCasesListener = new TestRunListener();
            AngelicExecution.disable();
            TestSuiteExecution.runTestResult(testClasses, classLoader, testCasesListener, nopolContext);
            passedTests = testCasesListener.passedTests;
            for (String next : passedTests.keySet()) {
                Object[] values = passedTests.get(next).toArray();
                if (values.length == 0) {
                    continue;
                }
                boolean isSame = true;
                for (int i = 1; i < values.length; i++) {
                    Object value = values[i - 1];
                    Object value1 = values[i];
                    if (!value.equals(value1)) {
                        isSame = false;
                        break;
                    }
                }
                // ignore the test if the result of the test is not dependant of the condition value
                if (isSame) {
                    AngelicExecution.enable();
                    boolean flippedValue = !(Boolean) values[0];
                    AngelicExecution.setBooleanValue(flippedValue);
                    testCasesListener = new TestRunListener();
                    try {
                        Result result = TestSuiteExecution.runTest(next, classLoader, testCasesListener, nopolContext);
                        if (!result.wasSuccessful()) {
                            oracle.put(next, values);
                        } else {
                            testsOutput.debug("Ignore the test {}", next);
                        }
                    } catch (Exception e) {
                        oracle.put(next, values);
                    }
                } else {
                    oracle.put(next, values);
                }
            }
            long remainingTime = TimeUnit.MINUTES.toMillis(maxTimeBuildPatch) - (System.currentTimeMillis() - startTime);

            SourcePosition position = nopolProcessor.getTarget().getPosition();
            this.sourceLocation.setSourceStart(position.getSourceStart());
            this.sourceLocation.setSourceEnd(position.getSourceEnd());

            DynamothCodeGenesis synthesizer = new DynamothCodeGenesisImpl(spooner, sourceFolders, sourceLocation, classpath, oracle, oracle.keySet().toArray(new String[0]), nopolContext);
            Candidates run = synthesizer.run(remainingTime);
            if (run.size() > 0) {
                List<Patch> patches = new ArrayList<>();
                for (Expression expression : run) {
                    patches.add(new ExpressionPatch(expression, sourceLocation, type));
                }
                return patches;
            }
        }
        return Collections.EMPTY_LIST;
    }

    private boolean determineViability(Collection<TestCase> testCases, final CompoundResult firstResult, final CompoundResult secondResult) {
        Set<String> firstPassing = getPassingTests(testCases, firstResult);
        Set<String> secondPassing = getPassingTests(testCases, secondResult);
        firstPassing.addAll(secondPassing);
        return firstPassing.size() == testCases.size();
    }

    private Set<String> getPassingTests(Collection<TestCase> testCases, final CompoundResult results) {
        Set<String> output = new HashSet<>();
        Collection<Description> failures = TestSuiteExecution.collectDescription(results.getFailures());
        testCaseLoop:
        for (TestCase testCase : testCases) {
            for (Description failure : failures) {
                if (testCase.className().equals(failure.getClassName())
                        && failure.getMethodName().equals(testCase.testName())) {
                    continue testCaseLoop;
                }
            }
            output.add(testCase.className() + "#" + testCase.testName());
        }
        return output;
    }

    @Override
    public NopolProcessor getProcessor() {
        return nopolProcessor;
    }


    private class TestRunListener extends RunListener {
        private Map<String, List<T>> failedTests = new HashMap<>();
        private Map<String, List<T>> passedTests = new HashMap<>();

        @Override
        public void testFailure(Failure failure) throws Exception {
            Description description = failure.getDescription();
            String key = description.getClassName() + "#" + description.getMethodName();
            failedTests.put(key, AngelicExecution.previousValue);
        }

        @Override
        public void testFinished(Description description) throws Exception {
            String key = description.getClassName() + "#" + description.getMethodName();
            if (!failedTests.containsKey(key)) {
                passedTests.put(key, AngelicExecution.previousValue);
            }
            AngelicExecution.previousValue = new ArrayList<>();
        }
    }
}
