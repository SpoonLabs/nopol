package fr.inria.lille.infinitel.loop;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.suite.TestCase;

public class FixableLoopSelection {
	
	public static Collection<FixableLoop> selection(MonitoringTestExecutor executor, Collection<SourcePosition> loops, Collection<TestCase> failed, Collection<TestCase> passed) {
		Collection<FixableLoop> fixableLoops = SetLibrary.newHashSet();
		for (SourcePosition loop : loops) {
			Map<TestCase, Integer> failingTestInvocations = invocationsPerTestFor(executor, loop, failed);
			Map<TestCase, Integer> passingTestInvocations = invocationsPerTestFor(executor, loop, passed);
			if (isFixable(loop, failingTestInvocations, passingTestInvocations)) {
				fixableLoops.add(new FixableLoop(loop, MapLibrary.keysWithValue(1, failingTestInvocations), MapLibrary.keysWithValue(1, passingTestInvocations)));
			}
		}
		return fixableLoops;
	}
	
	private static Map<TestCase, Integer> invocationsPerTestFor(MonitoringTestExecutor executor, SourcePosition loop, Collection<TestCase> testCases) {
		logDebug(logger, "Running test cases to count number of invocations in " + loop);
		return executor.invocationsPerTest(loop, testCases);
	}

	private static boolean isFixable(SourcePosition loop, Map<TestCase, Integer> failingTestInvocations, Map<TestCase, Integer> passingTestInvocations) {
		boolean isFixable = atMostOneInvocationEach(failingTestInvocations) && atMostOneInvocationEach(passingTestInvocations);
		if (! isFixable) {
			logDebug(logger, format("Unable to fix infinite loop (%s), it is invoked more than once", loop.toString()));
		}
		return isFixable;
	}
	
	private static boolean atMostOneInvocationEach(Map<TestCase, Integer> invocationsPerTest) {
		return MapLibrary.keysWithValuesIn(allowedInvocations(), invocationsPerTest).size() == invocationsPerTest.size();
	}
	
	private static Collection<Integer> allowedInvocations() {
		if (allowedInvocations == null) {
			allowedInvocations = asList(0, 1);
		}
		return allowedInvocations;
	}
	
	private static Collection<Integer> allowedInvocations;
	private static Logger logger = newLoggerFor(FixableLoopSelection.class); 
}