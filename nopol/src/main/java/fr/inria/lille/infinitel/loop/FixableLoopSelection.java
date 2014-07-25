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
import fr.inria.lille.commons.collections.Table;
import fr.inria.lille.commons.suite.TestCase;

public class FixableLoopSelection {
	
	public static Collection<FixableLoop> selection(MonitoringTestExecutor executor, Collection<SourcePosition> loops, Collection<TestCase> failed, Collection<TestCase> passed) {
		Collection<FixableLoop> fixableLoops = SetLibrary.newHashSet();
		Table<SourcePosition, TestCase, Integer> failingTestInvocations = invocationsPerTestFor(executor, loops, failed);
		Table<SourcePosition, TestCase, Integer> passingTestInvocations = invocationsPerTestFor(executor, loops, passed);
		for (SourcePosition loop : loops) {
			addIfFixable(loop, fixableLoops, failingTestInvocations.row(loop), passingTestInvocations.row(loop));
		}
		return fixableLoops;
	}
	
	private static Table<SourcePosition, TestCase, Integer> invocationsPerTestFor(MonitoringTestExecutor executor, Collection<SourcePosition> loops, Collection<TestCase> testCases) {
		logDebug(logger, "Running test cases to count number of invocations in " + loops);
		return executor.invocationsPerTest(loops, testCases);
	}
	
	private static void addIfFixable(SourcePosition loop, Collection<FixableLoop> fixableLoops, Map<TestCase, Integer> failing, Map<TestCase, Integer> passing) {
		if (isFixable(loop, failing, passing)) {
			fixableLoops.add(new FixableLoop(loop, MapLibrary.keysWithValue(1, failing), MapLibrary.keysWithValue(1, passing)));
		}
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