/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.repair.nopol.synth;

import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.commons.trace.SpecificationTestCasesListener;
import fr.inria.lille.localization.TestResult;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.Processor;
import xxl.java.compiler.DynamicCompilationException;
import xxl.java.junit.CompoundResult;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Favio D. DeMarco
 */
public final class ConstraintModelBuilder implements AngelicValue<Boolean> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean viablePatch;
    private final ClassLoader classLoader;
    private RuntimeValues<Boolean> runtimeValues;
    private SourceLocation sourceLocation;
    private NopolContext nopolContext;

    public ConstraintModelBuilder(RuntimeValues<Boolean> runtimeValues, SourceLocation sourceLocation, Processor<?> processor, SpoonedProject spooner, NopolContext nopolContext) {
        this.sourceLocation = sourceLocation;
        this.nopolContext = nopolContext;
        String qualifiedName = sourceLocation.getRootClassName();
        SpoonedClass fork = spooner.forked(qualifiedName);
        try {
            classLoader = fork.processedAndDumpedToClassLoader(processor);
        } catch (DynamicCompilationException e) {
            logger.error("Unable to compile the change: \n" + fork.getSimpleType());
            throw e;
        }
        this.runtimeValues = runtimeValues;
    }

    @Override
    public Collection<Specification<Boolean>> collectSpecifications(URL[] classpath, String[] testClasses, Collection<TestCase> failures) {
        return null;
    }

    /**
     * @see AngelicValue#collectSpecifications(URL[], List, Collection)
     */
    public Collection<Specification<Boolean>> collectSpecifications(URL[] classpath, List<TestResult> testClasses, Collection<TestCase> failures) {
        SpecificationTestCasesListener<Boolean> listener = new SpecificationTestCasesListener<>(runtimeValues);
        AngelicExecution.enable();
        CompoundResult firstResult = TestSuiteExecution.runTestCases(failures, classLoader, listener, nopolContext);
        AngelicExecution.flip();
        CompoundResult secondResult = TestSuiteExecution.runTestCases(failures, classLoader, listener, nopolContext);
        AngelicExecution.disable();
        if (determineViability(firstResult, secondResult)) {
            /* to collect information for passing tests */
            class PassingListener extends TestCasesListener {
                @Override
                public void testRunStarted(Description description)
                        throws Exception {
                }
            }
            PassingListener passingListener = new PassingListener();
            AngelicExecution.enable();
            TestSuiteExecution.runTestResult(testClasses, classLoader, passingListener, nopolContext);
            Collection<TestCase> testCases = passingListener.successfulTests();
            AngelicExecution.flip();
            passingListener = new PassingListener();
            TestSuiteExecution.runTestResult(testClasses, classLoader, passingListener, nopolContext);
            testCases.removeAll(passingListener.failedTests());
            AngelicExecution.disable();
            ArrayList<TestResult> tmp = new ArrayList<>(testClasses);
            // removes all tests that are not dependent of the condition
            for (int i = 0; i < testClasses.size(); i++) {
                TestResult testResult = testClasses.get(i);
                for (Iterator<TestCase> iterator = testCases.iterator(); iterator.hasNext(); ) {
                    TestCase next = iterator.next();
                    if (next.equals(testResult.getTestCase())) {
                        tmp.remove(testResult);
                    }
                }
            }
            TestSuiteExecution.runTestResult(tmp, classLoader, listener, nopolContext);
        }
        return listener.specifications();
    }

    private boolean determineViability(final Result firstResult, final Result secondResult) {
        Collection<Description> firstFailures = TestSuiteExecution.collectDescription(firstResult.getFailures());
        Collection<Description> secondFailures = TestSuiteExecution.collectDescription(secondResult.getFailures());
        firstFailures.retainAll(secondFailures);
        viablePatch = firstFailures.isEmpty();
        int nbFirstSuccess = firstResult.getRunCount() - firstResult.getFailureCount();
        int nbSecondSuccess = secondResult.getRunCount() - secondResult.getFailureCount();
        if (!viablePatch || (nbFirstSuccess == 0 && nbSecondSuccess == 0)) {
            logger.debug("Failing test(s): {}\n{}", sourceLocation, firstFailures);
            Logger testsOutput = LoggerFactory.getLogger("tests.output");
            testsOutput.debug("First set: \n{}", firstResult.getFailures());
            testsOutput.debug("Second set: \n{}", secondResult.getFailures());
        }
        return viablePatch;
    }

    /**
     * @see AngelicValue#isAViablePatch()
     */
    public boolean isAViablePatch() {
        return viablePatch;
    }
}
