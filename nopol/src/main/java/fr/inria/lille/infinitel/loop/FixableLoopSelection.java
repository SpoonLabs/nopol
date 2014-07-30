package fr.inria.lille.infinitel.loop;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.collections.Table;
import fr.inria.lille.commons.suite.TestCase;

public class FixableLoopSelection {
	
	public static Collection<FixableLoop> selection(MonitoringTestExecutor executor, Collection<SourcePosition> loops, Collection<TestCase> failed, Collection<TestCase> passed) {
		logDebug(logger, "Running test cases to count number of invocations in " + loops);
		Table<SourcePosition, TestCase, Integer> failingTestInvocations = executor.invocationsPerTest(loops, failed);
		Table<SourcePosition, TestCase, Integer> passingTestInvocations = executor.invocationsPerTest(loops, passed);
		Collection<FixableLoop> fixableLoops = SetLibrary.newHashSet();
		for (SourcePosition loop : loops) {
			addIfFixable(loop, fixableLoops, failingTestInvocations.row(loop), passingTestInvocations.row(loop));
		}
		return fixableLoops;
	}
	
	private static void addIfFixable(SourcePosition loop, Collection<FixableLoop> fixableLoops, Map<TestCase, Integer> failing, Map<TestCase, Integer> passing) {
		Collection<TestCase> failingInvoking = invokingTests(failing);
		Collection<TestCase> passingInvoking = invokingTests(passing);
		if (failingInvoking.size() + passingInvoking.size() > 0) {
			fixableLoops.add(new FixableLoop(loop, failingInvoking, passingInvoking));
		} else {
			logDebug(logger, format("Unfixable loop (%s)", loop.toString()));
		}
	}
	
	private static Collection<TestCase> invokingTests(Map<TestCase, Integer> invocations) {
		Collection<TestCase> nonInvoking = MapLibrary.keysWithValue(0, invocations);
		Collection<TestCase> invokingOnce = MapLibrary.keysWithValue(1, invocations);
		if (nonInvoking.size() + invokingOnce.size() == invocations.size()) {
			return invokingOnce;
		}
		return ListLibrary.newArrayList();
	}
	
	private static Logger logger = newLoggerFor(FixableLoopSelection.class); 
}