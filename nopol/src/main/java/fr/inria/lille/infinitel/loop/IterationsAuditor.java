package fr.inria.lille.infinitel.loop;

import java.util.Collection;
import java.util.Map;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.factory.CodeFactory;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.spoon.SpoonLibrary;

public class IterationsAuditor {

	public static CtExpression<Boolean> iterationTrackingCondition(CtWhile loopStatement) {
		String codeSnippet = canonicalName() + ".signalConditionEvaluation()";
		return SpoonLibrary.composedExpression(codeSnippet, BinaryOperatorKind.AND, loopStatement.getLoopingExpression());
	}

	public static CtStatement loopTerminationHint(CtWhile loopStatement, int loopID) {
		String codeSnippet = canonicalName() + String.format(".signalLoopEnd(%d)", loopID);
		return SpoonLibrary.statementFrom(codeSnippet, loopStatement.getParent());
	}
	
	public static boolean signalConditionEvaluation() {
		return instance().recordNewIteration();
	}
	
	public static void signalLoopEnd(int loopID) {
		instance().updateMaximumIterationsOf(loopID);
		instance().resetRecordedIterations();
	}
	
	public static Collection<Integer> infiniteLoopIDs() {
		return instance().loopIDsOverThreshold();
	}
	
	public static String canonicalName() {
		return instance().getClass().getCanonicalName();
	}
	
	private static IterationsAuditor instance() {
		if (instance == null) {
			instance = new IterationsAuditor(1E6);
		}
		return instance;
	}
	
	private IterationsAuditor(Number iterationsLimit) {
		threshold = iterationsLimit.intValue();
		maximumIterations = MapLibrary.newHashMap();
		resetRecordedIterations();
	}
	
	private void updateMaximumIterationsOf(int loopID) {
		int oldValue = MapLibrary.getPutIfAbsent(maximumIterations(), loopID, 0);
		maximumIterations().put(loopID, Math.max(recordedIterations(), oldValue));
	}
	
	private Collection<Integer> loopIDsOverThreshold() {
		Collection<Integer> ids = SetLibrary.newHashSet();
		for (Integer loopID : maximumIterations().keySet()) {
			if (reachesThreshold(loopID)) {
				ids.add(loopID);
			}
		}
		return ids;
	}
	
	private boolean reachesThreshold(Integer loopID) {
		return maximumIterations().get(loopID) == threshold();
	}

	private boolean recordNewIteration() {
		recordedIterations += 1;
		return ! thresholdReached();
	}
	
	private boolean thresholdReached() {
		return recordedIterations() == threshold();
	}

	private void resetRecordedIterations() {
		recordedIterations = 0;
	}
	
	private Map<Integer, Integer> maximumIterations() {
		return maximumIterations;
	}

	private int recordedIterations() {
		return recordedIterations;
	}
	
	private int threshold() {
		return threshold;
	}
	
	private int threshold;
	private int recordedIterations;
	private Map<Integer, Integer> maximumIterations;
	
	private static IterationsAuditor instance;
}
