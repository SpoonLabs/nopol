package fr.inria.lille.infinitel.loop;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.spoon.SpoonLibrary;

public class IterationsAuditor {

	public static void attachTo(CtWhile loopStatement, int loopID) {
		String variableName = counterVariableName(loopID);
		CtStatement beforeStatement = beforeLoopStatement(loopStatement, variableName);
		CtExpression<Boolean> modifiedLoopingExpression = iterationTrackingCondition(loopStatement, variableName);
		CtStatement afterStatement = afterLoopStatement(loopStatement, variableName, loopID);
		loopStatement.insertBefore(beforeStatement);
		loopStatement.setLoopingExpression(modifiedLoopingExpression);
		loopStatement.insertAfter(afterStatement);
	}
	
	private static String counterVariableName(int loopID) {
		return String.format("loopIterations_%d", loopID);
	}
	
	private static CtStatement beforeLoopStatement(CtWhile loopStatement, String variableName) {
		String codeSnippet = String.format("int %s = 0", variableName);
		return SpoonLibrary.statementFrom(codeSnippet, loopStatement.getParent());
	}
	
	private static CtExpression<Boolean> iterationTrackingCondition(CtWhile loopStatement, String variableName) {
		String codeSnippet = canonicalName() + String.format(".instance().threshold() > (++%s)", variableName);
		return SpoonLibrary.composedExpression(codeSnippet, BinaryOperatorKind.AND, loopStatement.getLoopingExpression());
	}

	private static CtStatement afterLoopStatement(CtWhile loopStatement, String variableName, int loopID) {
		String codeSnippet = canonicalName() + String.format(".signalLoopEnd(%s, %d)", variableName, loopID);
		return SpoonLibrary.statementFrom(codeSnippet, loopStatement.getParent());
	}
	
	public static void signalLoopEnd(int iterations, int loopID) {
		instance().addRecordOf(iterations, loopID);
	}
	
	public static Collection<Integer> infiniteLoopIDs() {
		return instance().loopIDsOverThreshold();
	}
	
	public static String canonicalName() {
		return instance().getClass().getCanonicalName();
	}
	
	public static IterationsAuditor instance() {
		if (instance == null) {
			instance = new IterationsAuditor();
		}
		return instance;
	}
	
	private IterationsAuditor() {
		loopIterationsRecord = MapLibrary.newHashMap();
		resetThreshold();
	}
	
	private void addRecordOf(int iterations, int loopID) {
		List<Integer> loopRecord = MapLibrary.getPutIfAbsent(iterationsRecord(), loopID, (List) ListLibrary.newArrayList());
		loopRecord.add(iterations);
	}
	
	private Collection<Integer> loopIDsOverThreshold() {
		Collection<Integer> ids = SetLibrary.newHashSet();
		for (Integer loopID : iterationsRecord().keySet()) {
			if (reachesThreshold(loopID)) {
				ids.add(loopID);
			}
		}
		return ids;
	}
	
	private boolean reachesThreshold(Integer loopID) {
		return iterationsRecord().get(loopID).contains(threshold());
	}

	private Map<Integer, List<Integer>> iterationsRecord() {
		return loopIterationsRecord;
	}
	
	public int threshold() {
		return threshold;
	}
	
	public void setThreshold(Number number) {
		threshold = number.intValue();
	}
	
	public void resetThreshold() {
		setThreshold(1E6);
	}
	
	private int threshold;
	private Map<Integer, List<Integer>> loopIterationsRecord;
	
	private static IterationsAuditor instance;
}
