package fr.inria.lille.repair.infinitel.loop;

import static java.lang.String.format;
import static xxl.java.library.LoggerLibrary.logDebug;
import static xxl.java.library.LoggerLibrary.newLoggerFor;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.classic.MetaSet;
import xxl.java.container.various.Table;
import xxl.java.junit.TestCase;
import fr.inria.lille.repair.infinitel.mining.MonitoringTestExecutor;

public class FixableLoopBuilder {
	
	public static Collection<FixableLoop> buildFrom(Collection<While> loops, Collection<TestCase> failed, Collection<TestCase> passed, MonitoringTestExecutor executor) {
		logDebug(logger, "Running test cases to count number of invocations in:", loops.toString());
		Table<While, TestCase, Integer> failingTestInvocations = executor.invocationsPerTest(loops, failed);
		Table<While, TestCase, Integer> passingTestInvocations = executor.invocationsPerTest(loops, passed);
		Collection<FixableLoop> fixableLoops = MetaSet.newHashSet();
		for (While loop : loops) {
			addIfFixable(loop, fixableLoops, failingTestInvocations.row(loop), passingTestInvocations.row(loop));
		}
		return fixableLoops;
	}
	
	private static void addIfFixable(While loop, Collection<FixableLoop> fixableLoops, Map<TestCase, Integer> failing, Map<TestCase, Integer> passing) {
		Collection<TestCase> failingInvoking = testsExtractedForRepair(failing);
		Collection<TestCase> passingInvoking = testsExtractedForRepair(passing);
		if (failingInvoking.size() + passingInvoking.size() > 0) {
			fixableLoops.add(new FixableLoop(loop, failingInvoking, passingInvoking));
		} else {
			logDebug(logger, format("Unfixable loop (%s)", loop.toString()));
		}
	}
	
	private static Collection<TestCase> testsExtractedForRepair(Map<TestCase, Integer> invocations) {
		Collection<TestCase> nonInvoking = MetaMap.keysWithValue(0, invocations);
		Collection<TestCase> invokingOnce = MetaMap.keysWithValue(1, invocations);
		if (nonInvoking.size() + invokingOnce.size() == invocations.size()) {
			return invokingOnce;
		}
		return MetaList.newArrayList();
	}
	
	private static Logger logger = newLoggerFor(FixableLoopBuilder.class); 
}